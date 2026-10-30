package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.OrdersIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OrderIdempotencyRepository extends JpaRepository<OrdersIdempotencyEntity, Long> {

    OrdersIdempotencyEntity findByRequestKey(UUID requestKey);

    @Modifying
    @Query(value = """
    INSERT INTO public.order_idempotency (request_key, request_text)
    VALUES (:key, :requestText)
    ON CONFLICT (request_key) DO NOTHING
    """, nativeQuery = true)
    int tryRegister(
            @Param("key") UUID key,
            @Param("requestText") String requestText
    );

    @Modifying
    @Query(value = """
    UPDATE public.order_idempotency
    SET order_id = :orderId
    WHERE request_key = :key
    """, nativeQuery = true)
    int linkOrder(
            @Param("key") UUID key,
            @Param("orderId") Long orderId
    );

}
