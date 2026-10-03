package com.store.exception;

public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(int quantity) {
        super("Quantity must be greater than 0 but was " + quantity);
    }
}
