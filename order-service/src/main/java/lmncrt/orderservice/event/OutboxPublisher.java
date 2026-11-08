package lmncrt.orderservice.event;

import lmncrt.orderservice.entity.OutboxEntity;
import lmncrt.orderservice.repository.OutboxRepository;
import lmncrt.orderservice.service.OutboxClaimService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxClaimService outboxClaimService;

    @Scheduled(fixedDelay = 1000)
    public void publishBatch() {
        var listOutboxEntities = outboxClaimService.claimBatch();

        for  (OutboxEntity outboxEntity : listOutboxEntities) {

            try {
                var sendMessage = kafkaTemplate.send(
                        "orders.events",
                        outboxEntity.getOrderId().toString(),
                        outboxEntity.getPayload()
                );

                sendMessage.whenComplete(
                        (res, ex) -> {
                            if (ex != null) {
                                log.error("Неуспешная отправка сообщения", ex);
                            } else {
                                log.info("Сообщение отправлено");
                                try{
                                    var returnedNum = outboxRepository.markPublished(
                                            outboxEntity.getEventId(),
                                            outboxEntity.getLockToken()
                                    );

                                    if (returnedNum == 1){
                                        log.info("Отметка, что публикация сохранена для {}",
                                                outboxEntity.getEventId());
                                    } else {
                                        log.info("Отметка не изменена для {}", outboxEntity.getEventId());
                                    }

                                    // ловим ошибку которая может возникнуть внутри лямбды.
                                } catch (RuntimeException e) {
                                    log.error("{}: id = {}", e.getClass(), outboxEntity.getEventId(), e);
                                }
                            }
                        });

            } catch (RuntimeException e) {
                log.error("{}: id = {}", e.getClass(), outboxEntity.getEventId(), e);
                return;
            }
        }
    }

}
