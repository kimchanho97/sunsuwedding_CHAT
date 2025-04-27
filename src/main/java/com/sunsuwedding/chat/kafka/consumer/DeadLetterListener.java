package com.sunsuwedding.chat.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeadLetterListener {

    @KafkaListener(
            topicPattern = ".*\\.DLT",
            groupId = "dlt-handler"
    )
    public void listen(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.error(
                "❌ DLT received: topic={} partition={} offset={} key={} value={} headers={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value(),
                record.headers()
        );

        ack.acknowledge();
    }
}
