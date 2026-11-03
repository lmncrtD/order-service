package lmncrt.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lmncrt.orderservice.service.OrderStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PatchOrderDto(
        OrderStatus status,
        Instant date,
        @Size(max = 100)
        List<@NotNull @Positive Long> itemIdsToAdd
) {}
