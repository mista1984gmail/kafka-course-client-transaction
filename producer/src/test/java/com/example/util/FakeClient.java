package com.example.util;

import com.example.producer.service.messaging.event.ClientSendEvent;


public class FakeClient {

    public static ClientSendEvent getClientSendEvent(){
        return new ClientSendEvent(
                "Ivan",
                "Ivanov",
                "Grodno, Pobedy, 123/45",
                "ivan2000@gmail.com");
    }

}
