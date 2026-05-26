ALTER TABLE cleaner_notification ADD COLUMN IF NOT EXISTS slot_start_at TIMESTAMP;
ALTER TABLE cleaner_notification ADD COLUMN IF NOT EXISTS duration_hours INT;
