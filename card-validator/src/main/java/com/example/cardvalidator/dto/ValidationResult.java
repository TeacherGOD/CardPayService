package com.example.cardvalidator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.example.cardvalidator.constant.ErrorMessages.CARD_VALID;

@Schema(description = "Результат проверки карты")
public record ValidationResult(

        @Schema(
                description = "Флаг валидности карты",
                example = "true",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        boolean valid,

        @Schema(
                description = "Детальное сообщение о результате проверки",
                example = CARD_VALID,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message
) {
}
