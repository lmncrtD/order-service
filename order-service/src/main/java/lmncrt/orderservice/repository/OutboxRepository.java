package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    @Query(
            value = """
            SELECT *
            FROM public.outbox o
            WHERE published_at IS NULL
                        AND (locked_until is NULL
                           or locked_until <= clock_timestamp())
            ORDER BY created_at ASC, event_id ASC
            LIMIT 100
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true
    )
    List<OutboxEntity> findUnpublishedBatch();

    @Query(
            value = "SELECT clock_timestamp()", nativeQuery = true
    )
    Instant getDatabaseTime();

    @Modifying
    @Transactional
    @Query( value =
            """
            UPDATE public.outbox
            SET published_at = clock_timestamp(),
                locked_until = NULL,
                lock_token = NULL
            WHERE event_id = :eventId
              AND lock_token = :lockToken
              AND published_at IS NULL; 
            """
            , nativeQuery = true
    )
    int markPublished(@Param("eventId") UUID eventId, @Param("lockToken") UUID lockToken);

}
