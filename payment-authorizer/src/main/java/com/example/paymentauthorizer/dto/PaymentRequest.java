package com.example.paymentauthorizer.dto;



import com.example.common.dto.CardData;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
        String currency,

        @NotNull(message = "Card data is required")
        CardData cardData,

        @NotBlank(message = "Merchant ID is required")
        String merchantId
) {
}
