package lmncrt.analyticsservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderStatisticsByUserDto(
        Instant calculatedAt,
        Long user_id,
        Long countOrders,
        Long createdOrders,
        Long completedOrders,
        Long cancelledOrders,
        BigDecimal totalAmountCompletedOrders,
        BigDecimal avgAmountOrders
) {
}
