package com.example.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String transactionId,

        @NotBlank
        String status
) {
}
