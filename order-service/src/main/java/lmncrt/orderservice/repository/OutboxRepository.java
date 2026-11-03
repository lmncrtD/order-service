package lmncrt.orderservice.repository;

import lmncrt.orderservice.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    @Query(
            value = """
            SELECT *
            FROM public.outbox o
            WHERE published_at IS NULL
            ORDER BY created_at ASC, event_id ASC
            LIMIT 100
            """, nativeQuery = true
    )
    List<OutboxEntity> findUnpublishedBatch();

}
