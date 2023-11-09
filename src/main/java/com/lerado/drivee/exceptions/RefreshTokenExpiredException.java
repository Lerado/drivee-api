package com.lerado.drivee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException() {
        super("Refresh token expired. Full authentication required");
    }

    public RefreshTokenExpiredException(String msg) {
        super(msg);
    }

    public RefreshTokenExpiredException(String msg, Throwable exception) {
        super(msg, exception);
    }
}
