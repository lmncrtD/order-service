package lmncrt.orderservice.exceptions;

import lmncrt.orderservice.service.OrderStatus;

public class OrderStatusException extends RuntimeException {

    public OrderStatusException(OrderStatus status) {
        super("Нельзя изменить заказ, если статус заказа = " + status);
    }

}
