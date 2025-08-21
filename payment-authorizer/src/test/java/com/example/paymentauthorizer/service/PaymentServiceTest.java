package com.example.paymentauthorizer.service;

import com.example.common.dto.bank.BankResponse;
import com.example.common.dto.payment.CardData;
import com.example.common.dto.payment.ValidationResult;
import com.example.common.enums.PaymentStatus;
import com.example.common.dto.payment.PaymentRequest;
import com.example.paymentauthorizer.exception.BankGatewayTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static com.example.common.constant.ErrorMessages.BANK_TIMEOUT_ERROR;
import static com.example.common.constant.ErrorMessages.CARD_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CardValidationClient cardValidationClient;

    @Mock
    private BankGatewayClient bankGatewayClient;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldDeclineWhenCardInvalid() {
        PaymentRequest request = createValidRequest();
        ValidationResult invalidCard = new ValidationResult(false, CARD_INVALID);

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.just(invalidCard));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.status() == PaymentStatus.DECLINED &&
                                response.message().contains(CARD_INVALID))
                .verifyComplete();
    }

    @Test
    void shouldApproveWhenAllValid() {
        PaymentRequest request = createValidRequest();
        String transactionId = "txn-" + UUID.randomUUID();
        BankResponse bankResponse = new BankResponse(
                "bank-txn-" + UUID.randomUUID(),
                PaymentStatus.APPROVED,
                "Approved"
        );

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.just(new ValidationResult(true, "Valid")));
        when(transactionService.generateTransactionId())
                .thenReturn(transactionId);
        when(bankGatewayClient.authorizeTransaction(any()))
                .thenReturn(Mono.just(bankResponse));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.transactionId().equals(transactionId) &&
                                response.status() == PaymentStatus.APPROVED)
                .verifyComplete();
    }

    @Test
    void shouldHandleBankError() {
        PaymentRequest request = createValidRequest();
        String transactionId = "txn-12345";

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.just(new ValidationResult(true, "Valid")));
        when(transactionService.generateTransactionId())
                .thenReturn(transactionId);
        when(bankGatewayClient.authorizeTransaction(any()))
                .thenReturn(Mono.error(new RuntimeException("Bank timeout")));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.transactionId().equals(transactionId) &&
                                response.status() == PaymentStatus.ERROR &&
                                response.message().contains("Bank timeout"))
                .verifyComplete();
    }

    @Test
    void shouldHandleValidationError() {
        PaymentRequest request = createValidRequest();

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.error(new RuntimeException("Validation service down")));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.transactionId() == null &&
                                response.status() == PaymentStatus.ERROR &&
                                response.message().contains("Validation service down"))
                .verifyComplete();
    }

    @Test
    void shouldDeclineWhenBankDeclines() {
        PaymentRequest request = createValidRequest();
        String transactionId = "txn-12345";
        BankResponse bankResponse = new BankResponse(
                "bank-txn-" + UUID.randomUUID(),
                PaymentStatus.DECLINED,
                "Insufficient funds"
        );

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.just(new ValidationResult(true, "Valid")));
        when(transactionService.generateTransactionId())
                .thenReturn(transactionId);
        when(bankGatewayClient.authorizeTransaction(any()))
                .thenReturn(Mono.just(bankResponse));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.transactionId().equals(transactionId) &&
                                response.status() == PaymentStatus.DECLINED &&
                                response.message().contains("Insufficient funds"))
                .verifyComplete();
    }

    @Test
    void shouldHandleTimeout() {
        PaymentRequest request = createValidRequest();

        when(cardValidationClient.validateCard(any()))
                .thenReturn(Mono.just(new ValidationResult(true, "Valid card")));

        String transactionId = "txn-12345";
        when(transactionService.generateTransactionId())
                .thenReturn(transactionId);

        when(bankGatewayClient.authorizeTransaction(any()))
                .thenReturn(Mono.delay(Duration.ofSeconds(6))
                        .then(Mono.error(new BankGatewayTimeoutException())));

        StepVerifier.create(paymentService.authorizePayment(request))
                .expectNextMatches(response ->
                        response.status() == PaymentStatus.ERROR &&
                                response.message().equals(BANK_TIMEOUT_ERROR) &&
                                response.transactionId().equals(transactionId)
                )
                .verifyComplete();
    }

    private PaymentRequest createValidRequest() {
        return new PaymentRequest(
                BigDecimal.valueOf(100.50),
                "USD",
                new CardData("4111111111111111", "12/25", "123"),
                "merchant-123"
        );
    }
}