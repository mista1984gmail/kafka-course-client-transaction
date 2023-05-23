package com.example.producer.service.messaging.service;

import com.example.producer.service.messaging.event.ClientSendEvent;
import com.example.producer.service.messaging.event.TransactionSendEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessagingService {

    @Value("${topic.send-client}")
    private String sendClientTopic;
    @Value("${topic.send-transaction}")
    private String sendTransactionTopic;
    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void sendClient(ClientSendEvent clientSendEvent) {
       kafkaTemplate.send(sendClientTopic, clientSendEvent);
    }

    public void sendTransaction(TransactionSendEvent transactionSendEvent) {
        this.kafkaTemplate.send(sendTransactionTopic, transactionSendEvent);
    }
}
