package com.sunsuwedding.chat.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic chatMessageTopic() {
        return TopicBuilder.name("chat-message")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic presenceStatusTopic() {
        return TopicBuilder.name("presence-status")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic chatMessageSavedTopic() {
        return TopicBuilder.name("chat-message-saved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic chatMessageUnicastTopic() {
        return TopicBuilder.name("chat-message-unicast")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic chatMessageUnicastResponseTopic() {
        return TopicBuilder.name("chat-room-resort")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic chatMessageReadSyncTopic() {
        return TopicBuilder.name("chat-message-read-sync")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
