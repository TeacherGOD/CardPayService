package com.example.paymentprocessor.entity;

import com.example.common.enums.FinalPaymentStatus;

import java.util.UUID;

public record FinalTransaction(

        String transactionId,

        FinalPaymentStatus status,

        String message
) {
}
