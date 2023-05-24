package com.example.producer.service.messaging.service;


import com.example.kafka.ReusableKafkaContainer;
import com.example.kafka.ReuseKafkaContainerExtension;
import com.example.producer.service.messaging.event.ClientSendEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ReuseKafkaContainerExtension.class)
@Import(com.example.producer.service.messaging.service.KafkaTestContainersConfiguration.class)
class SendServiceTest {
    @ReusableKafkaContainer
    KafkaContainer kafka;

    @Test
    void sendRecords() {
        String bootstrapServers = kafka.getBootstrapServers();
        String topicName = "send-topic";

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new org.springframework.kafka.support.serializer.JsonDeserializer<>());

        ProducerFactory<String, Object> clientProducerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, Object> clientKafkaTemplate = new KafkaTemplate<>(clientProducerFactory);
        clientKafkaTemplate.setDefaultTopic(topicName);
        KafkaMessagingService kafkaMessagingService = new KafkaMessagingService(clientKafkaTemplate);
        kafkaMessagingService.sendClient(new ClientSendEvent("Stas", "Mitskevich", "Grodno", "stas@gmail.com"));

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        assertEquals(3, records.count());

    }
}