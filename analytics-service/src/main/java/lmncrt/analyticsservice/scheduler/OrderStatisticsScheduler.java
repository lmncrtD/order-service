package lmncrt.analyticsservice.scheduler;

import lmncrt.analyticsservice.service.OrderStatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class OrderStatisticsScheduler {

    private final OrderStatisticsService orderStatisticsService;

    @Scheduled(fixedDelayString = "${analytics.statistics.refresh-delay}")
    @SchedulerLock(name = "analytics-refresh-statitics-schedlock")
    public void runRefreshStatistics() {
        log.warn("Обновление статистики");
        orderStatisticsService.refreshStatistics();
    }
}
