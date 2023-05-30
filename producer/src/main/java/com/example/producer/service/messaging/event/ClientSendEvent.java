package com.example.producer.service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSendEvent {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String telephone;
    private String clientCode;
}
