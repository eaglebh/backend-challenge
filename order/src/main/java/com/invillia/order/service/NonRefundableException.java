package com.invillia.order.service;

public class NonRefundableException extends Exception {
    public NonRefundableException(String message) {
        super(message);
    }
}
