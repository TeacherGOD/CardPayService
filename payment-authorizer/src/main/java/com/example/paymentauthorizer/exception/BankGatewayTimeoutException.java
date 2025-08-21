package com.example.paymentauthorizer.exception;

public class BankGatewayTimeoutException extends RuntimeException {
    public BankGatewayTimeoutException() {
        super("Bank gateway timeout");
    }
}