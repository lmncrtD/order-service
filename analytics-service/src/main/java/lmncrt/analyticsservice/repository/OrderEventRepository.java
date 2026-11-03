package lmncrt.analyticsservice.repository;

import lmncrt.analyticsservice.entity.OrderEventEntity;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class OrderEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(OrderEventEntity event) {
        jdbcTemplate.update(
                """
                        INSERT INTO analytics.order_events (
                        event_id, event_type, occurred_at,
                        order_id, user_id, created_at,
                        status, total_amount
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                event.eventId(),
                event.eventType(),
                event.occurredAt(),
                event.orderId(),
                event.userId(),
                event.createdAt(),
                event.status(),
                event.totalAmount()
        );
    }
}
