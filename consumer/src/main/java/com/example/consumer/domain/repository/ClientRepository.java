package com.example.consumer.domain.repository;

import com.example.consumer.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsById(Long id);
    Client findByEmail(String email);
}
