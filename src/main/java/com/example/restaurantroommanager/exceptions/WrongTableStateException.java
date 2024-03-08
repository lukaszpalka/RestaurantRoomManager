package com.example.restaurantroommanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class WrongTableStateException extends RuntimeException {
    public WrongTableStateException(String message) {
    }
}
