package com.example.consumer.service.messaging.service;

import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.util.FakeClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
        partitions = 1,
        controlledShutdown = true)
public class KafkaConsumerTest {

    private static File EVENT_JSON = Paths.get("src", "test", "resources", "files",
            "event-file.json").toFile();
    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";

    @Autowired
    KafkaTemplate<String, ClientEvent> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private KafkaMessagingService kafkaMessagingService;

    @Captor
    ArgumentCaptor<ClientEvent> eventCaptor;


    @Test
    @SneakyThrows
    @DirtiesContext
    void consumeEvents() {
        ClientEvent client = FakeClient.getClientEvent();
        //String bootstrapServers = kafka.getBootstrapServers();
        //ClientEvent event = objectMapper.readValue(EVENT_JSON, ClientEvent.class);
        //kafkaTemplate.send(TOPIC_NAME_SEND_CLIENT, event);
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, Object> clientProducerFactory = new DefaultKafkaProducerFactory<>(configProps);

        KafkaTemplate<String, Object> clientKafkaTemplate = new KafkaTemplate<>(clientProducerFactory);
        clientKafkaTemplate.send(TOPIC_NAME_SEND_CLIENT, client);

   /*     Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, ClientEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_CLIENT));
        ConsumerRecords<String, ClientEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();*/

        //then
        //assertEquals(1, records.count());


        //verify(kafkaMessagingService,timeout(10000).times(1)).createClient(eventCaptor.capture());
        //Event argument = eventCaptor.getValue();
        // .. assert the message properties
        //verify(customInterface, timeout(10000).times(1)).apply(any());

    }

}
