package com.example.bankgateway.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.dto.bank.BankRequest;
import com.example.common.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankService {

    private final PaymentProcessorClient  paymentProcessorClient ;
    private static final BigDecimal MAX_APPROVED_AMOUNT = new BigDecimal("10000");


    public BankResponse authorizeTransaction(BankRequest request) {
        String bankTransactionId = "bank-txn-" + UUID.randomUUID();

        BankResponse response;

        if (request.amount().compareTo(MAX_APPROVED_AMOUNT) < 0) {
            response = new BankResponse(
                    bankTransactionId,
                    PaymentStatus.APPROVED,
                    "Approved"
            );
        } else {
            response = new BankResponse(
                    bankTransactionId,
                    PaymentStatus.DECLINED,
                    "Amount exceeds limit"
            );
        }

        paymentProcessorClient.processTransaction(response)
                .doOnError(error -> log.error("Failed to send to processor: {}", error.getMessage()))
                .subscribe();

        return response;
    }
}
