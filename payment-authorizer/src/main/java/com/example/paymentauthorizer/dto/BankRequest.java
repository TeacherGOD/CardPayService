package com.example.paymentauthorizer.dto;


import com.example.common.dto.CardData;

import java.math.BigDecimal;

public record BankRequest(

        String transactionId,

        BigDecimal amount,

        String currency,

        CardData cardData,

        String merchantId
) {
}
