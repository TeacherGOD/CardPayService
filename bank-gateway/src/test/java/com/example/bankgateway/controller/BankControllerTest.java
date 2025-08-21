package com.example.bankgateway.controller;

import com.example.common.dto.bank.BankResponse;
import com.example.bankgateway.service.BankService;
import com.example.common.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @Test
    void shouldApproveTransactionWhenAmountBelowLimit() throws Exception {
        given(bankService.authorizeTransaction(any()))
                .willReturn(new BankResponse(
                        "bank-txn-123",
                        PaymentStatus.APPROVED,
                        "Approved",
                        BigDecimal.valueOf(9999.99),
                        "USD",
                        "merch-123",
                        "1@email.com"));
        String requestJson = """
        {
            "cardData": {
                "cardNumber": "4111111111111111",
                "expiryDate": "12/27",
                "cvv": "123"
            },
            "amount": 9999.99,
            "currency": "USD",
            "merchantId": "merch-123",
            "email": "1@email.com"
        }""";

        mockMvc.perform(post("/bank/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankTransactionId").value("bank-txn-123"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reason").value("Approved"));
    }

    @Test
    void shouldDeclineTransactionWhenAmountExceedsLimit() throws Exception {
        given(bankService.authorizeTransaction(any()))
                .willReturn(new BankResponse(
                        "bank-txn-456",
                        PaymentStatus.DECLINED,
                        "Amount exceeds limit",
                        BigDecimal.valueOf(10000.01),
                        "EUR",
                        "merch-123",
                        "1@email.com"));

        String requestJson = """
        {
            "cardData": {
                "cardNumber": "5555555555554444",
                "expiryDate": "12/27",
                "cvv": "123"
            },
            "amount": 10000.01,
            "currency": "EUR",
            "merchantId": "merch-123",
            "email": "1@email.com"
        }""";

        mockMvc.perform(post("/bank/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankTransactionId").value("bank-txn-456"))
                .andExpect(jsonPath("$.status").value("DECLINED"))
                .andExpect(jsonPath("$.reason").value("Amount exceeds limit"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        String invalidRequestJson = """
        {
            "cardData": {
                "cardNumber": "4111",
                "expiryDate": "13/25",
                "cvv": "abc"
            },
            "amount": -10,
            "currency": "usd",
            "merchantId": "",
            "email": "1@email.com"
        }""";

        mockMvc.perform(post("/bank/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
}