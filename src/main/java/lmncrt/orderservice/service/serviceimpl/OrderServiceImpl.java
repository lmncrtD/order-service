package lmncrt.orderservice.service.serviceimpl;

import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import lmncrt.orderservice.api.OrderController;
import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.mapper.OrdersMapper;
import lmncrt.orderservice.repository.ItemsRepository;
import lmncrt.orderservice.repository.OrdersRepository;
import lmncrt.orderservice.service.OrderService;
import lmncrt.orderservice.service.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final ItemsRepository itemsRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(
            OrdersRepository ordersRepository,
            OrdersMapper ordersMapper,
            ItemsRepository itemsRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.itemsRepository = itemsRepository;
    }

    // Поиск всех заказов
    @Override
    public List<OrdersDto> getOrders() {
        List<OrdersEntity> entities = ordersRepository.findAll();
        return ordersMapper.toDtoList(entities);
    }

    // Поиск заказов по id заказа
    @Override
    public OrdersDto findById(Long id) {
        OrdersEntity entity = ordersRepository.findbById(id);
        return ordersMapper.toDto(entity);
    }

    // Поиск всех заказов по id клиента
    @Override
    public List<OrdersDto> findAllOrdersByUserId(Long id) {
        List<OrdersEntity> entity = ordersRepository.findAllOrdersByUserId(id);
        return ordersMapper.toDtoList(entity);
    }

    // Расчет общей стоимости для конкретного заказа
    @Override
    public BigDecimal calculateTotalAmountByOrderId(Long id){
        OrdersEntity order = ordersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));
        List<ItemsEntity> items = order.getItems();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemsEntity item : items){
            totalAmount = totalAmount.add(item.getItemAmount());
        }

        order.setTotalAmount(totalAmount);
        ordersRepository.save(order);

        return totalAmount;
    }

    // Создание нового заказа
    @Override
    public OrdersDto createOrder(OrdersDto order) {
        OrdersEntity entity = ordersMapper.toEntity(order);

        entity.setCreatedAt(LocalDate.now());
        entity.setStatus(OrderStatus.CREATED);
        entity.setTotalAmount(BigDecimal.ZERO);

        List<ItemsEntity> loadedItems = new ArrayList<>();
        List<ItemsEntity> items = entity.getItems();
        for (ItemsEntity item : items){
            loadedItems.add(itemsRepository.findItemsById(item.getId()));
        }
        entity.setItems(loadedItems);

        ordersRepository.save(entity);

        BigDecimal total = calculateTotalAmountByOrderId(entity.getId());
        entity.setTotalAmount(total);

        return findById(entity.getId());
    }



}
