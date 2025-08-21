CREATE TABLE IF NOT EXISTS log_entries (
                                           id BIGSERIAL PRIMARY KEY,
                                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           level VARCHAR(10) NOT NULL,
                                           message VARCHAR(1000) NOT NULL,
                                           service VARCHAR(50) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_log_entries_timestamp ON log_entries (timestamp);

CREATE INDEX IF NOT EXISTS idx_log_entries_level ON log_entries (level);

CREATE INDEX IF NOT EXISTS idx_log_entries_service ON log_entries (service);
