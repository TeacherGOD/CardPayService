package com.example.common.dto.payment;


import com.example.common.enums.PaymentStatus;

public record AuthorizationResponse(

        String transactionId,

        PaymentStatus status,

        String message
) {

    public static AuthorizationResponse declined(String message) {
        return new AuthorizationResponse(null, PaymentStatus.DECLINED, message);
    }

    public static AuthorizationResponse error(String transactionId, String message) {
        return new AuthorizationResponse(transactionId, PaymentStatus.ERROR, message);
    }
}
