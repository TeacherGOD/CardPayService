package com.example.paymentprocessor.controller;

import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.PaymentStatus;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.exception.DuplicateTransactionException;
import com.example.paymentprocessor.service.PaymentProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentProcessingController.class)
class PaymentProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentProcessingService processingService;

    @Test
    void shouldProcessValidRequest() throws Exception {
        BankResponse request = new BankResponse(
                "bank-txn-123",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(750.00),
                "USD",
                "merch-333"
        );

        FinalTransactionStatus response = new FinalTransactionStatus(
                "bank-txn-123",
                PaymentStatus.APPROVED,
                "Transaction processed"
        );

        given(processingService.processBankResponse(any())).willReturn(response);

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("bank-txn-123"))
                .andExpect(jsonPath("$.status").value(PaymentStatus.APPROVED.toString()))
                .andExpect(jsonPath("$.message").value("Transaction processed"));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {
        String invalidRequest = """
        {
            "bankTransactionId": "",
            "status": "INVALID_STATUS",
            "amount": -100
        }""";

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MALFORMED_JSON"));
    }

    @Test
    void shouldHandleDuplicateTransaction() throws Exception {

        BankResponse request = new BankResponse(
                "bank-txn-dup",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(300.00),
                "USD",
                "merch-555"
        );

        given(processingService.processBankResponse(any()))
                .willThrow(new DuplicateTransactionException("Duplicate detected"));

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_TRANSACTION"))
                .andExpect(jsonPath("$.message").value("Duplicate detected"));
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        BankResponse invalidRequest = new BankResponse(
                "txn-123",
                PaymentStatus.APPROVED,
                "Approved",
                BigDecimal.valueOf(-100.00),
                "USD",
                "merch-666"
        );

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message",containsString("must be at least 0.01")));
    }
}