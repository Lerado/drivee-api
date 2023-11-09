package com.lerado.drivee.exceptions;

public class StorageException extends RuntimeException {

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable exception) {
        super(msg, exception);
    }
}
