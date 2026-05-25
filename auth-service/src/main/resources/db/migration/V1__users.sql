CREATE TABLE app_user (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(32),
    address VARCHAR(512),
    role VARCHAR(20) NOT NULL,
    cleaner_id VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_app_user_email ON app_user(email);
CREATE INDEX idx_app_user_cleaner_id ON app_user(cleaner_id);
