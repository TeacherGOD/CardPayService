package com.example.bankgateway.service;

import com.example.common.dto.bank.BankRequest;
import com.example.common.dto.bank.BankResponse;
import com.example.common.dto.payment.CardData;
import com.example.common.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private PaymentProcessorClient paymentProcessorClient;

    @InjectMocks
    private BankService bankService;

    @Test
    void shouldApproveTransactionWhenAmountBelowLimit() {
        BankRequest request = new BankRequest(
                new CardData("4111111111111111", "12/25", "123"),
                BigDecimal.valueOf(9999.99),
                "USD",
                "merchant-123"
        );

        when(paymentProcessorClient.processTransaction(any()))
                .thenReturn(Mono.empty());

        BankResponse response = bankService.authorizeTransaction(request);

        assertNotNull(response.bankTransactionId());
        assertTrue(response.bankTransactionId().startsWith("bank-txn-"));
        assertEquals(PaymentStatus.APPROVED, response.status());
        assertEquals("Approved", response.reason());

        verify(paymentProcessorClient).processTransaction(any());
    }

    @Test
    void shouldDeclineTransactionWhenAmountExceedsLimit() {
        BankRequest request = new BankRequest(
                new CardData("4111111111111111", "12/25", "123"),
                BigDecimal.valueOf(10000.01),
                "USD",
                "merchant-123"
        );

        when(paymentProcessorClient.processTransaction(any()))
                .thenReturn(Mono.empty());

        BankResponse response = bankService.authorizeTransaction(request);

        assertNotNull(response.bankTransactionId());
        assertTrue(response.bankTransactionId().startsWith("bank-txn-"));
        assertEquals(PaymentStatus.DECLINED, response.status());
        assertEquals("Amount exceeds limit", response.reason());

        verify(paymentProcessorClient).processTransaction(any());
    }

    @Test
    void shouldSendToProcessorEvenWhenProcessorFails() {
        BankRequest request = new BankRequest(
                new CardData("4111111111111111", "12/25", "123"),
                BigDecimal.valueOf(5000.00),
                "USD",
                "merchant-123"
        );

        when(paymentProcessorClient.processTransaction(any()))
                .thenReturn(Mono.error(new RuntimeException("Processor unavailable")));

        BankResponse response = bankService.authorizeTransaction(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.APPROVED, response.status());

        verify(paymentProcessorClient).processTransaction(any());
    }
}