CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            bank_transaction_id VARCHAR(255) NOT NULL UNIQUE,
                                            amount NUMERIC(19, 2) NOT NULL,
                                            currency CHAR(3) NOT NULL,
                                            merchant_id VARCHAR(255) NOT NULL,
                                            status VARCHAR(20) NOT NULL,
                                            reason TEXT,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
