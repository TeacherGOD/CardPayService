package com.example.paymentprocessor.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.PaymentStatus;
import com.example.paymentprocessor.entity.Transaction;
import com.example.paymentprocessor.exception.DuplicateTransactionException;
import com.example.paymentprocessor.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@Testcontainers
class PaymentProcessingServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PaymentProcessingService processingService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldProcessApprovedTransaction() {
        BankResponse response = new BankResponse(
                "txn-12345",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(500.00),
                "USD",
                "merch-456"
        );

        var result = processingService.processBankResponse(response);

        assertEquals("txn-12345", result.transactionId());
        assertEquals(PaymentStatus.APPROVED, result.status());

        Optional<Transaction> saved = transactionRepository.findByBankTransactionId("txn-12345");
        assertTrue(saved.isPresent());
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(saved.get().getAmount()));
        assertEquals("USD", saved.get().getCurrency());
    }

    @Test
    void shouldRejectDuplicateTransaction() {
        BankResponse firstResponse = new BankResponse(
                "txn-dup-123",
                PaymentStatus.APPROVED,
                "First",
                BigDecimal.valueOf(100.00),
                "EUR",
                "merch-789"
        );

        processingService.processBankResponse(firstResponse);

        BankResponse duplicateResponse = new BankResponse(
                "txn-dup-123",
                PaymentStatus.DECLINED,
                "Duplicate",
                BigDecimal.valueOf(200.00),
                "USD",
                "merch-000"
        );

        DuplicateTransactionException thrown = assertThrows(DuplicateTransactionException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                processingService.processBankResponse(duplicateResponse);
            }
        });

        assertEquals("Duplicate transaction: txn-dup-123", thrown.getMessage());

        // Проверяем, что в базе осталась только первая транзакция
        assertEquals(1L, transactionRepository.countByBankTransactionId("txn-dup-123"));
        Transaction original = transactionRepository.findByBankTransactionId("txn-dup-123").get();
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(original.getAmount()));
        assertEquals("EUR", original.getCurrency());
        assertEquals("merch-789", original.getMerchantId());
    }
}