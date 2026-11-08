package lmncrt.analyticsservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record OrderStatisticsDto(
        Instant calculatedAt,
        Long countOrders,
        Long createdOrders,
        Long completedOrders,
        Long cancelledOrders,
        BigDecimal totalAmountCompletedOrders,
        BigDecimal avgAmountOrders
) {

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        OrderStatisticsDto dto = (OrderStatisticsDto) o;

        return Objects.equals(countOrders, dto.countOrders)
                && Objects.equals(createdOrders, dto.createdOrders)
                && Objects.equals(completedOrders, dto.completedOrders)
                && Objects.equals(cancelledOrders, dto.cancelledOrders)
                && totalAmountCompletedOrders.compareTo(dto.totalAmountCompletedOrders) == 0
                && avgAmountOrders.compareTo(dto.avgAmountOrders) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                countOrders,
                createdOrders,
                completedOrders,
                cancelledOrders,
                totalAmountCompletedOrders.stripTrailingZeros(),
                avgAmountOrders.stripTrailingZeros()
        );
    }
}
