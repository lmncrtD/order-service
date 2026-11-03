package lmncrt.analyticsservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEventDto(
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
