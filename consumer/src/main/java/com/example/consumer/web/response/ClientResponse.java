package com.example.consumer.web.response;

import com.example.consumer.domain.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String telephone;
    private List<Transaction> transactions;
}
