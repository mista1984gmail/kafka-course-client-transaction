package com.example.consumer.domain.repository;

import com.example.consumer.domain.entity.TransactionFailed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionFailedRepository extends JpaRepository<TransactionFailed, Long> {
    List<TransactionFailed> findByIncorrectClientUniqueCode(String code);
}
