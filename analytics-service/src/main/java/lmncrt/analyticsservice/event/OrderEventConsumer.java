package lmncrt.analyticsservice.event;

import lmncrt.analyticsservice.dto.OrderEventDto;
import lmncrt.analyticsservice.mappers.OrderEventMapper;
import lmncrt.analyticsservice.repository.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderEventMapper orderEventMapper;
    private final OrderEventRepository orderEventRepository;

    @KafkaListener(topics = "orders.events")
    public void consume(OrderEventDto message, Acknowledgment acknowledgment) {
        var entity = orderEventMapper.toEntity(message);
        orderEventRepository.save(entity);
        acknowledgment.acknowledge();
    }

}
