package com.example.common.dto.bank;

import com.example.common.enums.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BankResponse(

        @NotBlank(message = "Bank transaction ID is required")
        String bankTransactionId,

        @NotNull(message = "Status is required")
        PaymentStatus status,

        String reason,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
        String currency,

        @NotBlank(message = "Merchant ID is required")
        String merchantId,

        @NotBlank
        @Email
        String email
) {

}
