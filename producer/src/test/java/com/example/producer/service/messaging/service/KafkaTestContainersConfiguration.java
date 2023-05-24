package com.example.producer.service.messaging.service;

import com.example.producer.service.messaging.event.ClientSendEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTestContainersConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapService;

    @Bean
    public ProducerFactory<String, ClientSendEvent> clientProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapService);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSeserializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ClientSendEvent> clientKafkaTemplate() {
        return new KafkaTemplate<>(clientProducerFactory());
    }
}
