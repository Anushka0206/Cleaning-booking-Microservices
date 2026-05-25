-- BCrypt hash for password "customer123" and "cleaner123" (strength 10)
INSERT INTO app_user (id, email, password_hash, full_name, phone, address, role, cleaner_id)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'customer@justlife.com',
    '$2b$10$NwIDaU2D3WkHxFxiRfkvaOGf82nuvypd8uEbAHYExSKGKGo0/uGkG',
    'Priya Sharma',
    '+971501234567',
    'Marina Tower, Apt 1204, Dubai',
    'CUSTOMER',
    NULL
);

INSERT INTO app_user (id, email, password_hash, full_name, phone, address, role, cleaner_id)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'cleaner@justlife.com',
    '$2b$10$hiqGxgALTqrM1o8Sxnfpcu.7ypKHtnF/.H6SioLZBHcElnOUb0YyG',
    'Priya Sharma',
    '+971509876543',
    NULL,
    'CLEANER',
    '2949580d-d1bf-4a22-966f-aaf79898fd3d'
);
