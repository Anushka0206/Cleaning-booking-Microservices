CREATE TABLE cleaner_notification (
    id VARCHAR(36) PRIMARY KEY,
    cleaner_id VARCHAR(36) NOT NULL,
    booking_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    message TEXT NOT NULL,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(32),
    customer_address VARCHAR(512),
    read_flag BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_cleaner ON cleaner_notification(cleaner_id, created_at DESC);
