package com.example.paymentauthorizer.service;

import com.example.common.enums.PaymentStatus;
import com.example.paymentauthorizer.dto.BankRequest;
import com.example.paymentauthorizer.dto.BankResponse;
import com.example.paymentauthorizer.exception.BankGatewayTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class BankGatewayClient {
    private final WebClient webClient;
    private final Duration timeout;

    public BankGatewayClient(
            WebClient.Builder webClientBuilder,
            @Value("${bank.gateway.url}") String baseUrl,
            @Value("${bank.gateway.timeout:5000}") long timeoutMillis
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.timeout = Duration.ofMillis(timeoutMillis);
    }

    public Mono<BankResponse> authorizeTransaction(BankRequest request) {

        BankResponse successResponse = new BankResponse(request.transactionId(), PaymentStatus.APPROVED, "APPROVED");
        return Mono.just(successResponse)
                .doOnSubscribe(sub -> log.info("Stub: Sending to bank: {}", request))
                .doOnSuccess(res -> log.info("Stub: Bank response: {}", res));

//        return webClient.post()
//                .uri("/authorize")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(BankResponse.class)
//                .timeout(timeout, Mono.error(new BankGatewayTimeoutException()))
//                .doOnSubscribe(sub -> log.info("Sending to bank: {}", request))
//                .doOnSuccess(res -> log.info("Bank response: {}", res))
//                .doOnError(e -> log.error("Bank communication error", e));
    }
}
