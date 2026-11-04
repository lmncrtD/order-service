package lmncrt.analyticsservice.service;

import lmncrt.analyticsservice.dto.OrderStatisticsByUserDto;
import lmncrt.analyticsservice.dto.OrderStatisticsDto;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderStatisticsService {

    void refreshStatistics();

    OrderStatisticsDto findLastAnalytics();

    OrderStatisticsDto findAnalyticsByPeriod(LocalDate dateFrom, LocalDate dateTo);

    OrderStatisticsByUserDto findOrderStatisticsByUser(Long user_id);
}
