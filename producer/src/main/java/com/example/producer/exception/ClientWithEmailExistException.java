package com.example.producer.exception;

public class ClientWithEmailExistException extends ApplicationException {
  public ClientWithEmailExistException(String message) {
    super(message);
  }
}
