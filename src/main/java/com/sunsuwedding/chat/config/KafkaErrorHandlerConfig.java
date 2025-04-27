package com.sunsuwedding.chat.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaOperations<String, Object> kafkaTemplate) {
        // 3회 재시도(backOffInterval=0, maxAttempts=3)
        FixedBackOff backOff = new FixedBackOff(0L, 3L);

        // 실패 메시지는 "<토픽>.DLT" 로 분기
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, e) ->
                        new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
