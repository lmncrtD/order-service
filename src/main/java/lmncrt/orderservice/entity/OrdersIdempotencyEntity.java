package lmncrt.orderservice.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lmncrt.orderservice.service.OrderOperations;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_idempotency")
public class OrdersIdempotencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_key", nullable = false, unique = true)
    private UUID requestKey;

    @Column(name = "request_text", nullable = false)
    private String requestText;

    @Column(name = "order_id")
    private Long orderId;

}
