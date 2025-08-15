package com.example.paymentprocessor.controller;

import com.example.common.dto.bank.BankResponse;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.service.PaymentProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentProcessingController {

    private final PaymentProcessingService processingService;

    @PostMapping("/process")
    @Operation(
            summary = "Process final transaction result",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    public ResponseEntity<FinalTransactionStatus> processTransaction(
            @Valid @RequestBody BankResponse bankResponse
    ) {
        FinalTransactionStatus result = processingService.processBankResponse(bankResponse);
        return ResponseEntity.ok(result);
    }
}