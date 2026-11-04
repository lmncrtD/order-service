package lmncrt.analyticsservice.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderStatisticsDto(
        Instant calculatedAt,
        Long countOrders,
        Long createdOrders,
        Long completedOrders,
        Long cancelledOrders,
        BigDecimal totalAmountCompletedOrders,
        BigDecimal avgAmountOrders
) {
}
