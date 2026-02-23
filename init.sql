CREATE TABLE IF NOT EXISTS accounts (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    from_account_number VARCHAR(50),
    to_account_number VARCHAR(50),
    amount NUMERIC(19,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS fraud_alerts (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    related_transaction_ids TEXT,
    detected_at TIMESTAMP NOT NULL
);