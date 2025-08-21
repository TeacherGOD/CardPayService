package com.example.common.dto;

import com.example.common.enums.FinalPaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRecordResponse(

        Long transactionId,

        BigDecimal amount,

        String currency,

        FinalPaymentStatus status,

        LocalDateTime createdAt,

        String bankTransactionId,

        String reason,

        String merchantId
) {}