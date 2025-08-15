package com.example.cardvalidator.controller;


import com.example.cardvalidator.service.CardValidationService;
import com.example.common.dto.payment.CardData;
import com.example.common.dto.payment.ValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/card")
public class CardController {
    private final CardValidationService validationService;

    public CardController(CardValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/validate")
    @Operation(
            summary = "Validate card details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешная проверка",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationResult.class),
                    examples = {
                            @ExampleObject(
                                    name = "valid_card",
                                    value = "{\"valid\": true, \"message\": \"Card is valid\"}"),
                            @ExampleObject(
                                    name = "invalid_card",
                                    value = "{\"valid\": false, \"message\": \"Invalid card number\"}")
                    }
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные входные данные",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationResult.class),
                    examples = {
                            @ExampleObject(
                                    name = "invalid_input",
                                    value = "{\"valid\": false, \"message\": \"expiryDate: Expiry date must be in MM/yy format; cvv: CVV must contain only digits\"}")
                    }
            )
    )
    public ValidationResult validateCard(@Valid @RequestBody CardData cardData) {
        return validationService.validateCard(cardData);
    }
}