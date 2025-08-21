package com.example.paymentprocessor.service;
import com.example.common.dto.TransactionRecordRequest;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessingServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransactionRecorderClient transactionRecorderClient;

    @InjectMocks
    private PaymentProcessingService processingService;

    @Test
    void shouldProcessApprovedTransaction() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-123",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(750),
                "RUB",
                "merch-333"
        );

        when(transactionRecorderClient.recordTransaction(any(TransactionRecordRequest.class)))
                .thenReturn(Mono.empty());

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-123", result.transactionId());
        assertEquals(FinalPaymentStatus.APPROVED, result.status());
        assertEquals("Approved", result.message());

        ArgumentCaptor<TransactionProcessedEvent> eventCaptor = ArgumentCaptor.forClass(TransactionProcessedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(result, eventCaptor.getValue().getTransaction());

        ArgumentCaptor<TransactionRecordRequest> recordCaptor = ArgumentCaptor.forClass(TransactionRecordRequest.class);
        verify(transactionRecorderClient).recordTransaction(recordCaptor.capture());

        TransactionRecordRequest recordedRequest = recordCaptor.getValue();
        assertEquals(BigDecimal.valueOf(750), recordedRequest.amount());
        assertEquals("RUB", recordedRequest.currency());
        assertEquals(FinalPaymentStatus.APPROVED, recordedRequest.status());
        assertEquals("bank-txn-123", recordedRequest.bankTransactionId());
        assertEquals("Approved", recordedRequest.reason());
        assertEquals("merch-333", recordedRequest.merchantId());
    }

    @Test
    void shouldProcessDeclinedTransaction() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-456",
                PaymentStatus.DECLINED,
                "Insufficient funds",
                BigDecimal.valueOf(750),
                "RUB",
                "merch-333"
        );

        when(transactionRecorderClient.recordTransaction(any(TransactionRecordRequest.class)))
                .thenReturn(Mono.empty());

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-456", result.transactionId());
        assertEquals(FinalPaymentStatus.DECLINED, result.status());
        assertEquals("Insufficient funds", result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
        verify(transactionRecorderClient).recordTransaction(any(TransactionRecordRequest.class));
    }

    @Test
    void shouldProcessDeclinedTransactionWhenStatusIsError() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-332",
                PaymentStatus.ERROR,
                "Connection Error",
                BigDecimal.valueOf(750),
                "RUB",
                "merch-333"
        );

        when(transactionRecorderClient.recordTransaction(any(TransactionRecordRequest.class)))
                .thenReturn(Mono.empty());

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-332", result.transactionId());
        assertEquals(FinalPaymentStatus.DECLINED, result.status());
        assertEquals("Connection Error", result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
        verify(transactionRecorderClient).recordTransaction(any(TransactionRecordRequest.class));
    }

    @Test
    void shouldProcessTransactionWithEmptyReason() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-789",
                PaymentStatus.APPROVED,
                null,
                BigDecimal.valueOf(750),
                "RUB",
                "merch-333"
        );

        when(transactionRecorderClient.recordTransaction(any(TransactionRecordRequest.class)))
                .thenReturn(Mono.empty());

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-789", result.transactionId());
        assertEquals(FinalPaymentStatus.APPROVED, result.status());
        assertNull(result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
        verify(transactionRecorderClient).recordTransaction(any(TransactionRecordRequest.class));
    }

    @Test
    void shouldHandleTransactionRecordingFailure() {
        BankResponse bankResponse = new BankResponse(
                "bank-txn-999",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(750),
                "RUB",
                "merch-333"
        );

        when(transactionRecorderClient.recordTransaction(any(TransactionRecordRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Recording failed")));

        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);

        assertNotNull(result);
        assertEquals("bank-txn-999", result.transactionId());
        assertEquals(FinalPaymentStatus.APPROVED, result.status());
        assertEquals("Approved", result.message());

        verify(eventPublisher).publishEvent(any(TransactionProcessedEvent.class));
        verify(transactionRecorderClient).recordTransaction(any(TransactionRecordRequest.class));
    }
}