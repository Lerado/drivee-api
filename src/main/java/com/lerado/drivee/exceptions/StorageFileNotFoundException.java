package com.lerado.drivee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String msg) {
        super(msg);
    }

    public StorageFileNotFoundException(String msg, Throwable exception) {
        super(msg, exception);
    }
    
}
