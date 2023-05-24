package com.example.consumer.service.messaging.service;

import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import com.example.util.FakeClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:29092", "port=29092"})
public class KafkaConsumerTestTwo {

    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, ClientEvent> clientFactory;

    @Autowired
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ClientEvent>> clientContainerFactory;

    @Autowired
    private ConsumerFactory<String, TransactionEvent> transactionFactory;

    @Autowired
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TransactionEvent>> transactionContainerFactory;


    @SpyBean
    private KafkaMessagingService kafkaMessagingService;

    @Captor
    ArgumentCaptor<ClientEvent> eventCaptor;

    @Test
    public void testReceivingKafkaEvents() throws InterruptedException {
        ClientEvent client = FakeClient.getClientEvent();
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:29092");
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        ProducerFactory<String, ClientEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, ClientEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.send(TOPIC_NAME_SEND_CLIENT, client);

        verify(kafkaMessagingService,timeout(10000).times(1)).createClient(eventCaptor.capture());
        //kafkaTemplate.send(new ProducerRecord<>(TOPIC_NAME_SEND_CLIENT, client));



    }


}
