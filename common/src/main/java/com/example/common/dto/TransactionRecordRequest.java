package com.example.common.dto;

import com.example.common.enums.FinalPaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransactionRecordRequest(

        @Positive @NotNull
        BigDecimal amount,

        @NotNull(message = "Amount is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
        String currency,

        @NotNull
        FinalPaymentStatus status,

        @NotBlank
        String bankTransactionId,

        String reason,

        @NotBlank
        String merchantId
) {}