package lmncrt.orderservice.api;

import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.service.serviceimpl.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class OrderController {

    private final OrderServiceImpl orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getMain() {
        return "Order Service";
    }

    // Получение списка заказов
    @GetMapping(value = "/orders")
    public List<OrdersDto> getOrders() {
        log.info("Вызван метод получения списка заказов");
        return orderService.getOrders();
    }

    // Получение заказа по id
    @GetMapping(value = "/orders/{id}")
    public OrdersDto getOrderById(
            @PathVariable Long id
    ) {
        log.info("Вызван метод получения заказа по ID: {}", id);
        return orderService.findById(id);
    }

    // Получение заказов конкретного клиента по его id
    @GetMapping(value = "/orders/client/{id}")
    public List<OrdersDto> getClientOrdersById(
            @PathVariable Long id
    ) {
        log.info("Вызов метода получения заказов конкретного пользователя по ID: {}", id);
        return orderService.findAllOrdersByUserId(id);
    }

    // Запрос на создание нового заказа
    @PostMapping("/orders")
    public void createOrder(
            @RequestBody OrdersDto order
    ) {
        OrdersDto clientOrder = orderService.createOrder(order);
        log.info("Вызван метод создания нового заказа, результат = {}", clientOrder);
    }

    //

    @GetMapping(value = "/get/{id}/calc")
    public OrdersDto getCalcAmountOrderById(@PathVariable Long id) {
        orderService.calculateTotalAmountByOrderId(id);
        return orderService.findById(id);
    }

}
