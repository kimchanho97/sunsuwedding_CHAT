package com.sunsuwedding.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
@Configuration
public class KafkaProducerListenerConfig {

    @Bean
    public ProducerListener<String, Object> producerListener() {
        return new ProducerListener<>() {
            
            @Override
            public void onError(
                    ProducerRecord<String, Object> record, RecordMetadata metadata, Exception exception
            ) {
                // 실패 시 공통 로직 (로깅, 메트릭, 알림 등)
                log.error(
                        "❌ Kafka producer error: topic={} partition={} offset={} key={} value={} headers={}",
                        record.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        record.key(),
                        record.value(),
                        record.headers()
                );
            }
        };
    }

}
