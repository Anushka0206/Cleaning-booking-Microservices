-- Friendly labels + phone for UI (existing databases)

ALTER TABLE cleaner ADD COLUMN IF NOT EXISTS phone VARCHAR(32);

UPDATE vehicle SET code = 'Marina Team A' WHERE id = '5b2b9322-3a9a-42b0-84a0-7d27af53dea4';
UPDATE vehicle SET code = 'Downtown Team B' WHERE id = '6c84d63c-cfb9-4088-933f-66c837794787';
UPDATE vehicle SET code = 'JLT Team C' WHERE id = '22fae9b7-4311-4801-9906-ce20fc660c7b';
UPDATE vehicle SET code = 'Palm Team D' WHERE id = '81ca6396-fa90-4267-a555-75080e08857f';
UPDATE vehicle SET code = 'Indore Team E' WHERE id = '9cd3df86-8c5c-42ce-b311-38bfcf249963';

UPDATE cleaner SET full_name = 'Priya Sharma' WHERE id = '2949580d-d1bf-4a22-966f-aaf79898fd3d';
UPDATE cleaner SET full_name = 'Amit Verma' WHERE id = 'fa306ca1-4453-4ade-8084-762f7bf09e2f';
UPDATE cleaner SET full_name = 'Sneha Patel' WHERE id = 'f5a8bd02-e328-4f72-a539-011cca949f3b';
UPDATE cleaner SET full_name = 'Rahul Mehta' WHERE id = 'cb8968a5-c06d-409b-80a8-08a1c72a48ed';
UPDATE cleaner SET full_name = 'Ananya Iyer' WHERE id = '0bdcb6e9-da44-4cfc-bb68-aa15e6cd608f';

UPDATE cleaner SET full_name = 'Omar Hassan' WHERE id = '3612b240-aee0-426d-8d47-cf0dff74fa92';
UPDATE cleaner SET full_name = 'Fatima Ali' WHERE id = 'b2ed2874-0e86-4e31-a197-26b35fa0396b';
UPDATE cleaner SET full_name = 'James Wilson' WHERE id = '3638acf9-8297-4237-915b-ab1bff4ae7ce';
UPDATE cleaner SET full_name = 'Sarah Chen' WHERE id = '78a95903-a1d4-40a0-bc69-49bd7d9b954d';
UPDATE cleaner SET full_name = 'David Kumar' WHERE id = 'a65b69e5-85ba-424d-8f39-3d0fc720578c';

UPDATE cleaner SET full_name = 'Meera Nair' WHERE id = '83e68582-78a4-4d0e-84eb-2e013c38825c';
UPDATE cleaner SET full_name = 'Vikram Singh' WHERE id = 'f4204e32-70fc-4c2c-aa8f-c97a1a8735a2';
UPDATE cleaner SET full_name = 'Kavya Reddy' WHERE id = '8a884bd6-651e-408c-8c65-13089b7ac17b';
UPDATE cleaner SET full_name = 'Arjun Desai' WHERE id = '7d379e99-5226-4fe7-a204-c848270773ec';
UPDATE cleaner SET full_name = 'Isha Gupta' WHERE id = '4e4ce5d5-4aa7-44fb-a977-24bf4c8e8ccd';

UPDATE cleaner SET full_name = 'Noah Brooks' WHERE id = '7c90aef0-9c6b-4efd-ac93-7c96d1a893c1';
UPDATE cleaner SET full_name = 'Emma Lewis' WHERE id = 'f758171a-4db5-4107-af59-575f3856563c';
UPDATE cleaner SET full_name = 'Liam Carter' WHERE id = '1c3dc639-2157-458f-b08e-7bb184656925';
UPDATE cleaner SET full_name = 'Zoe Martin' WHERE id = '6e43c8dd-dd3a-4a80-a414-940a593193b5';
UPDATE cleaner SET full_name = 'Ethan Price' WHERE id = 'c209ea4c-9349-43bf-86ae-e9e29f04f094';

UPDATE cleaner SET full_name = 'Anushka Rao' WHERE id = '7828d8e9-a41e-4f4c-83cd-06b90cbece4c';
UPDATE cleaner SET full_name = 'Kabir Joshi' WHERE id = '47cbbc2d-da3d-486b-9e47-a3a32dabb674';
UPDATE cleaner SET full_name = 'Diya Malhotra' WHERE id = '25f03770-ca7a-408c-81fb-93cf494bb5c4';
UPDATE cleaner SET full_name = 'Rohan Kapoor' WHERE id = '27d86c53-c072-4916-b90f-7f39d6631835';
UPDATE cleaner SET full_name = 'Neha Bansal' WHERE id = '3004fc94-def3-443b-aaa9-25806686cbce';

UPDATE cleaner SET phone = '+971501234567' WHERE id = '2949580d-d1bf-4a22-966f-aaf79898fd3d' AND phone IS NULL;
