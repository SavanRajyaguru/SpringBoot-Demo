package com.crystal.store.exception;

public class Unauthorize extends RuntimeException {
    public Unauthorize(String message) {
        super(message);
    }

    public Unauthorize(String message, Throwable cause) {
        super(message, cause);
    }
}
