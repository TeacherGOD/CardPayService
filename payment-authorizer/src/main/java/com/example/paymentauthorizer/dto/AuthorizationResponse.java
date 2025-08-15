package com.example.paymentauthorizer.dto;


import com.example.common.enums.PaymentStatus;

public record AuthorizationResponse(
        String transactionId,
        PaymentStatus status,
        String message
) {
    public static AuthorizationResponse approved(String message) {
        return new AuthorizationResponse(null, PaymentStatus.APPROVED, message);
    }

    public static AuthorizationResponse declined(String message) {
        return new AuthorizationResponse(null, PaymentStatus.DECLINED, message);
    }

    public static AuthorizationResponse error(String transactionId, String message) {
        return new AuthorizationResponse(transactionId, PaymentStatus.ERROR, message);
    }
}
