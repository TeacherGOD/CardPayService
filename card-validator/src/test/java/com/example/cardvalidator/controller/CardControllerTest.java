package com.example.cardvalidator.controller;

import com.example.cardvalidator.dto.ValidationResult;
import com.example.cardvalidator.service.CardValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static com.example.cardvalidator.constant.ErrorMessages.CARD_VALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CardValidationService validationService;

    @Test
    void validRequestShouldReturnOk() throws Exception {
        given(validationService.validateCard(any()))
                .willReturn(new ValidationResult(true, CARD_VALID));

        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardNumber\":\"4111111111111111\",\"expiryDate\":\"12/30\",\"cvv\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void invalidInputShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/card/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardNumber\":\"4111\",\"expiryDate\":\"13/30\",\"cvv\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false));
    }
}