package com.example.common.dto.payment;

import com.example.common.constant.ErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;


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
                example = ErrorMessages.CARD_VALID,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message
) {
}
