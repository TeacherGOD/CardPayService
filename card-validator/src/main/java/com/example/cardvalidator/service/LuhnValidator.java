package com.example.cardvalidator.service;

import org.springframework.stereotype.Component;


@Component
public class LuhnValidator implements Validator {
    public boolean isValid(String cardNumber) {
        var sum = 0;
        var alternate = cardNumber.length()%2!=0;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            var digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

}