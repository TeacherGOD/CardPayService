package com.example.paymentprocessor.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.FinalPaymentStatus;
import com.example.common.enums.PaymentStatus;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.event.TransactionProcessedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentProcessingService processingService;

    @Test
    void shouldProcessApprovedTransaction() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-123",
                PaymentStatus.APPROVED,
                "Approved"
        );

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-123", result.transactionId());
        assertEquals(FinalPaymentStatus.APPROVED, result.status());
        assertEquals("Approved", result.message());

        ArgumentCaptor<TransactionProcessedEvent> eventCaptor = ArgumentCaptor.forClass(TransactionProcessedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TransactionProcessedEvent event = eventCaptor.getValue();
        assertEquals(result, event.getTransaction());
    }

    @Test
    void shouldProcessDeclinedTransaction() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-456",
                PaymentStatus.DECLINED,
                "Insufficient funds"
        );

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-456", result.transactionId());
        assertEquals(FinalPaymentStatus.DECLINED, result.status());
        assertEquals("Insufficient funds", result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
    }
    @Test
    void shouldProcessDeclinedTransactionWhenStatusIsError() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-332",
                PaymentStatus.ERROR,
                "Connection Error"
        );

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-332", result.transactionId());
        assertEquals(FinalPaymentStatus.DECLINED, result.status());
        assertEquals("Connection Error", result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
    }

    @Test
    void shouldProcessTransactionWithEmptyReason() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-789",
                PaymentStatus.APPROVED,
                null
        );

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-789", result.transactionId());
        assertEquals(FinalPaymentStatus.APPROVED, result.status());
        assertNull(result.message());
    }
}