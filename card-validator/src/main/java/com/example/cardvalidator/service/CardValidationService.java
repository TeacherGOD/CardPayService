package com.example.cardvalidator.service;

import com.example.cardvalidator.dto.CardDataRequest;
import com.example.cardvalidator.dto.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.example.cardvalidator.constant.ErrorMessages.*;


@Service
@RequiredArgsConstructor
public class CardValidationService {


    private final Validator validator;

    public ValidationResult validateCard(CardDataRequest cardData) {
        if (!validator.isValid(cardData.cardNumber().trim())) {
            return new ValidationResult(false, CARD_INVALID_NUMBER);
        }
        YearMonth expiry = YearMonth.parse(
                cardData.expiryDate(),
                DateTimeFormatter.ofPattern("MM/yy")
        );
        if (expiry.isBefore(YearMonth.now())) {
            return new ValidationResult(false, CARD_EXPIRED);
        }
        return new ValidationResult(true, CARD_VALID);
    }
}
