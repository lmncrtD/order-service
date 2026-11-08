package lmncrt.orderservice.service.serviceimpl;


import lmncrt.orderservice.entity.OutboxEntity;
import lmncrt.orderservice.repository.OutboxRepository;
import lmncrt.orderservice.service.OutboxClaimService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxClaimServiceImpl implements OutboxClaimService {

    private final OutboxRepository outboxRepository;
    @Value("${outbox.claim-duration}")
    private Duration claimDuration;

    @Override
    @Transactional
    public List<OutboxEntity> claimBatch() {

        var findBatch = outboxRepository.findUnpublishedBatch();
        var databaseTime = outboxRepository.getDatabaseTime();

        var resultClaim = databaseTime.plus(claimDuration);

        for (OutboxEntity outboxEntity : findBatch) {

            outboxEntity.setLockedUntil(resultClaim);
            outboxEntity.setLockToken(UUID.randomUUID());

        }

        return findBatch;
    }

}
