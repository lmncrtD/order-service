package lmncrt.orderservice.dto;

import lmncrt.orderservice.event.OrderEventType;
import lmncrt.orderservice.service.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OrderEventDto(
        UUID eventId,
        OrderEventType eventType,
        Instant occurredAt,
        Long orderId,
        Long userId,
        Instant createdAt,
        OrderStatus status,
        BigDecimal totalAmount
) {
}
