package com.example.consumer.service.messaging.service;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.domain.entity.Transaction;
import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.domain.repository.ClientRepository;
import com.example.consumer.domain.repository.TransactionFailedRepository;
import com.example.consumer.domain.repository.TransactionRepository;
import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import com.example.util.FakeClient;
import com.example.util.FakeTransaction;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KafkaMessagingServiceIT {
    public static final Long CLIENT_ID = 1L;
    public static final Long TRANSACTIONAL_ID = 1L;
    public static final Long TRANSACTIONAL_FAILED_ID = 1L;
    public static final String TOPIC_NAME_SEND_CLIENT = "send.client";
    public static final String TOPIC_NAME_SEND_TRANSACTION = "send.transaction";

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withUsername("username")
            .withPassword("password")
            .withExposedPorts(5432)
            .withReuse(true);
    @Container
    static final KafkaContainer kafkaContainer =
                new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.4"))
            .withEmbeddedZookeeper()
          .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9093 ,BROKER://0.0.0.0:9092")
          .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:PLAINTEXT")
          .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "BROKER")
          .withEnv("KAFKA_BROKER_ID", "1")
          .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
          .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
          .withEnv("KAFKA_LOG_FLUSH_INTERVAL_MESSAGES", Long.MAX_VALUE + "")
          .withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0");

    static {
        Startables.deepStart(Stream.of(postgreSQLContainer, kafkaContainer)).join();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionFailedRepository transactionFailedRepository;


    @Test
    @Order(1)
    void createClient() throws InterruptedException {
        //given
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        ClientEvent clientEvent = FakeClient.getClientEvent();
        Client client = FakeClient.getClient();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, ClientEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, ClientEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        //when
        TimeUnit.SECONDS.sleep(5);
        kafkaTemplate.send(TOPIC_NAME_SEND_CLIENT, clientEvent);
        TimeUnit.SECONDS.sleep(5);

        //then
        Client clientFromDB = clientRepository.findById(CLIENT_ID).get();
        assertEquals(clientFromDB.getId(), CLIENT_ID);
        assertEquals(clientFromDB.getFirstName(), client.getFirstName());
        assertEquals(clientFromDB.getLastName(), client.getLastName());
        assertEquals(clientFromDB.getAddress(), client.getAddress());
        assertEquals(clientFromDB.getEmail(), client.getEmail());
        assertEquals(clientFromDB.getTelephone(), client.getTelephone());
    }

    @Test
    @Order(2)
    void createTransactionIfClientExist() throws InterruptedException {

        //given
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        TransactionEvent transactionEvent = FakeTransaction.getTransactionEvent();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, TransactionEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, TransactionEvent> kafkaTemplateTransaction = new KafkaTemplate<>(producerFactory);

        //when
        TimeUnit.SECONDS.sleep(5);
        kafkaTemplateTransaction.send(TOPIC_NAME_SEND_TRANSACTION, transactionEvent);
        TimeUnit.SECONDS.sleep(5);

        //then
        Transaction transactionFromDB = transactionRepository.findById(TRANSACTIONAL_ID).get();
        assertEquals(transactionFromDB.getId(), TRANSACTIONAL_ID);
        assertEquals(transactionFromDB.getBank(), transactionEvent.getBank());
        assertEquals(transactionFromDB.getOrderType(), transactionEvent.getOrderType());
        assertEquals(transactionFromDB.getQuantity(), transactionEvent.getQuantity());
        assertEquals(transactionFromDB.getPrice(), transactionEvent.getPrice());
        assertEquals(transactionFromDB.getCreatedAt(), LocalDateTime.of(2023,05,19,00,00));
        assertEquals(transactionFromDB.getOwner().getId(), CLIENT_ID);

    }
    @Test
    @Order(3)
    void createTransactionIfClientDoesNotExist() throws InterruptedException {
        //given
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        TransactionEvent transactionEvent = FakeTransaction.getTransactionEventFailed();

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, TransactionEvent> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        KafkaTemplate<String, TransactionEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        //when
        TimeUnit.SECONDS.sleep(5);
        kafkaTemplate.send(TOPIC_NAME_SEND_TRANSACTION, transactionEvent);
        TimeUnit.SECONDS.sleep(5);

        //then
        TransactionFailed transactionFromDB = transactionFailedRepository.findById(TRANSACTIONAL_FAILED_ID).get();
        assertEquals(transactionFromDB.getId(), TRANSACTIONAL_FAILED_ID);
        assertEquals(transactionFromDB.getBank(), transactionEvent.getBank());
        assertEquals(transactionFromDB.getOrderType(), transactionEvent.getOrderType());
        assertEquals(transactionFromDB.getQuantity(), transactionEvent.getQuantity());
        assertEquals(transactionFromDB.getPrice(), transactionEvent.getPrice());
        assertEquals(transactionFromDB.getCreatedAt(), LocalDateTime.of(2023,05,19,00,00));
        assertEquals(transactionFromDB.getIncorrectId(), 55555L);
        assertEquals(transactionFromDB.getError(), "Transaction canceled, can not find client id.");

    }
}