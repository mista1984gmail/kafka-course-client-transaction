package com.example.producer.service.messaging.service;

import com.example.producer.service.messaging.event.ClientSendEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class KafkaMessagingServiceTest {
    @Container
    KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Test
    void sendClient() {
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        String topicName = "send.client";

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new JsonDeserializer<>());

        ProducerFactory<String, Object> clientProducerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, Object> clientKafkaTemplate = new KafkaTemplate<>(clientProducerFactory);
        clientKafkaTemplate.setDefaultTopic(topicName);
        KafkaMessagingService kafkaMessagingService = new KafkaMessagingService(clientKafkaTemplate);

        kafkaMessagingService.sendClient(new ClientSendEvent("Stas", "Mitskevich", "Grodno", "stas@gmail.com"));

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, new JsonDeserializer<>(ClientSendEvent.class, false));
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer consumer = new KafkaConsumer(properties);
        consumer.subscribe(Arrays.asList(topicName));
        ConsumerRecords records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        assertEquals(1, records.count());



    }

    //@Test
    void sendTransaction() {
    }
}