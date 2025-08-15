package com.example.paymentauthorizer.service;

import com.example.common.dto.bank.BankRequest;
import com.example.common.dto.bank.BankResponse;
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
    private final WebClient bankWebClient;
    private final WebClient processorWebClient;
    private final Duration timeout;

    public BankGatewayClient(
            WebClient.Builder webClientBuilder,
            @Value("${bank.gateway.url}") String bankUrl,
            @Value("${payment.processor.url}") String processorUrl,
            @Value("${bank.gateway.timeout:5000}") long timeoutMillis
    ) {
        this.bankWebClient = webClientBuilder.clone().baseUrl(bankUrl).build();
        this.processorWebClient = webClientBuilder.clone().baseUrl(processorUrl).build();
        this.timeout = Duration.ofMillis(timeoutMillis);
    }

    public Mono<BankResponse> authorizeTransaction(BankRequest request) {

        return bankWebClient.post()
                .uri("/bank/authorize")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BankResponse.class)
                .flatMap(response -> processorWebClient.post()
                        .uri("/payment/process")
                        .bodyValue(response)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .thenReturn(response))
                .timeout(timeout, Mono.error(new BankGatewayTimeoutException()))
                .doOnSubscribe(sub -> log.info("Sending to bank: {}", request))
                .doOnSuccess(res -> log.info("Bank response: {}", res))
                .doOnError(e -> log.error("Bank communication error", e));
    }
}
