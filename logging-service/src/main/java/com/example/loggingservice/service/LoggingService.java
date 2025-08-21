package com.example.loggingservice.service;

import com.example.common.dto.LogEntry;
import com.example.common.enums.LogLevel;
import com.example.loggingservice.entity.LogEntryEntity;
import com.example.loggingservice.repository.LogEntryRepository;
import com.example.loggingservice.specification.LogEntrySpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingService {
    private final LogEntryRepository repository;

    public void saveLog(LogEntry logEntry) {
        LogEntryEntity entity = new LogEntryEntity();
        entity.setTimestamp(logEntry.timestamp());
        entity.setLevel(logEntry.level());
        entity.setMessage(logEntry.message());
        entity.setService(logEntry.service());

        log.info("got log FROM: "+entity.getService() +". "+entity.getLevel() +". "+logEntry.message());
        repository.save(entity);
    }

    public Page<LogEntry> getLogs(LogLevel level, String message, String service, Pageable pageable) {
        Specification<LogEntryEntity> spec = (root, query, builder) -> null;

        if (level != null) {
            spec = spec.and(LogEntrySpecifications.hasLevel(level));
        }
        if (message != null && !message.isEmpty()) {
            spec = spec.and(LogEntrySpecifications.containsMessage(message));
        }
        if (service != null && !service.isEmpty()) {
            spec = spec.and(LogEntrySpecifications.containsService(service));
        }

        return repository.findAll(spec, pageable).map(this::convertToDto);
    }

    private LogEntry convertToDto(LogEntryEntity entity) {
        return new LogEntry(
                entity.getTimestamp(),
                entity.getLevel(),
                entity.getMessage(),
                entity.getService()
        );
    }
}