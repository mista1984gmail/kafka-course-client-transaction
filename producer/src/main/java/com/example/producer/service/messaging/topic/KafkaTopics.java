package com.example.producer.service.messaging.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {
    @Value("${topic.send-client}")
    private String sendClientTopic;
    @Value("${topic.send-transaction}")
    private String sendTransactionTopic;

    @Bean
    public NewTopic topicSendClient() {
        return TopicBuilder
                .name(sendClientTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicSendTransaction() {
        return TopicBuilder
                .name(sendTransactionTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
