package com.example.loggingservice.repository;

import com.example.loggingservice.entity.LogEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface LogEntryRepository extends JpaRepository<LogEntryEntity, Long>,
        JpaSpecificationExecutor<LogEntryEntity> {
}