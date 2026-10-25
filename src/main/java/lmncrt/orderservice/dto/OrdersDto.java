package lmncrt.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lmncrt.orderservice.entity.ItemsEntity;
import lmncrt.orderservice.service.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdersDto(

    @Null
    Long id,

    @NotNull(message = "Необходимо передать ID пользователя")
    @Positive(message = "ID не должен быть отрицательным")
    Long userId,

    @Null
    LocalDate date,

    @Null
    OrderStatus status,

    @NotEmpty(message = "При создании заказа добавьте хотя бы один товар")
    List<@NotNull @Valid ItemsDto> items,

    @Null
    BigDecimal totalAmount

) {
}
