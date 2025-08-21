package com.example.paymentprocessor.service;

import com.example.paymentprocessor.dto.NotificationRequestDto;
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
public class NotificationClient {
    private final WebClient webClient;

    public NotificationClient(
            @Value("${services.notification.url}") String baseUrl,
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Void> sendNotification(NotificationRequestDto request) {
        return webClient.post()
                .uri("/notify")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.warn("Notification service unavailable: {}", e.getMessage()))
                .then();
    }
}