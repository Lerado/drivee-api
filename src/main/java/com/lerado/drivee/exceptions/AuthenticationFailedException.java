package com.lerado.drivee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends AuthenticationException {

    public AuthenticationFailedException(String msg) {
        super(msg);
    }
    
}
