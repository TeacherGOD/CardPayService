package com.example.bankgateway.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.dto.bank.BankRequest;
import com.example.common.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BankService {

    private static final BigDecimal MAX_APPROVED_AMOUNT = new BigDecimal("10000");

    public BankResponse authorizeTransaction(BankRequest request) {
        String bankTransactionId = "bank-txn-" + UUID.randomUUID();

        if (request.amount().compareTo(MAX_APPROVED_AMOUNT) < 0) {
            return new BankResponse(
                    bankTransactionId,
                    PaymentStatus.APPROVED,
                    "Approved transaction (less than " + MAX_APPROVED_AMOUNT + ")",
                    request.amount(),
                    request.currency(),
                    request.merchantId()
            );
        } else {
            return new BankResponse(
                    bankTransactionId,
                    PaymentStatus.DECLINED,
                    "Amount exceeds limit of 10000",
                    request.amount(),
                    request.currency(),
                    request.merchantId()
            );
        }
    }
}
