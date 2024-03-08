package com.example.restaurantroommanager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotPositiveNumberException extends RuntimeException {
    public NotPositiveNumberException(String message) {
        super(message);
    }
}
