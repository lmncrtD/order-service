package lmncrt.orderservice.event;

import lmncrt.orderservice.entity.OutboxEntity;
import lmncrt.orderservice.repository.OutboxRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@AllArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void publishBatch() {
        var listOutboxEntities = outboxRepository.findUnpublishedBatch();

        for  (OutboxEntity outboxEntity : listOutboxEntities) {

            try {
                var sendMessage = kafkaTemplate.send(
                        "orders.events",
                        outboxEntity.getOrderId().toString(),
                        outboxEntity.getPayload()
                );

                sendMessage.get();

                outboxEntity.setPublishedAt(Instant.now());
                outboxRepository.save(outboxEntity);
            } catch (ExecutionException e) {
                log.error("{}: id = {}", e.getClass(), outboxEntity.getEventId(), e);
                return;
            } catch (InterruptedException e) {
                log.error("{}: id = {}", e.getClass(), outboxEntity.getEventId(), e);
                Thread.currentThread().interrupt();
                return;
            } catch (RuntimeException e) {
                log.error("{}: id = {}", e.getClass(), outboxEntity.getEventId(), e);
                return;
            }
        }

    }

}
