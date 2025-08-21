package com.example.paymentprocessor.service;


import com.example.common.dto.TransactionRecordRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class TransactionRecorderClient {
    private final WebClient webClient;

    public TransactionRecorderClient(@Value("${services.transaction-recorder.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> recordTransaction(TransactionRecordRequest request) {
        return webClient.post()
                .uri("/transactions")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .doOnError(e -> log.error("Transaction recorder unavailable: {}", e.getMessage()))
                .then();
    }
}
