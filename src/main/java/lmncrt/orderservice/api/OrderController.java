package lmncrt.orderservice.api;

import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getMain() {
        return "Order Service";
    }

    @GetMapping(value = "/get")
    public List<OrdersEntity> getOrders() {
        return orderService.getOrders();
    }

}
