package com.sunsuwedding.chat.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private static final int PARTITIONS = 1;
    private static final int REPLICAS = 1;
    private static final long RETENTION_MS = 7L * 24 * 60 * 60 * 1000; // 7일

    private NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(RETENTION_MS))
                .build();
    }

    @Bean
    public NewTopic chatMessageTopic() {
        return createTopic("chat-message");
    }

    @Bean
    public NewTopic presenceStatusTopic() {
        return createTopic("presence-status");
    }

    @Bean
    public NewTopic chatMessageSavedTopic() {
        return createTopic("chat-message-saved");
    }

    @Bean
    public NewTopic chatMessageUnicastTopic() {
        return createTopic("chat-message-unicast");
    }

    @Bean
    public NewTopic chatRoomResortTopic() {
        return createTopic("chat-room-resort");
    }

    @Bean
    public NewTopic chatMessageReadSyncTopic() {
        return createTopic("chat-message-read-sync");
    }

}