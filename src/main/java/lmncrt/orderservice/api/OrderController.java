package lmncrt.orderservice.api;

import jakarta.validation.Valid;
import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.dto.PatchOrderDto;
import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.service.serviceimpl.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderServiceImpl orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    // Получение списка заказов
    @GetMapping
    public Page<OrdersDto> getOrders(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "2") int pageSize

    ) {
        log.info("Вызван метод получения списка заказов");
        Page<OrdersDto> allOrders = orderService.getOrders(PageRequest.of(pageNumber, pageSize));
        return allOrders;

    }

    // Получение заказа по id
    @GetMapping(value = "/{id}")
    public OrdersDto getOrderById(
            @PathVariable Long id
    ) {
        OrdersDto findOrder = orderService.findById(id);
        log.info("Вызван метод получения заказа по ID: {}", id);
        return findOrder;
    }

    // Получение заказов конкретного клиента по его id
    @GetMapping(value = "/client/{id}")
    public List<OrdersDto> getClientOrdersById(
            @PathVariable Long id
    ) {
        List<OrdersDto> findOrderByUserId = orderService.findAllOrdersByUserId(id);
        log.info("Вызов метода получения заказов конкретного пользователя по ID: {}", id);
        return findOrderByUserId;
    }

    // Запрос на создание нового заказа
    @PostMapping
    public OrdersDto createOrder(
            @RequestHeader("X-Request-ID") UUID idempotencyKey,
            @Valid @RequestBody OrdersDto order
    ) {

        OrdersDto clientOrder = orderService.createOrder(idempotencyKey, order);
        log.info("Вызван метод создания нового заказа, результат = {}", clientOrder);
        return clientOrder;

    }

    @PatchMapping(value = "/{id}")
    public OrdersDto patchOrder (
            @RequestHeader("X-Request-ID") UUID idempotencyKey,
            @PathVariable Long id,
            @Valid @RequestBody PatchOrderDto patch
    ) {
        return orderService.patchOrder(idempotencyKey, id, patch);
    }

    @DeleteMapping(value = "/{id}")
    public OrdersDto deleteOrder(
            @PathVariable Long id
    ) {
        return orderService.deleteOrder(id);
    }

}
