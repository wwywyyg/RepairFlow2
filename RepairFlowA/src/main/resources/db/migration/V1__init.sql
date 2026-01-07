-- V1__init.sql
-- PostgreSQL schema for RepairFlow (with enum CHECK constraints)

-- =========================
-- 1) users
-- =========================
CREATE TABLE IF NOT EXISTS users (
                                     id            BIGSERIAL PRIMARY KEY,
                                     first_name    VARCHAR(24) NOT NULL,
    last_name     VARCHAR(24) NOT NULL,
    email         VARCHAR(24) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    phone         VARCHAR(24) NOT NULL,

    role          VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,

    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    update_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_users_role
    CHECK (role IN ('CUSTOMER','EMPLOYEE','ADMIN'))
    );

CREATE INDEX IF NOT EXISTS idx_users_role   ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);

-- =========================
-- 2) device_category
-- =========================
CREATE TABLE IF NOT EXISTS device_category (
                                               id   BIGSERIAL PRIMARY KEY,
                                               name VARCHAR(64) NOT NULL UNIQUE
    );

-- =========================
-- 3) issue_type
-- =========================
CREATE TABLE IF NOT EXISTS issue_type (
                                          id                 BIGSERIAL PRIMARY KEY,
                                          name               VARCHAR(128) NOT NULL,
    device_category_id BIGINT,

    CONSTRAINT fk_issue_type_device_category
    FOREIGN KEY (device_category_id)
    REFERENCES device_category(id)
    ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_issue_type_device_category_id
    ON issue_type(device_category_id);

-- =========================
-- 4) tickets
-- =========================
CREATE TABLE IF NOT EXISTS tickets (
                                       id                 BIGSERIAL PRIMARY KEY,
                                       title              VARCHAR(64) NOT NULL,
    description        TEXT NOT NULL,

    device_category_id BIGINT,
    issue_type_id      BIGINT,

    customer_id        BIGINT NOT NULL,   -- must exist
    employee_id        BIGINT,            -- can be null

    status             VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    quote_amount       NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    paid               BOOLEAN NOT NULL DEFAULT FALSE,

    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_tickets_status
    CHECK (status IN (
           'PENDING',
           'ASSIGNED',
           'QUOTED',
           'AWAITING_DEVICE',
           'DEVICE_RECEIVED',
           'IN_PROGRESS',
           'READY_FOR_CONFIRMATION',
           'PAID',
           'SHIPPED',
           'DELIVERED'
                     )),

    CONSTRAINT fk_tickets_customer
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,

    CONSTRAINT fk_tickets_employee
    FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_tickets_device_category
    FOREIGN KEY (device_category_id) REFERENCES device_category(id) ON DELETE SET NULL,

    CONSTRAINT fk_tickets_issue_type
    FOREIGN KEY (issue_type_id) REFERENCES issue_type(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_tickets_status       ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_tickets_customer_id  ON tickets(customer_id);
CREATE INDEX IF NOT EXISTS idx_tickets_employee_id  ON tickets(employee_id);
CREATE INDEX IF NOT EXISTS idx_tickets_updated_at   ON tickets(updated_at);
CREATE INDEX IF NOT EXISTS idx_tickets_device_cat   ON tickets(device_category_id);
CREATE INDEX IF NOT EXISTS idx_tickets_issue_type   ON tickets(issue_type_id);

-- =========================
-- 5) messages (chat)
-- =========================
CREATE TABLE IF NOT EXISTS messages (
                                        id         BIGSERIAL PRIMARY KEY,
                                        ticket_id  BIGINT NOT NULL,
                                        sender_id  BIGINT,
                                        content    TEXT NOT NULL,
                                        type       VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_messages_type
    CHECK (type IN ('CHAT','IMAGE','SYSTEM')),

    CONSTRAINT fk_messages_ticket
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,

    CONSTRAINT fk_messages_sender
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_messages_ticket_id  ON messages(ticket_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
