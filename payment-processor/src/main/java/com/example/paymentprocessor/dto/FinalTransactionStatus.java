package com.example.paymentprocessor.dto;

import com.example.common.enums.PaymentStatus;

public record FinalTransactionStatus(

        String transactionId,

        PaymentStatus status,

        String message
) {
}
