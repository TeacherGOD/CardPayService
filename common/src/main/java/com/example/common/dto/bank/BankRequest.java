package com.example.common.dto.bank;

import com.example.common.dto.payment.CardData;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record BankRequest(

        @NotNull(message = "Card data is required")
        CardData cardData,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
        String currency,

        @NotBlank
        String merchantId
) {
}
