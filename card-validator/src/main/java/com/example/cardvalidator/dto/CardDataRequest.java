package com.example.cardvalidator.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные пластиковой карты для проверки")
public record CardDataRequest(

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d+", message = "Card number must contain only digits")
        @Schema(
                description = "Номер карты",
                example = "4111111111111111",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cardNumber,

        @NotBlank(message = "Expiry date is required")
        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/yy format")
        @Schema(
                description = "Срок действия в формате MM/yy",
                example = "12/30",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String expiryDate,

        @NotBlank(message = "CVV is required")
        @Size(min = 3, max = 3, message = "CVV must be 3 digits")
        @Pattern(regexp = "\\d{3}", message = "CVV must contain only digits")
        @Schema(
                description = "CVV/CVC код (3 цифры)",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String cvv
        ) {
}
