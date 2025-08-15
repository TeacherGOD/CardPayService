package com.example.bankgateway.controller;

import com.example.common.dto.bank.BankResponse;
import com.example.bankgateway.service.BankService;
import com.example.common.dto.bank.BankRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank")
@RequiredArgsConstructor
public class BankController {
    private final BankService bankService;

    @PostMapping("/authorize")
    @Operation(summary = "Authorize transaction")
    @ApiResponse(responseCode = "200", description = "Authorization result")
    public BankResponse authorizeTransaction(
            @Valid @RequestBody BankRequest request
    ) {
        return bankService.authorizeTransaction(request);
    }
}