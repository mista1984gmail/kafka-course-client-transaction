package com.example.consumer.service.messaging.service;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.domain.repository.ClientRepository;
import com.example.consumer.domain.repository.TransactionFailedRepository;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.TransactionFailedService;
import com.example.consumer.service.dto.ClientDto;
import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import com.example.util.FakeClient;
import com.example.util.FakeTransaction;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@Testcontainers
class KafkaMessagingServiceIT {
    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "send.transaction";
    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
    );

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Mock
    private KafkaMessagingService kafkaMessagingService;

    @Mock
    private ClientService clientService;
    @Mock
    private TransactionFailedService transactionFailedService;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionFailedRepository transactionFailedRepository;


    @Test
    @SneakyThrows
    @DirtiesContext
    void createClient() {
        //given
        String bootstrapServers = kafka.getBootstrapServers();
        ClientEvent clientEvent = FakeClient.getClientEvent();
        Client client = FakeClient.getClient();
        ClientDto clientDto = FakeClient.getClientDto();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, ClientEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, ClientEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.send(TOPIC_NAME_SEND_CLIENT, clientEvent);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer <String, ClientEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_CLIENT));
        ConsumerRecords <String, ClientEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        //then
        assertEquals(1, records.count());
        assertEquals(client.getFirstName(), records.iterator().next().value().getFirstName());
        assertEquals(client.getLastName(), records.iterator().next().value().getLastName());
        assertEquals(client.getAddress(), records.iterator().next().value().getAddress());
        assertEquals(client.getEmail(), records.iterator().next().value().getEmail());

        //then
        kafkaMessagingService.createClient(clientEvent);

        when(modelMapper.map(clientEvent, ClientDto.class)).thenReturn(clientDto);
        when(modelMapper.map(clientDto, Client.class)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDto.class)).thenReturn(clientDto);

        verify(kafkaMessagingService,timeout(10000).times(1)).createClient(any());
    }

    @Test
    void createTransactionIfClientExist() {

        //given
        String bootstrapServers = kafka.getBootstrapServers();
        TransactionEvent transactionEvent = FakeTransaction.getTransactionEvent();
        Client client = FakeClient.getClient();
        ClientDto clientDto = FakeClient.getClientDto();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, TransactionEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, TransactionEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.send(TOPIC_NAME_SEND_TRANSACTION, transactionEvent);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer <String, TransactionEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_TRANSACTION));
        ConsumerRecords <String, TransactionEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        assertEquals(transactionEvent.getBank(), records.iterator().next().value().getBank());
        assertEquals(transactionEvent.getClientId(), records.iterator().next().value().getClientId());
        assertEquals(transactionEvent.getOrderType(), records.iterator().next().value().getOrderType());
        assertEquals(transactionEvent.getQuantity(), records.iterator().next().value().getQuantity());
        assertEquals(transactionEvent.getPrice(), records.iterator().next().value().getPrice());
        assertEquals(transactionEvent.getCreatedAt(), records.iterator().next().value().getCreatedAt());

        //then
        kafkaMessagingService.createTransaction(transactionEvent);

        when(clientService.isExistClient(transactionEvent.getClientId())).thenReturn(true);

        when(clientService.addTransactionToClient(transactionEvent)).thenReturn(client);
        when(modelMapper.map(client, ClientDto.class)).thenReturn(clientDto);
        when(clientRepository.save(client)).thenReturn(client);

        verify(kafkaMessagingService,timeout(10000).times(1)).createTransaction(any());

    }
    @Test
    void createTransactionIfClientDoesNotExist() {

        //given
        String bootstrapServers = kafka.getBootstrapServers();
        TransactionEvent transactionEvent = FakeTransaction.getTransactionEvent();
        TransactionFailed transactionFailed = FakeTransaction.getTransactionFailed();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, TransactionEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, TransactionEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.send(TOPIC_NAME_SEND_TRANSACTION, transactionEvent);

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-java-test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer <String, TransactionEvent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(TOPIC_NAME_SEND_TRANSACTION));
        ConsumerRecords <String, TransactionEvent> records = consumer.poll(Duration.ofMillis(10000L));
        consumer.close();

        assertEquals(transactionEvent.getBank(), records.iterator().next().value().getBank());
        assertEquals(transactionEvent.getClientId(), records.iterator().next().value().getClientId());
        assertEquals(transactionEvent.getOrderType(), records.iterator().next().value().getOrderType());
        assertEquals(transactionEvent.getQuantity(), records.iterator().next().value().getQuantity());
        assertEquals(transactionEvent.getPrice(), records.iterator().next().value().getPrice());
        assertEquals(transactionEvent.getCreatedAt(), records.iterator().next().value().getCreatedAt());

        //then
        kafkaMessagingService.createTransaction(transactionEvent);

        when(clientService.isExistClient(transactionEvent.getClientId())).thenReturn(false);

        when(transactionFailedService.getTransactionFailed(transactionEvent)).thenReturn(transactionFailed);
        when(transactionFailedRepository.save(transactionFailed)).thenReturn(transactionFailed);

        verify(kafkaMessagingService,timeout(10000).times(1)).createTransaction(any());

    }
}