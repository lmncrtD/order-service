package lmncrt.analyticsservice.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEventEntity(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        UUID orderId,
        Long userId,
        Instant createdAt,
        String status,
        BigDecimal totalAmount
) {
}
