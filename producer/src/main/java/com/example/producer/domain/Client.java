package com.example.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Client {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
}
