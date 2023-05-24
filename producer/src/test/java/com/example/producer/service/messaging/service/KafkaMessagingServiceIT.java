package com.example.producer.service.messaging.service;

import com.example.producer.service.messaging.event.ClientSendEvent;
import com.example.producer.service.messaging.event.TransactionSendEvent;
import com.example.util.FakeClient;
import com.example.util.FakeTransaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
class KafkaMessagingServiceIT {

    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "send.transaction";
   @Container
   static final KafkaContainer kafka = new KafkaContainer(
           DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
   );

    @Autowired
    private KafkaTemplate<String, Object> clientKafkaTemplate;
    @Autowired
    private  ProducerFactory<String, Object> clientProducerFactory;
    @Autowired
    private KafkaMessagingService kafkaMessagingService;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


    @Test
    void sendClient() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        ClientSendEvent client = FakeClient.getClientSendEvent();

        //when
        kafkaMessagingService.sendClient(client);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, ClientSendEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_CLIENT));
        ConsumerRecords <String, ClientSendEvent>records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        //then
        assertEquals(1, records.count());
        assertEquals(client.getFirstName(), records.iterator().next().value().getFirstName());
        assertEquals(client.getLastName(), records.iterator().next().value().getLastName());
        assertEquals(client.getAddress(), records.iterator().next().value().getAddress());
        assertEquals(client.getEmail(), records.iterator().next().value().getEmail());

    }

    @Test
    void sendTransaction() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        TransactionSendEvent transaction = FakeTransaction.getTransactionSendEvent();

        //when
        kafkaMessagingService.sendTransaction(transaction);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, TransactionSendEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_TRANSACTION));
        ConsumerRecords <String, TransactionSendEvent>records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        //then
        assertEquals(1, records.count());
        assertEquals(transaction.getBank(), records.iterator().next().value().getBank());
        assertEquals(transaction.getClientId(), records.iterator().next().value().getClientId());
        assertEquals(transaction.getOrderType(), records.iterator().next().value().getOrderType());
        assertEquals(transaction.getQuantity(), records.iterator().next().value().getQuantity());
        assertEquals(transaction.getPrice(), records.iterator().next().value().getPrice());
        assertEquals(transaction.getCreatedAt(), records.iterator().next().value().getCreatedAt());
    }
}