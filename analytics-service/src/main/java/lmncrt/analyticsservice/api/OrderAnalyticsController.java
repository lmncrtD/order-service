package lmncrt.analyticsservice.api;

import jakarta.websocket.server.PathParam;
import lmncrt.analyticsservice.dto.OrderStatisticsByUserDto;
import lmncrt.analyticsservice.dto.OrderStatisticsDto;
import lmncrt.analyticsservice.service.OrderStatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@Slf4j
@AllArgsConstructor
public class OrderAnalyticsController {

    private final OrderStatisticsService orderStatisticsService;

    @GetMapping
    public OrderStatisticsDto getOrderStatistics() {

        log.info("Метод получения общей статистики");
        return orderStatisticsService.findLastAnalytics();

    }

    @GetMapping("/period")
    public OrderStatisticsDto getOrderStatisticsByPeriod(
            @RequestParam(name = "from", required = false) LocalDate dateFrom,
            @RequestParam(name = "to", required = false) LocalDate dateTo
    ) {
        return orderStatisticsService.findAnalyticsByPeriod(dateFrom, dateTo);
    }

    @GetMapping("/{id}")
    public OrderStatisticsByUserDto getOrdersStatisticsByUser(
            @PathVariable Long id
    ) {
        return orderStatisticsService.findOrderStatisticsByUser(id);
    }

}
