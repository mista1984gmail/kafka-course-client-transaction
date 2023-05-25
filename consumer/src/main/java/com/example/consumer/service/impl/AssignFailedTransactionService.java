package com.example.consumer.service.impl;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.domain.entity.Transaction;
import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.TransactionFailedService;
import com.example.consumer.service.TransactionService;
import com.example.consumer.service.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignFailedTransactionService {

    private final TransactionFailedService transactionFailedService;
    private final TransactionService transactionService;
    private final ClientService clientService;
    private final ModelMapper modelMapper;

    @Transactional
    public void assignFailedTransactionToClient(){
        log.info("Find all failed transaction");
        List<TransactionFailed> transactionFaileds = transactionFailedService.findAll();
        log.info("On schedule: checking for a valid client");
        Set<Long> clientsIds = transactionFaileds.stream()
                .map(TransactionFailed::getIncorrectId).collect(Collectors.toSet());
        for (Long id : clientsIds) {
            if(clientService.isExistClient(id)){
                Client client = modelMapper.map(clientService.findById(id), Client.class);
                List<TransactionFailed>transactionFailedWithFoundClient = transactionFailedService.findByIncorrectId(id);
                for (TransactionFailed transactionFailed : transactionFailedWithFoundClient) {
                    Transaction transaction = Transaction.builder()
                            .bank(transactionFailed.getBank())
                            .owner(client)
                            .orderType(transactionFailed.getOrderType())
                            .quantity(transactionFailed.getQuantity())
                            .price(transactionFailed.getPrice())
                            .totalCost(new BigDecimal(transactionFailed.getQuantity() * transactionFailed.getPrice()))
                            .createdAt(transactionFailed.getCreatedAt())
                            .build();
                    transactionService.save(modelMapper.map(transaction, TransactionDto.class));
                    transactionFailedService.delete(transactionFailed.getId());
                }
            }
        }
    }
}
