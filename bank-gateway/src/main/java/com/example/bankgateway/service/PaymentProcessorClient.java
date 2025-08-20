package com.example.bankgateway.service;

import com.example.common.dto.bank.BankResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class PaymentProcessorClient {
    private final WebClient webClient;

    public PaymentProcessorClient(@Value("${payment.processor.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> processTransaction(BankResponse response) {
        return webClient.post()
                .uri("/payment/process")
                .bodyValue(response)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .doOnError(e -> log.warn("Processor unavailable: {}", e.getMessage()))
                .then();
    }
}
