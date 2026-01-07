-- V2__seed_users.sql
-- Seed initial users: ADMIN / EMPLOYEE / CUSTOMER

INSERT INTO users (
    first_name,
    last_name,
    email,
    password_hash,
    phone,
    role,
    is_active,
    created_at,
    update_at
)
VALUES
-- Admin
(
    'Admin',
    'User',
    'admin@test.com',
    '$2a$10$929SUeYe1hnl4jsB6m6m3.EBpSXlbyO5zflWsvY/m4b67w7rxmb7e',
    '000-000-0000',
    'ADMIN',
    TRUE,
    now(),
    now()
),

-- Employee
(
    'Employee',
    'User',
    'employee@test.com',
    '$2a$10$3G1TDsJhyOheFezhdwXFPeXOtjW8d3A3HIVMbAp418nuIdVxwOXBO',
    '000-000-0001',
    'EMPLOYEE',
    TRUE,
    now(),
    now()
),

-- Customer
(
    'Customer',
    'User',
    'customer@test.com',
    '$2a$10$a0GOz85Z65eMqhMaZGTZJ.vu8mpV42/FHOUQewKscaxKrktHkgKsm',
    '000-000-0002',
    'CUSTOMER',
    TRUE,
    now(),
    now()
)

    ON CONFLICT (email) DO NOTHING;
