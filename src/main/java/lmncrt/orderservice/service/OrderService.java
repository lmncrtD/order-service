package lmncrt.orderservice.service;

import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrdersRepository ordersRepository;

    public OrderService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<OrdersEntity> getOrders() {
        return ordersRepository.findAllOrders();
    }

}
