package com.example.paymentauthorizer.exception;


import com.example.common.dto.bank.BankResponse;
import com.example.common.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BankResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        String errorMessage = "Wrong request. "+ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .badRequest()
                .body(new BankResponse(
                        null,
                        PaymentStatus.ERROR,
                        errorMessage,
                        null,
                        null,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BankResponse> handleAllExceptions(Exception ex) {
        String errorMessage = "There was an internal server error. Please try later.";
        log.error("Необработанная ошибка.", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BankResponse(
                        null,
                        PaymentStatus.ERROR,
                        errorMessage,
                        null,
                        null,
                        null
                ));
    }
}
