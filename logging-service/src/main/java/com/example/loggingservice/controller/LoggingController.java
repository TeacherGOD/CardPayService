package com.example.loggingservice.controller;

import com.example.common.dto.LogEntry;
import com.example.common.enums.LogLevel;
import com.example.loggingservice.service.LoggingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LoggingController {
    private final LoggingService loggingService;

    @PostMapping
    @Operation(summary = "Receive and store logs")
    public void receiveLog(@RequestBody LogEntry logEntry) {
        loggingService.saveLog(logEntry);
    }

    @GetMapping
    @Operation(summary = "View logs with filtering and pagination")
    public Page<LogEntry> getLogs(
            @RequestParam(required = false) LogLevel level,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String service,
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable) {

        return loggingService.getLogs(level, message, service, pageable);
    }
}