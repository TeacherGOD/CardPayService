package com.example.paymentauthorizer.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {
    public String generateTransactionId() {
        return "txn-" + UUID.randomUUID();
    }
}
