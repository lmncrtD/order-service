package lmncrt.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class KafkaConfig {

    @Bean
    public NewTopic ordersEvent() {
        TopicBuilder topicBuilder = TopicBuilder.name("orders.events");
        topicBuilder.partitions(1);
        topicBuilder.replicas(1);
        return topicBuilder.build();
    }

}
