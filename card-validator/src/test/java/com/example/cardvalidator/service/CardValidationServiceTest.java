package com.example.cardvalidator.service;


import com.example.common.dto.payment.CardData;
import com.example.common.dto.payment.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.common.constant.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardValidationServiceTest {

    @Mock
    private LuhnValidator luhnValidator;
    @InjectMocks
    private CardValidationService validationService;

    @Test
    void validCardShouldReturnSuccess() {
        var cardData = new CardData(
                "4111111111111111",
                "12/30",
                "123"
        );

        when(luhnValidator.isValid("4111111111111111")).thenReturn(true);

        ValidationResult result = validationService.validateCard(cardData);

        assertTrue(result.valid());
        assertEquals(CARD_VALID, result.message());
    }

    @Test
    void invalidLuhnShouldReturnError() {
        var cardData = new CardData(
                "4111111111111112",
                "12/30",
                "123"
        );

        when(luhnValidator.isValid("4111111111111112")).thenReturn(false);

        ValidationResult result = validationService.validateCard(cardData);

        assertFalse(result.valid());
        assertTrue(result.message().contains(CARD_INVALID));
    }

    @Test
    void expiredCardShouldReturnError() {
        var cardData = new CardData(
                "4111111111111111",
                "12/20",
                "123"
        );

        when(luhnValidator.isValid("4111111111111111")).thenReturn(true);

        ValidationResult result = validationService.validateCard(cardData);

        assertFalse(result.valid());
        assertEquals(CARD_EXPIRED, result.message());
    }
}