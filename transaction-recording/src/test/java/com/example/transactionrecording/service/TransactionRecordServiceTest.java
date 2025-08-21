package com.example.transactionrecording.service;

import com.example.common.dto.TransactionRecordRequest;
import com.example.common.dto.TransactionRecordResponse;
import com.example.common.enums.FinalPaymentStatus;
import com.example.transactionrecording.entity.Transaction;
import com.example.transactionrecording.repository.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRecordServiceTest {

    @Mock
    private TransactionRecordRepository repository;

    @InjectMocks
    private TransactionRecordService service;

    @Test
    void saveTransaction_ShouldSaveAndReturnResponse() {
        TransactionRecordRequest request = new TransactionRecordRequest(
                BigDecimal.valueOf(100.50),
                "USD",
                FinalPaymentStatus.APPROVED,
                "bank-txn-456",
                "Approved",
                "merch-789"
        );

        Transaction savedEntity = new Transaction();
        savedEntity.setId(1L);
        savedEntity.setAmount(request.amount());
        savedEntity.setCurrency(request.currency());
        savedEntity.setStatus(request.status());
        savedEntity.setBankTransactionId(request.bankTransactionId());
        savedEntity.setReason(request.reason());
        savedEntity.setMerchantId(request.merchantId());
        savedEntity.setCreatedAt(LocalDateTime.now());

        when(repository.save(any(Transaction.class))).thenReturn(savedEntity);

        TransactionRecordResponse response = service.saveTransaction(request);

        assertNotNull(response);
        assertEquals(1L, response.transactionId());
        assertEquals(BigDecimal.valueOf(100.50), response.amount());
        assertEquals("USD", response.currency());
        assertEquals(FinalPaymentStatus.APPROVED, response.status());
        assertEquals("bank-txn-456", response.bankTransactionId());
        assertEquals("Approved", response.reason());
        assertEquals("merch-789", response.merchantId());
        assertNotNull(response.createdAt());

        verify(repository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransaction_WhenExists_ShouldReturnResponse() {
        var bankTransactionId = "bank-txn-456";
        Transaction savedEntity = new Transaction();
        savedEntity.setId(1L);
        savedEntity.setAmount(BigDecimal.valueOf(100.50));
        savedEntity.setCurrency("USD");
        savedEntity.setStatus(FinalPaymentStatus.APPROVED);
        savedEntity.setBankTransactionId(bankTransactionId);
        savedEntity.setReason("Approved");
        savedEntity.setMerchantId("merch-789");
        savedEntity.setCreatedAt(LocalDateTime.now());

        when(repository.findByBankTransactionId(bankTransactionId)).thenReturn(Optional.of(savedEntity));

        TransactionRecordResponse response = service.getTransaction(bankTransactionId);

        assertNotNull(response);
        assertEquals(1L, response.transactionId());
        assertEquals(BigDecimal.valueOf(100.50), response.amount());
        assertEquals("USD", response.currency());
        assertEquals(FinalPaymentStatus.APPROVED, response.status());
        assertEquals(bankTransactionId, response.bankTransactionId());
        assertEquals("Approved", response.reason());
        assertEquals("merch-789", response.merchantId());
        assertNotNull(response.createdAt());

        verify(repository, times(1)).findByBankTransactionId(bankTransactionId);
    }

    @Test
    void getTransaction_WhenNotExists_ShouldThrowException() {
        var bankTransactionId ="bank-txn-456";
        when(repository.findByBankTransactionId(bankTransactionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getTransaction(bankTransactionId));
        verify(repository, times(1)).findByBankTransactionId(bankTransactionId);
    }
}