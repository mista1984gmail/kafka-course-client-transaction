package com.example.consumer.service.dto;

import com.example.consumer.domain.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private List<Transaction> transactions;
}