package com.example.common.dto;


import com.example.common.enums.LogLevel;

import java.time.LocalDateTime;


public record LogEntry(
        LocalDateTime timestamp,
        LogLevel level,
        String message,
        String service
) {}
