package com.example.consumer.service.impl;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.domain.entity.Transaction;
import com.example.consumer.domain.repository.ClientRepository;
import com.example.consumer.exception.EntityNotFoundException;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.dto.ClientDto;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<ClientDto> findAll() {
        log.debug("Find all clients");
        return clientRepository.findAll().stream()
                .map(client -> modelMapper.map(client, ClientDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        log.debug("Save client: {}", clientDto);
        return modelMapper.map(clientRepository.save(
                modelMapper.map(clientDto, Client.class)), ClientDto.class);
    }

    @Override
    public ClientDto findById(Long id) {
        log.debug("Find client with id: {}", id);
        return Optional.of(getById(id))
                .map(client -> modelMapper.map(client, ClientDto.class))
                .get();
    }

    @Override
    public boolean isExistClient(Long id) {
        log.info("Checking exist client");
        return clientRepository.existsById(id);
    }

    @Override
    public Client addTransactionToClient(TransactionEvent transactionEvent) {
        Client client = modelMapper.map(findById(transactionEvent.getClientId()), Client.class);

        Transaction transaction = Transaction.builder()
                .bank(transactionEvent.getBank())
                .owner(client)
                .orderType(transactionEvent.getOrderType())
                .quantity(transactionEvent.getQuantity())
                .price(transactionEvent.getPrice())
                .totalCost(new BigDecimal(transactionEvent.getQuantity() * transactionEvent.getPrice()))
                .createdAt(transactionEvent.getCreatedAt())
                .build();

        log.info("Add transaction to client");
        client.addTransaction(transaction);

        return client;
    }

    private Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Client with id: " + id + " not found"));
    }
}
