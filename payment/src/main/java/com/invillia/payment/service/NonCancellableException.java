package com.invillia.payment.service;

public class NonCancellableException extends Exception {
    public NonCancellableException(String message) {
        super(message);
    }
}
