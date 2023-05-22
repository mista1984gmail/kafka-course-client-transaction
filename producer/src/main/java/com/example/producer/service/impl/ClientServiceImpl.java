package com.example.producer.service.impl;

import com.example.producer.service.ClientService;
import com.example.producer.service.dto.ClientDto;
import com.example.producer.service.messaging.producer.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final Producer producer;

    @Override
    public ClientDto sendClient(ClientDto clientDto) {
        log.info("Send client from service {}", clientDto);
        return producer.sendClient(clientDto);
    }
}
