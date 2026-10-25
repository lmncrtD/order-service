package lmncrt.orderservice.service;


import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.entity.OrdersEntity;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    List<OrdersDto> getOrders();

    BigDecimal calculateTotalAmountByOrderId(Long id);

    OrdersDto findById(Long id);

    List<OrdersDto> findAllOrdersByUserId(Long id);

    OrdersDto createOrder(OrdersDto order);



}
