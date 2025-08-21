package com.example.common.dto.bank;

import com.example.common.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record BankResponse(

        @NotBlank(message = "Bank transaction ID is required")
        String bankTransactionId,

        @NotNull(message = "Status is required")
        PaymentStatus status,

        String reason
) {

}
