package lmncrt.analyticsservice.config;

import lmncrt.analyticsservice.dto.OrderEventDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {


    @Bean
    public FixedBackOff retryPolitics(
            @Value("${analytics.kafkaconfig.interval-param}") long interval,
            @Value("${analytics.kafkaconfig.maxattempts-param}") long maxAttempts
    ) {
        return new FixedBackOff(interval, maxAttempts);
    }

    // Создаем фабрику, в которой описываем как производим сериализацию.
    // В нашем случае БД может быть недоступна, с JSON все ок.
    // JSON может быть некорректный и JSON не смог преобразоваться в OrderEventDto
    @Bean
    public ProducerFactory<String, Object> fabricProducer(
        @Value("${spring.kafka.bootstrap-servers}") String connection
    ) {
        // Передаем для фабрики настройки подключения
        Map<String, Object> configs = new HashMap<>();
        configs.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                connection
        );

        // Превращаем строковый ключ в байт
        StringSerializer stringSerializer = new StringSerializer();

        // Задаем соответствие. Для какого типа какой сериализатор используем.
        Map<Class<?>, Serializer<?>>  serializers = new HashMap<>();
        serializers.put(byte[].class, new ByteArraySerializer());
        serializers.put(OrderEventDto.class, new JacksonJsonSerializer<OrderEventDto>());

        // При отправке выбираем фактический сериализатор на основе типа данных.
        DelegatingByTypeSerializer delegatingByTypeSerializer = new DelegatingByTypeSerializer(serializers);

        // возвращаем объединение настроек и сериализаторов для фабрики.
        return  new DefaultKafkaProducerFactory<>(
                configs,
                stringSerializer,
                delegatingByTypeSerializer
        );

    }

    // Создаем объект Spring который будет отправлять сообщение
    @Bean
    public KafkaTemplate<String, Object> dlqKafkaTemplate(ProducerFactory<String, Object> factory){

        return new KafkaTemplate<>(factory);
    }

    // прописываем правило, куда будем записывать сообщение.
    @Bean
    public DeadLetterPublishingRecoverer  deadLetterPublishingRecoverer(
            KafkaTemplate<String, Object> kafkaTemplate
    ){

        var deadLetter = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(
                        "orders.events.dlq",
                        -1)
        );

        deadLetter.setFailIfSendResultIsError(true);

        return deadLetter;
    }

    // метод создает и настраивает объект, который будет обрабатывать ошибки.
    // при ошибке в listener мы передаем управление этому обрабочтику, который производит ретраи,
    // и в случае неудачи пишет в DLQ
    @Bean
    public DefaultErrorHandler errorHandler(
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer,
            FixedBackOff fixedBackOff){
        var defaultHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer,
                fixedBackOff
        );

        defaultHandler.setCommitRecovered(true);
        defaultHandler.addNotRetryableExceptions(DeserializationException.class);

        defaultHandler.setRetryListeners(
                (record, exception, deliveryAttempt) -> {
                    log.warn("RETRY: {}", deliveryAttempt);
                }
        );

        return defaultHandler;
    }

    @Bean
    public NewTopic dlqTopic(){
        TopicBuilder topicBuilder = TopicBuilder.name("orders.events.dlq");
        topicBuilder.partitions(1);
        topicBuilder.replicas(1);
        return topicBuilder.build();
    }


}
