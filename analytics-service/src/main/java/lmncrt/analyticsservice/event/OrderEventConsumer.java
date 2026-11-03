package lmncrt.analyticsservice.event;

import lmncrt.analyticsservice.dto.OrderEventDto;
import lmncrt.analyticsservice.mappers.OrderEventMapper;
import lmncrt.analyticsservice.repository.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderEventMapper orderEventMapper;
    private final OrderEventRepository orderEventRepository;

    @KafkaListener(topics = "orders.events")
    public void consume(String message) {
        var dto = objectMapper.readValue(message, OrderEventDto.class);
        var entity = orderEventMapper.toEntity(dto);
        orderEventRepository.save(entity);
    }

}
