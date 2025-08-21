package com.example.paymentprocessor.dto;

import com.example.common.enums.FinalPaymentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(

        @NotNull
        @Email
        String email,

        @NotNull
        String transactionId,

        @NotNull
        FinalPaymentStatus status
) {
}
