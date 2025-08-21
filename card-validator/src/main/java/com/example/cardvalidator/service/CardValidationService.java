package com.example.cardvalidator.service;


import com.example.common.dto.CardData;
import com.example.common.dto.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.example.common.constant.ErrorMessages.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardValidationService {


    private final Validator validator;

    public ValidationResult validateCard(CardData cardData) {
        if (!validator.isValid(cardData.cardNumber().trim())) {
            return new ValidationResult(false, CARD_INVALID);
        }
        YearMonth expiry = YearMonth.parse(
                cardData.expiryDate(),
                DateTimeFormatter.ofPattern("MM/yy")
        );
        if (expiry.isBefore(YearMonth.now())) {
            return new ValidationResult(false, CARD_EXPIRED);
        }
        log.info(String.format("Card Validated Successfully %s", cardData.cardNumber()));
        return new ValidationResult(true, CARD_VALID);
    }
}
