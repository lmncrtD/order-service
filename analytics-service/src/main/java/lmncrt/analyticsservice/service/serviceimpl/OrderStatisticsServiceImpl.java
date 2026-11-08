package lmncrt.analyticsservice.service.serviceimpl;

import lmncrt.analyticsservice.dto.OrderStatisticsByUserDto;
import lmncrt.analyticsservice.dto.OrderStatisticsDto;
import lmncrt.analyticsservice.exceptions.AnalyticsException;
import lmncrt.analyticsservice.repository.OrderStatisticsRepository;
import lmncrt.analyticsservice.service.OrderStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;

    // Обновление витрины
    @Override
    public void refreshStatistics() {

        OrderStatisticsDto newCalculate = orderStatisticsRepository.calculateAnalytics().orElseThrow(
                () -> new AnalyticsException(
                        "ANALYTICS_NOT_FOUND",
                        "Analytics data is not found",
                        HttpStatus.NOT_FOUND
                )
        );

        Optional<OrderStatisticsDto> lastCalculate = orderStatisticsRepository.findLastAnalytics();

        if (lastCalculate.isEmpty()) {
            orderStatisticsRepository.saveAnalytics(newCalculate);
            return;
        }

        boolean isSameMetricsEquals = hasSameMetrics(lastCalculate.get(), newCalculate);

        if (!isSameMetricsEquals) {
            orderStatisticsRepository.saveAnalytics(newCalculate);
        }
    }

    // Вывод последнего актуального расчета статистики по заказам из витрины
    @Override
    public OrderStatisticsDto findLastAnalytics() {

        var lastAnalytics = orderStatisticsRepository.findLastAnalytics().orElseThrow(
                () -> new AnalyticsException(
                        "ANALYTICS_NOT_FOUND",
                        "Analytics data is not found",
                        HttpStatus.NOT_FOUND)
        );

        return lastAnalytics;

    }

    // Аналитика за период
    @Override
    public OrderStatisticsDto findAnalyticsByPeriod(LocalDate dateFrom, LocalDate dateTo) {

        if (dateFrom == null) {
            dateFrom = LocalDate.of(LocalDate.now(ZoneOffset.UTC).getYear(), 1, 1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.of(LocalDate.now(ZoneOffset.UTC).getYear() + 1, 1, 1);
        }
        if(!dateFrom.isBefore(dateTo)) {
            throw new AnalyticsException(
                    "DATE_FROM_BETWEEN",
                    "Uncorrected date from between dateTo",
                    HttpStatus.BAD_REQUEST
            );
        }

        return orderStatisticsRepository.findLastAnalyticsByPeriod(dateFrom, dateTo).orElseThrow(
                () -> new AnalyticsException(
                        "ANALYTICS_NOT_FOUND_BY_PERIOD",
                        "Analytics data is not found by period",
                        HttpStatus.NOT_FOUND
                )
        );
    }

    // Статистика по заказам по конкретному клиенту, если по клиенту ничего не найдем, вернем 200 без тела
    @Override
    public OrderStatisticsByUserDto findOrderStatisticsByUser(Long user_id) {
        var userAnalytics = orderStatisticsRepository.findAnalyticsByUser(user_id).orElse(null);

        return userAnalytics;
    }

    // Сравниваем два снимка. Прошлый и тот, что пришел.
    private boolean hasSameMetrics(OrderStatisticsDto oldDto, OrderStatisticsDto newDto) {
        return oldDto.equals(newDto);
    }
}
