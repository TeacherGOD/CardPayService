package com.example.loggingservice.specification;

import com.example.common.enums.LogLevel;
import com.example.loggingservice.entity.LogEntryEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class LogEntrySpecifications {
    private LogEntrySpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<LogEntryEntity> hasLevel(LogLevel level) {
        return (root, query, cb) ->
                level != null ? cb.equal(root.get("level"), level) : null;
    }

    public static Specification<LogEntryEntity> containsMessage(String message) {
        return (root, query, cb) ->
                StringUtils.hasText(message) ?
                        cb.like(cb.lower(root.get("message")), "%" + message.toLowerCase() + "%") : null;
    }

    public static Specification<LogEntryEntity> containsService(String service) {
        return (root, query, cb) ->
                StringUtils.hasText(service) ?
                        cb.like(cb.lower(root.get("service")), "%" + service.toLowerCase() + "%") : null;
    }
}