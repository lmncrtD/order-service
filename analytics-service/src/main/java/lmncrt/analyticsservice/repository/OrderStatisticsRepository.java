package lmncrt.analyticsservice.repository;

import lmncrt.analyticsservice.dto.OrderStatisticsByUserDto;
import lmncrt.analyticsservice.dto.OrderStatisticsDto;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class OrderStatisticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<OrderStatisticsDto> calculateAnalytics() {
        var calculatedAnaytics =
                jdbcTemplate.query(
                        """
                                select now() as calculated_at ,
                                       count() as count_orders,
                                       countIf(status = 'CREATED') as created_orders,
                                       countIf(status = 'COMPLETED') as completed_orders,
                                       countIf(status = 'CANCELLED') as cancelled_orders,
                                       sumIf(total_amount, status = 'COMPLETED') as total_amount_completed_orders,
                                       if(count_orders = 0, 0, avg(total_amount)) as average_amount_orders
                                from
                                    (select oe.order_id,
                                            oe.status,
                                            oe.occurred_at,
                                            oe.total_amount
                                     from order_events oe
                                     order by oe.order_id, oe.occurred_at desc
                                     LIMIT 1 BY oe.order_id
                                        );
                            """,
                        (rs, rowNum) -> new OrderStatisticsDto(
                                rs.getTimestamp("calculated_at").toInstant(),
                                rs.getLong("count_orders"),
                                rs.getLong("created_orders"),
                                rs.getLong("completed_orders"),
                                rs.getLong("cancelled_orders"),
                                rs.getBigDecimal("total_amount_completed_orders"),
                                rs.getBigDecimal("average_amount_orders")
                        )
                );

        return calculatedAnaytics.stream().findFirst();
    }

    public void saveAnalytics (OrderStatisticsDto orderStatisticsDto) {

        jdbcTemplate.update(
                """
                        INSERT INTO analytics.order_statistics(
                             calculated_at,
                             count_orders,
                             created_orders,
                             completed_orders,
                             cancelled_orders,
                             total_amount_completed_orders,
                             average_amount_orders
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?);
                    """,
                Timestamp.from(orderStatisticsDto.calculatedAt()),
                orderStatisticsDto.countOrders(),
                orderStatisticsDto.createdOrders(),
                orderStatisticsDto.completedOrders(),
                orderStatisticsDto.cancelledOrders(),
                orderStatisticsDto.totalAmountCompletedOrders(),
                orderStatisticsDto.avgAmountOrders()
                );


    }

//    public void refreshAnalytics () throws IOException {
//        ClassPathResource res = new ClassPathResource("sql/insert-refresh-order-statistics.sql");
//        var contentSql = res.getContentAsString(StandardCharsets.UTF_8);
//        jdbcTemplate.execute(contentSql);
//    }

    public Optional<OrderStatisticsDto> findLastAnalytics () {
        var analytics = jdbcTemplate.query(
                """
                        select * from analytics.order_statistics
                        order by calculated_at desc
                        limit 1;
                    """, (rs, rowNum) -> new OrderStatisticsDto(
                            rs.getTimestamp("calculated_at").toInstant(),
                            rs.getLong("count_orders"),
                            rs.getLong("created_orders"),
                            rs.getLong("completed_orders"),
                            rs.getLong("cancelled_orders"),
                            rs.getBigDecimal("total_amount_completed_orders"),
                            rs.getBigDecimal("average_amount_orders")
                )
        );

        return  analytics.stream().findFirst();

    }

    public Optional<OrderStatisticsDto> findLastAnalyticsByPeriod(
            LocalDate dateFrom,
            LocalDate dateTo
    ) {

        Instant fromInstant = dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant toInstant = dateTo.atStartOfDay(ZoneOffset.UTC).toInstant();

        var analyticsByPeriod = jdbcTemplate.query(
                """
                        select now() as calculated_at ,
                               count() as count_orders,
                               countIf(status = 'CREATED') as created_orders,
                               countIf(status = 'COMPLETED') as completed_orders,
                               countIf(status = 'CANCELLED') as cancelled_orders,
                               sumIf(total_amount, status = 'COMPLETED') as total_amount_completed_orders,
                               if(count_orders = 0, 0, avg(total_amount)) as average_amount_orders
                        from
                            (select oe.order_id,
                                    oe.created_at,
                                    oe.status,
                                    oe.occurred_at,
                                    oe.total_amount
                             from order_events oe
                             order by oe.order_id, oe.occurred_at desc
                             LIMIT 1 BY oe.order_id)
                        where created_at >= ?
                          and created_at < ?
                        ;
                    """,
                (rs, rowNum) -> new OrderStatisticsDto(
                        rs.getTimestamp("calculated_at").toInstant(),
                        rs.getLong("count_orders"),
                        rs.getLong("created_orders"),
                        rs.getLong("completed_orders"),
                        rs.getLong("cancelled_orders"),
                        rs.getBigDecimal("total_amount_completed_orders"),
                        rs.getBigDecimal("average_amount_orders")
                ), Timestamp.from(fromInstant), Timestamp.from(toInstant));

        return  analyticsByPeriod.stream().findFirst();
    }

    public Optional<OrderStatisticsByUserDto> findAnalyticsByUser(Long userId) {
        var userAnalytics =  jdbcTemplate.query(
                """
                       select 
                               now() as calculated_at,
                               oe.user_id,
                               count() as count_orders,
                               countIf(status = 'CREATED') as created_orders,
                               countIf(status = 'COMPLETED') as completed_orders,
                               countIf(status = 'CANCELLED') as cancelled_orders,
                               sumIf(total_amount, status = 'COMPLETED') as total_amount_completed_orders,
                               if(count_orders = 0, 0, avg(total_amount)) as average_amount_orders
                        from (select     
                                  oe.order_id,
                                  oe.user_id,
                                  oe.status,
                                  oe.occurred_at,
                                  oe.total_amount
                            from order_events oe
                            order by oe.order_id, oe.occurred_at desc
                            LIMIT 1 BY oe.order_id
                        ) as oe
                        where oe.user_id = ?                    
                        group by oe.user_id
                        ;
                    """,
                (rs, rowNum) -> new OrderStatisticsByUserDto(
                        rs.getTimestamp("calculated_at").toInstant(),
                        rs.getLong("user_id"),
                        rs.getLong("count_orders"),
                        rs.getLong("created_orders"),
                        rs.getLong("completed_orders"),
                        rs.getLong("cancelled_orders"),
                        rs.getBigDecimal("total_amount_completed_orders"),
                        rs.getBigDecimal("average_amount_orders")
                ),
                userId);

        return  userAnalytics.stream().findFirst();
    }

}
