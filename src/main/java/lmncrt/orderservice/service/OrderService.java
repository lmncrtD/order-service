package lmncrt.orderservice.service;


import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.entity.OrdersEntity;
import lmncrt.orderservice.entity.OrdersIdempotencyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    Page<OrdersDto> getOrders(PageRequest pageRequest);

    BigDecimal calculateTotalAmount(List<ItemsEntity> items);

    OrdersDto findById(Long id);

    List<OrdersDto> findAllOrdersByUserId(Long id);

    OrdersDto createOrder(UUID idempotencyKey, OrdersDto order);



}
