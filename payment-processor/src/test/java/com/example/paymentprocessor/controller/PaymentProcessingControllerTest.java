package com.example.paymentprocessor.controller;

import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.PaymentStatus;
import com.example.paymentprocessor.dto.FinalTransactionStatus;
import com.example.paymentprocessor.service.PaymentProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
                "Approved"
        );

        FinalTransactionStatus response = new FinalTransactionStatus(
                "bank-txn-123",
                com.example.common.enums.FinalPaymentStatus.APPROVED,
                "Transaction processed"
        );

        given(processingService.processBankResponse(any())).willReturn(response);

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("bank-txn-123"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.message").value("Transaction processed"));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {
        String invalidRequest = """
        {
            "bankTransactionId": "",
            "status": "INVALID_STATUS"
        }""";

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleServiceExceptions() throws Exception {
        BankResponse request = new BankResponse(
                "bank-txn-123",
                PaymentStatus.APPROVED,
                "Approved"
        );

        given(processingService.processBankResponse(any()))
                .willThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}