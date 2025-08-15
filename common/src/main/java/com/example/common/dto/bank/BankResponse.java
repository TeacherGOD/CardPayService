package com.example.common.dto.bank;

import com.example.common.enums.PaymentStatus;

public record BankResponse(

        String bankTransactionId,

        PaymentStatus status,

        String reason
) {

}
