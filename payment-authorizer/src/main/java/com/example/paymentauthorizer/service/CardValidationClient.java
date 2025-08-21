package com.example.paymentauthorizer.service;

import com.example.common.dto.CardData;
import com.example.common.dto.ValidationResult;
import com.example.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class CardValidationClient {
    private final WebClient webClient;

    public CardValidationClient(
            @Value("${services.card-validator.url}") String baseUrl,
            WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ValidationResult> validateCard(CardData cardData) {
        return webClient.post()
                .uri("/card/validate")
                .bodyValue(cardData)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ServiceException(
                                        "Card validation service error: " + error,
                                        response.statusCode()
                                )))
                )
                .bodyToMono(ValidationResult.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.error("Card validation failed", e));
    }
}