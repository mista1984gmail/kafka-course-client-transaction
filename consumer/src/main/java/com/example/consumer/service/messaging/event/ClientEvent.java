package com.example.consumer.service.messaging.event;

import com.example.consumer.domain.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEvent {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String telephone;
    private String clientCode;
}
