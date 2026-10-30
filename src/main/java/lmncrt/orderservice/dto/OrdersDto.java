package lmncrt.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.service.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrdersDto(

    Long id,

    @NotNull(message = "Необходимо передать ID пользователя")
    @Positive(message = "ID не должен быть отрицательным")
    Long userId,

    Instant date,

    OrderStatus status,

    @Size(max = 100)
    @NotNull(message = "При создании заказа добавьте хотя бы один товар")
    List<@NotNull @Valid ItemsDto> items,

    @Null
    BigDecimal totalAmount


) {
}
