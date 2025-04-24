package com.sunsuwedding.chat.init;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaInitializer implements InitializingBean {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaInitializer(@Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void verifyKafkaTemplate() {
        log.info("🔍 KafkaTemplate class: {}", kafkaTemplate.getClass());
        log.info("🔍 ProducerFactory class: {}", kafkaTemplate.getProducerFactory().getClass());
        log.info("🔍 transactionCapable: {}", kafkaTemplate.getProducerFactory().transactionCapable());

        if (!kafkaTemplate.getProducerFactory().transactionCapable()) {
            throw new IllegalStateException("❌ 주입된 KafkaTemplate은 트랜잭션을 지원하지 않습니다.");
        }
    }

    @Override
    public void afterPropertiesSet() {
        // Kafka 트랜잭션을 사용하려면 Kafka 브로커와 트랜잭션 핸드셰이크를 미리 한번 수행해야 한다.
        kafkaTemplate.executeInTransaction(kt -> null);
    }
}
