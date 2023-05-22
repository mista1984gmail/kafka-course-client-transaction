package com.example.producer.service;


import com.example.producer.service.dto.ClientDto;

public interface ClientService {
    ClientDto sendClient(ClientDto clientDto);
}
