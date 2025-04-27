package com.sunsuwedding.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;
    private final ProducerListener<String, Object> listener;

    // 1) At-Least-Once용 (Primary)
    @Bean
    @Primary
    public ProducerFactory<String, Object> normalProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> normalProducerFactory
    ) {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(normalProducerFactory);
        template.setProducerListener(listener);
        return template;
    }

    // 2) Exactly-Once용
    @Bean
    @Qualifier("transactionalProducerFactory")
    public ProducerFactory<String, Object> transactionalProducerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());

        // 트랜잭션 전용 ID prefix만 추가
        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setTransactionIdPrefix("chat-tx-");
        return factory;
    }

    @Bean
    @Qualifier("transactionalKafkaTemplate")
    public KafkaTemplate<String, Object> transactionalKafkaTemplate(
            @Qualifier("transactionalProducerFactory") ProducerFactory<String, Object> txProducerFactory
    ) {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(txProducerFactory);
        template.setProducerListener(listener);
        return template;
    }

    // 3) 트랜잭션 매니저 (Exactly-Once 컨슈머에서 사용)
    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            @Qualifier("transactionalProducerFactory") ProducerFactory<String, Object> txProducerFactory
    ) {
        return new KafkaTransactionManager<>(txProducerFactory);
    }
}
