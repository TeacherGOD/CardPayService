package com.example.paymentprocessor.dto;

import com.example.common.enums.FinalPaymentStatus;
import jakarta.validation.constraints.NotNull;

public record FinalTransactionStatus(

        @NotNull
        String transactionId,

        @NotNull
        FinalPaymentStatus status,

        String message
) {
}
