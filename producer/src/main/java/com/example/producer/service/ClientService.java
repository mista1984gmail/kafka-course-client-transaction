package com.example.producer.service;


import com.example.producer.domain.Client;
import com.example.producer.service.dto.ClientDto;

import java.util.Optional;

public interface ClientService {
    ClientDto sendClient(ClientDto clientDto);
    Optional<Client> findByEmail(String email);
}
