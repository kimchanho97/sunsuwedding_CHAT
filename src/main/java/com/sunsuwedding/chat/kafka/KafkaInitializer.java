package com.sunsuwedding.chat.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaInitializer implements InitializingBean {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void afterPropertiesSet() {
        // Kafka 트랜잭션을 사용하려면 Kafka 브로커와 트랜잭션 핸드셰이크를 미리 한번 수행해야 한다.
        kafkaTemplate.executeInTransaction(kt -> null);
    }
}
