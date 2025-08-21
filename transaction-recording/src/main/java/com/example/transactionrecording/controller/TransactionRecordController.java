package com.example.transactionrecording.controller;

import com.example.common.dto.TransactionRecordRequest;
import com.example.common.dto.TransactionRecordResponse;
import com.example.transactionrecording.service.TransactionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionRecordController {
    private final TransactionRecordService service;

    @PostMapping
    @Operation(summary = "Save completed transaction")
    public ResponseEntity<TransactionRecordResponse> saveTransaction(@Valid @RequestBody TransactionRecordRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveTransaction(dto));
    }

    @GetMapping
    @Operation(summary = "Get transaction by ID")
    public TransactionRecordResponse getTransaction(@RequestParam String bankTransactionId) {
        return service.getTransaction(bankTransactionId);
    }
}
