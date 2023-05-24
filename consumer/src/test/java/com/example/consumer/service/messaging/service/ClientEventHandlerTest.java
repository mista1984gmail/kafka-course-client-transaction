package com.example.consumer.service.messaging.service;

import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.util.FakeClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientEventHandlerTest {
    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";
    private Producer<String, String> producer;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {

    }

    @SpyBean
    private KafkaMessagingService kafkaMessagingService;

    @Captor
    ArgumentCaptor<ClientEvent> userArgumentCaptor;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
    }

    @Test
    public void testPublishEmployee(CapturedOutput output) throws ExecutionException, InterruptedException, JsonProcessingException {
        ClientEvent client = FakeClient.getClientEvent();
        String message = objectMapper.writeValueAsString(client);
        producer.send(new ProducerRecord<>(TOPIC_NAME_SEND_CLIENT, message));

        verify(kafkaMessagingService, timeout(5000).times(1))
                .createClient(userArgumentCaptor.capture());

    }
}
