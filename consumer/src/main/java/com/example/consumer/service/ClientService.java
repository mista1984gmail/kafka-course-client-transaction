package com.example.consumer.service;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.service.dto.ClientDto;
import com.example.consumer.service.messaging.event.TransactionEvent;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    List<ClientDto> findAll();
    ClientDto save(ClientDto clientDto);

    ClientDto findById(Long id);

    boolean isExistClient(Long id);
    Client addTransactionToClient(TransactionEvent transactionEvent);
    Optional<Client> findByEmail(String email);
}
