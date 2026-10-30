package lmncrt.orderservice.service.serviceimpl;


import jakarta.validation.Valid;
import lmncrt.orderservice.dto.*;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.entity.OrdersIdempotencyEntity;
import lmncrt.orderservice.entity.OutboxEntity;
import lmncrt.orderservice.event.OrderEventType;
import lmncrt.orderservice.exceptions.*;
import lmncrt.orderservice.mapper.OrdersMapper;
import lmncrt.orderservice.repository.ItemsRepository;
import lmncrt.orderservice.repository.OrderIdempotencyRepository;
import lmncrt.orderservice.repository.OrdersRepository;
import lmncrt.orderservice.repository.OutboxRepository;
import lmncrt.orderservice.service.OrderOperations;
import lmncrt.orderservice.service.OrderService;
import lmncrt.orderservice.service.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final ItemsRepository itemsRepository;
    private final OrderIdempotencyRepository orderIdempotencyRepository;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;

    // Поиск всех заказов
    @Override
    public Page<OrdersDto> getOrders(PageRequest pageRequest) {
        Page<OrdersEntity> page = ordersRepository.findAll(pageRequest);
        return page.map(ordersMapper::toDto);
    }

    // Поиск заказов по id заказа
    @Override
    public OrdersDto findById(Long id) {
        OrdersEntity entity = ordersRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Заказ под таким ID = {}, не найден", id);

                    return new OrderNotFoundException(
                            id
                    );
                });
        return ordersMapper.toDto(entity);
    }

    // Поиск всех заказов по id клиента
    @Override
    public List<OrdersDto> findAllOrdersByUserId(Long id) {
        List<OrdersEntity> entity = ordersRepository.findAllOrdersByUserId(id);
        if(entity.isEmpty()) {
            throw new ClientNotFoundException(
                    id
            );
        }
        return ordersMapper.toDtoList(entity);
    }

    // Передавать для расчета не ID заказа, а список items
    // Расчет общей стоимости для конкретного заказа
    @Override
    public BigDecimal calculateTotalAmount(List<ItemsEntity> items){

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemsEntity item : items){
            totalAmount = totalAmount.add(item.getItemAmount());
        }

        return totalAmount;
    }

    public List<ItemsEntity> searchItems(List<Long> itemIds) {

        List<Long> uniqueIds = itemIds
                .stream()
                .distinct()
                .toList();

        List<ItemsEntity> loadedItems;
        if(uniqueIds.isEmpty()) {
            log.warn("Список товаров пуст");
            throw new ItemNotFoundException();
        }
        loadedItems = itemsRepository.findAllById(uniqueIds);
        if (uniqueIds.size() != loadedItems.size()) {
            log.warn("Часть товаров не найдена");
            throw new SomeItemsNotFoundException();
        }

        return loadedItems;
    }

    // Создание нового заказа
    @Transactional
    @Override
    public OrdersDto createOrder(
            UUID idempotencyKey,
            @Valid OrdersDto order
    ) {
        OrdersEntity entity = ordersMapper.toEntity(order);

        String request_text = objectMapper.writeValueAsString(
                new BodyRequestIdempotencyDto<>(OrderOperations.CREATE_ORDER, null, order)
        );

        int isRegister =
                orderIdempotencyRepository.tryRegister(
                        idempotencyKey,
                        request_text
                );

        if (isRegister == 0) {
            OrdersIdempotencyEntity findIdempotencyKey = findIdempotencyKey(idempotencyKey);

            if(findIdempotencyKey.getRequestText().equals(request_text)){
                log.warn("Запрос с ключом идемпотентности = {} уже существует", idempotencyKey);
                return findAlredyRegistryKey(idempotencyKey);

            } else {
                throw new OrderAlreadyRegisteredException(idempotencyKey);
            }
        }

        entity.setCreatedAt(Instant.now());
        entity.setStatus(OrderStatus.CREATED);
        entity.setTotalAmount(BigDecimal.ZERO);
        List<ItemsEntity> items = entity.getItems();

        List<Long> itemIds = items.stream()
                .map(ItemsEntity::getId)
                .distinct()
                .toList();

        List<ItemsEntity> loadedItems = searchItems(itemIds);

        entity.setItems(loadedItems);
        BigDecimal total = calculateTotalAmount(loadedItems);
        entity.setTotalAmount(total);

        OrdersEntity save = ordersRepository.save(entity);
        int isNewIdempotencyLink = orderIdempotencyRepository
                .linkOrder(
                        idempotencyKey,
                        save.getId()
                );
        if (isNewIdempotencyLink == 0) {
            log.warn("Ключ идемпотентности не найден");
        } else {
            log.info("Ключ идемпотентности = {} для заказа с id = {} создан", idempotencyKey, save.getId());
        }

        OrderEventDto orderEvent = new OrderEventDto(
                UUID.randomUUID(),
                OrderEventType.ORDER_CREATED,
                Instant.now(),
                save.getId(),
                save.getUserId(),
                save.getCreatedAt(),
                save.getStatus(),
                save.getTotalAmount()
        );

        String eventJson = objectMapper.writeValueAsString(orderEvent);

        OutboxEntity outboxEntity = new OutboxEntity(
                orderEvent.eventId(),
                orderEvent.occurredAt(),
                orderEvent.orderId(),
                eventJson,
                null
        );

        outboxRepository.save(outboxEntity);

        return ordersMapper.toDto(save);
    }

    // Поиск и получение данных по id ключа идемпотентности
    public OrdersIdempotencyEntity findIdempotencyKey(UUID idempotencyKey) {
        return orderIdempotencyRepository.findByRequestKey(idempotencyKey);
    }

    // поиск уже зарегистрированного ключа
    public OrdersDto findAlredyRegistryKey(UUID idempotencyKey) {

        OrdersIdempotencyEntity idempotency = orderIdempotencyRepository.findByRequestKey(idempotencyKey);
        Long order_id = idempotency.getOrderId();

        OrdersEntity orderEntity = ordersRepository
                .findById(order_id)
                .orElseThrow(
                        () -> new OrderNotFoundException(order_id)
                );

        log.info("Ключ идемпотентности = {} уже существует и найден у заказа = {}",
                idempotencyKey,
                orderEntity.getId()
        );

        return  ordersMapper.toDto(orderEntity);

    }

    // изменение заказа; Новый статус, дата или добавление товаров
    @Transactional
    public OrdersDto patchOrder(
            UUID idempotencyKey,
            Long id,
            @Valid PatchOrderDto patch
    ) {

        OrdersEntity order = ordersRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus currentStatus = order.getStatus();

        String request_text = objectMapper.writeValueAsString(
                new BodyRequestIdempotencyDto<>(OrderOperations.PATCH_ORDER, id, patch)
        );

        int isReallyNewUpdate = 0;

        int isRegister =
                orderIdempotencyRepository.tryRegister(
                        idempotencyKey,
                        request_text
                );

        if (isRegister == 0){

            OrdersIdempotencyEntity findIdempotencyKey = findIdempotencyKey(idempotencyKey);

            if(findIdempotencyKey.getRequestText().equals(request_text)){
                log.warn("Запрос с ключом идемпотентности = {} уже существует", idempotencyKey);
                return findAlredyRegistryKey(idempotencyKey);

            } else {
                throw new OrderAlreadyRegisteredException(idempotencyKey);
            }

        }

        if (patch.status() != null && !patch.status().equals(order.getStatus())) {
            if (!currentStatus.equals(OrderStatus.CREATED)) {
                throw new OrderStatusException(currentStatus);
            }
            isReallyNewUpdate += 1;
            order.setStatus(patch.status());
        }

        if (patch.date() != null && currentStatus.equals(OrderStatus.CREATED) && !patch.date().equals(order.getCreatedAt())) {
            isReallyNewUpdate += 1;
            order.setCreatedAt(patch.date());
        }

        if (patch.itemIdsToAdd() != null && currentStatus.equals(OrderStatus.CREATED)) {
            Set<Long> idsToAdd = new HashSet<>(patch.itemIdsToAdd());

            for (ItemsEntity item : order.getItems()) {
                idsToAdd.remove(item.getId());
            }

            if (!idsToAdd.isEmpty()) {
                isReallyNewUpdate += 1;
                List<ItemsEntity> newItems = searchItems(new ArrayList<>(idsToAdd));
                order.getItems().addAll(newItems);

                order.setTotalAmount(calculateTotalAmount(order.getItems()));
            }
        } else if (currentStatus != OrderStatus.CREATED && patch.itemIdsToAdd() != null) {
            throw new OrderStatusException(currentStatus);
        }

        int isNewIdempotencyLink = orderIdempotencyRepository
                .linkOrder(
                        idempotencyKey,
                        order.getId()
                );
        if (isNewIdempotencyLink == 0) {
            log.warn("Ключ идемпотентности не найден");
        } else {
            log.info("Ключ идемпотентности = {} для заказа с id = {} создан", idempotencyKey, order.getId());
        }

        if (isReallyNewUpdate > 0) {
            OrdersEntity save = ordersRepository.save(order);

            OrderEventDto orderEvent = new OrderEventDto(
                    UUID.randomUUID(),
                    OrderEventType.ORDER_UPDATED,
                    Instant.now(),
                    save.getId(),
                    save.getUserId(),
                    save.getCreatedAt(),
                    save.getStatus(),
                    save.getTotalAmount()
            );

            String eventJson = objectMapper.writeValueAsString(orderEvent);

            OutboxEntity outboxEntity = new OutboxEntity(
                    orderEvent.eventId(),
                    orderEvent.occurredAt(),
                    orderEvent.orderId(),
                    eventJson,
                    null
            );

            outboxRepository.save(outboxEntity);
        }

        return ordersMapper.toDto(order);
    }

    // удаление заказа
    @Transactional
    public OrdersDto deleteOrder(
            Long id
    ) {
        OrdersEntity order = ordersRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() ==  OrderStatus.CREATED) {
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            throw new OrderStatusException(order.getStatus());
        }

        OrderEventDto orderEvent = new OrderEventDto(
                UUID.randomUUID(),
                OrderEventType.ORDER_CANCELLED,
                Instant.now(),
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount()
        );

        String eventJson = objectMapper.writeValueAsString(orderEvent);

        OutboxEntity outboxEntity = new OutboxEntity(
                orderEvent.eventId(),
                orderEvent.occurredAt(),
                orderEvent.orderId(),
                eventJson,
                null
        );

        outboxRepository.save(outboxEntity);


        return ordersMapper.toDto(order);
    }
}
