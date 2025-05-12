package com.ssafy.happymeal.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoMealLogFoundException extends RuntimeException{
    public NoMealLogFoundException(String message) {
        super(message);
    }
}
