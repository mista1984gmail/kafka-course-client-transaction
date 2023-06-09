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
    private String telephone;
    private String email;
    private String clientCode;
    private List<Transaction> transactions;

    public ClientDto(String firstName, String lastName, String address, String email, String telephone, String clientCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
        this.clientCode = clientCode;
    }
}
