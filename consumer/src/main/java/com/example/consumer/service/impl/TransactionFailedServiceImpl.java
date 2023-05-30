package com.example.consumer.service.impl;

import com.example.consumer.config.ErrorName;
import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.domain.repository.TransactionFailedRepository;
import com.example.consumer.service.TransactionFailedService;
import com.example.consumer.service.messaging.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionFailedServiceImpl implements TransactionFailedService {

    private final TransactionFailedRepository transactionFailedRepository;

    @Override
    @Transactional
    public TransactionFailed save(TransactionFailed transactionFailed) {
        log.debug("Save failed transaction: {}", transactionFailed);
        return transactionFailedRepository.save(transactionFailed);
    }

    @Override
    public TransactionFailed getTransactionFailed(TransactionEvent transactionEvent) {
        log.info("Get failed transaction from event transaction");
        return TransactionFailed.builder()
                .bank(transactionEvent.getBank())
                .orderType(transactionEvent.getOrderType())
                .quantity(transactionEvent.getQuantity())
                .price(transactionEvent.getPrice())
                .totalCost(new BigDecimal(transactionEvent.getQuantity() * transactionEvent.getPrice()))
                .createdAt(transactionEvent.getCreatedAt())
                .incorrectClientUniqueCode(transactionEvent.getClientUniqueCode())
                .error(ErrorName.NOT_FOUND_CLIENT_ID)
                .build();
    }

    @Override
    public List<TransactionFailed> findAll() {
        log.info("Find all failed transactions");
        return transactionFailedRepository.findAll();
    }

    @Override
    public List<TransactionFailed> findByIncorrectClientUniqueCode(String code) {
        log.info("Find all failed transaction by incorrect client code");
        return transactionFailedRepository.findByIncorrectClientUniqueCode(code);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Delete failed transaction by id: " + id);
        transactionFailedRepository.deleteById(id);
    }
}
