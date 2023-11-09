package com.lerado.drivee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
