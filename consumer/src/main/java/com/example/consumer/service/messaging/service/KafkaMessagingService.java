package com.example.consumer.service.messaging.service;

import com.example.consumer.service.ClientService;
import com.example.consumer.service.TransactionFailedService;
import com.example.consumer.service.dto.ClientDto;
import com.example.consumer.service.messaging.event.ClientEvent;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@AllArgsConstructor
public class KafkaMessagingService {
    private static final String topicCreateClient = "${topic.send-client}";
    private static final String topicCreateTransaction = "${topic.send-transaction}";
    private static final String kafkaConsumerGroupId = "${spring.kafka.consumer.group-id}";
    private final ClientService clientService;
    private final TransactionFailedService transactionFailedService;
    private final ModelMapper modelMapper;

    @Transactional
    @KafkaListener(topics = topicCreateClient, groupId = kafkaConsumerGroupId, properties = {"spring.json.value.default.type=com.example.consumer.service.messaging.event.ClientEvent"})
    public ClientEvent createClient(ClientEvent clientEvent) {
        log.info("Message consumed {}", clientEvent);
        clientService.save(modelMapper.map(clientEvent, ClientDto.class));
        return clientEvent;
    }

    @Transactional
    @KafkaListener(topics = topicCreateTransaction, groupId = kafkaConsumerGroupId, properties = {"spring.json.value.default.type=com.example.consumer.service.messaging.event.TransactionEvent"})
    public TransactionEvent createTransaction(TransactionEvent transactionEvent) {
        log.info("Message consumed {}", transactionEvent);
        if (clientService.existsByClientCode(transactionEvent.getClientUniqueCode())) {
            clientService.save(
                    modelMapper.map(clientService.addTransactionToClient(transactionEvent), ClientDto.class));
        } else {
            transactionFailedService.save(transactionFailedService.getTransactionFailed(transactionEvent));
        }
        return transactionEvent;
    }
}
