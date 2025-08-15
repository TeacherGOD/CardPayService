package com.example.paymentauthorizer.controller;

import com.example.common.dto.payment.AuthorizationResponse;
import com.example.common.dto.payment.PaymentRequest;
import com.example.paymentauthorizer.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/authorize")
    @Operation(summary = "Authorize a payment transaction")
    public Mono<ResponseEntity<AuthorizationResponse>> authorizePayment(
            @Valid @RequestBody PaymentRequest request
    ) {
        return paymentService.authorizePayment(request)
                .map(ResponseEntity::ok);
    }
}
