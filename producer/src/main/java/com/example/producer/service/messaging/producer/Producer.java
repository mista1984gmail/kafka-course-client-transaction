package com.example.producer.service.messaging.producer;

import com.example.producer.service.dto.ClientDto;
import com.example.producer.service.dto.TransactionDto;
//import com.example.producer.service.messaging.event.ClientSendEvent;
import com.example.producer.service.messaging.event.ClientSendEvent;
import com.example.producer.service.messaging.event.TransactionSendEvent;
import com.example.producer.service.messaging.service.KafkaMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

    private final KafkaMessagingService kafkaMessagingService;
    private final ModelMapper modelMapper;

    public ClientDto sendClient(ClientDto clientDto) {
        kafkaMessagingService.sendClient(modelMapper.map(clientDto, ClientSendEvent.class));
        log.info("Send client from producer {}", clientDto);
        return clientDto;
    }


    public TransactionDto sendTransaction(TransactionDto transactionDto) {
        kafkaMessagingService.sendTransaction(modelMapper.map(transactionDto, TransactionSendEvent.class));
        log.info("Send transaction from producer {}", transactionDto);
        return transactionDto;
    }
}
