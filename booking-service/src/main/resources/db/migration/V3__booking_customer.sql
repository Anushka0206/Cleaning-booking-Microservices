ALTER TABLE booking ADD COLUMN user_id VARCHAR(36);
ALTER TABLE booking ADD COLUMN customer_name VARCHAR(255);
ALTER TABLE booking ADD COLUMN customer_phone VARCHAR(32);
ALTER TABLE booking ADD COLUMN customer_address VARCHAR(512);

CREATE INDEX idx_booking_user_id ON booking(user_id);
