package com.example.paymentauthorizer.service;


import com.example.paymentauthorizer.dto.AuthorizationResponse;
import com.example.paymentauthorizer.dto.BankRequest;
import com.example.paymentauthorizer.dto.PaymentRequest;
import com.example.paymentauthorizer.exception.BankGatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.example.common.constant.ErrorMessages.BANK_TIMEOUT_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final CardValidationClient cardValidationClient;
    private final BankGatewayClient bankGatewayClient;
    private final TransactionService transactionService;

    public Mono<AuthorizationResponse> authorizePayment(PaymentRequest request) {
        return cardValidationClient.validateCard(request.cardData())
                .flatMap(validationResult -> {
                    if (!validationResult.valid()) {
                        log.warn("Card validation failed: {}", validationResult.message());
                        return Mono.just(AuthorizationResponse.declined(
                                "Card validation failed: " + validationResult.message()
                        ));
                    }

                    String transactionId = transactionService.generateTransactionId();
                    BankRequest bankRequest = new BankRequest(
                            transactionId,
                            request.amount(),
                            request.currency(),
                            request.cardData(),
                            request.merchantId()
                    );

                    return bankGatewayClient.authorizeTransaction(bankRequest)
                            .map(bankResponse -> new AuthorizationResponse(
                                    transactionId,
                                    bankResponse.status(),
                                    bankResponse.reason()
                            ))
                            .onErrorResume(e -> handleBankError(e, transactionId));
                })
                .onErrorResume(e -> Mono.just(AuthorizationResponse.error(
                        null, "Validation service error: " + e.getMessage()
                )));
    }

    private Mono<AuthorizationResponse> handleBankError(Throwable e, String transactionId) {
        if (e instanceof BankGatewayTimeoutException) {
            return Mono.just(AuthorizationResponse.error(
                    transactionId, BANK_TIMEOUT_ERROR
            ));
        } else if (e instanceof WebClientResponseException webClientResponseException) {
            return Mono.just(AuthorizationResponse.error(
                    transactionId, "Bank service error: " + (webClientResponseException).getStatusCode()
            ));
        } else {
            return Mono.just(AuthorizationResponse.error(
                    transactionId, "Bank communication error: " + e.getMessage()
            ));
        }
    }


}
