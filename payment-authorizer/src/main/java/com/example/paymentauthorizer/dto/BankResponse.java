package com.example.paymentauthorizer.dto;

import com.example.common.enums.PaymentStatus;

public record BankResponse(

        String transactionId,

        PaymentStatus status,

        String reason
) {
}
