package lmncrt.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox")
public class OutboxEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "order_id")
    private Long orderId;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "payload")
    private String payload;

    @Column(name = "published_at")
    private Instant publishedAt;


}
