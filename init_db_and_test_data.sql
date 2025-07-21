--
-- Mandatory Tables Creation
--

-- Database Schema Definitions
CREATE TABLE public.users (
    id serial4 NOT NULL,
    username varchar NOT NULL,
    "password" varchar NOT NULL,
    "role" varchar NOT NULL,
    email varchar NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY (ARRAY['ADMIN'::text, 'MANAGER'::text, 'VIEWER'::text, 'FINANCE_MANAGER'::text]))),
    CONSTRAINT users_username_key UNIQUE (username)
);

CREATE TABLE public.employees (
    id serial4 NOT NULL,
    "name" varchar NOT NULL,
    department varchar NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT employees_pkey PRIMARY KEY (id)
);

CREATE TABLE public.counterparties (
    id serial4 NOT NULL,
    "name" varchar NOT NULL,
    "type" varchar NOT NULL,
    mobile varchar NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT counterparties_pkey PRIMARY KEY (id),
    CONSTRAINT counterparties_type_check CHECK (((type)::text = ANY ((ARRAY['CLIENT'::character varying, 'VENDOR'::character varying])::text[])))
);

CREATE TABLE public.payments (
    id serial4 NOT NULL,
    amount numeric NOT NULL,
    direction varchar NOT NULL,
    category varchar NOT NULL,
    status varchar NOT NULL,
    description text NULL,
    created_by int4 NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at timestamp NULL,
    employee_id int4 NULL,
    counterparty_id int4 NULL,
    CONSTRAINT payments_category_check CHECK (((category)::text = ANY ((ARRAY['SALARY'::character varying, 'VENDOR_PAYMENT'::character varying, 'CLIENT_INVOICE'::character varying])::text[]))),
    CONSTRAINT payments_direction_check CHECK (((direction)::text = ANY ((ARRAY['INCOMING'::character varying, 'OUTGOING'::character varying])::text[]))),
    CONSTRAINT payments_pkey PRIMARY KEY (id),
    CONSTRAINT payments_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying])::text[]))),
    CONSTRAINT payments_counterparty_id_fkey FOREIGN KEY (counterparty_id) REFERENCES public.counterparties(id),
    CONSTRAINT payments_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
    CONSTRAINT payments_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES public.employees(id)
);

CREATE TABLE public.audit_logs (
    id serial4 NOT NULL,
    payment_id int4 NULL,
    field_changed varchar NOT NULL,
    old_value text NULL,
    new_value text NULL,
    changed_by int4 NULL,
    changed_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT audit_logs_pkey PRIMARY KEY (id),
    CONSTRAINT audit_logs_changed_by_fkey FOREIGN KEY (changed_by) REFERENCES public.users(id),
    CONSTRAINT audit_logs_payment_id_fkey FOREIGN KEY (payment_id) REFERENCES public.payments(id)
);

-- Initial Data Population
-- Users (must be inserted first due to foreign key constraints)
INSERT INTO users (username, password, role, email) VALUES
('admin', '$2a$10$FXrU/pRCugzM3fFZhnAJBu6svU3vkVRLpEJwmIiK580bP2baNQEeW', 'ADMIN', 'admin@example.com');

--
-- Optional Sample Data Insertions
--
INSERT INTO users (username, password, role, email) VALUES
('manager1', '$2a$12$k/gbEcQYz9O.wBEeXOpUkezHByEuaHB7LzczaUdcJCBIRuc.vQvwm', 'FINANCE_MANAGER', 'fm@example.com'),
('viewer1', '$2a$10$oMBPB0VWclWbgBwVtxfRwe8r3hbxmuZJE/MMh97KquGdyjqBFbH.W', 'VIEWER', 'viewer@example.com'),
('finance_guy', '$2a$10$iOyogPAvWz7XQvOVg34HaO24ug/eSu10ZdFuFIJ4baWg7tdST1DfK', 'FINANCE_MANAGER', 'financeguy@example.com'),
('viewer2', '$2a$10$iWoIU9PHOmp3XlY2vqmxNOffpWLpNCZkdQSHyXOnDUd8oG33fVZiO', 'VIEWER', 'viewer2@example.com'),
('auditor', '$2a$10$snZt7meH8YZ4BXvm5.L3ceoiX5K0PqbTuVDY1h9bFkEuB.keO6Pl6', 'VIEWER', 'auditor@example.com');

--
-- Sample User Credentials (for development/testing only)
--
-- These are for the optional sample data inserted above.
--
-- Usernames       | Passwords (all are 'password')
-- ----------------|----------------------------------
-- manager1        | pass123
-- viewer1         | viewpass
-- finance_guy     | fin2pass
-- viewer2         | view2pass
-- auditor         | auditpass
--
-- Note: Passwords are hashed in the database, but 'password' is the plain text for login.
-- Please change these credentials immediately in a production environment.
--

-- Employees
INSERT INTO employees (name, department) VALUES
('John Doe', 'HR'),
('Jane Smith', 'Finance'),
('Alice Lee', 'IT'),
('Mark Twain', 'Sales'),
('Sara Connor', 'Marketing'),
('Tom Hanks', 'Operations');

-- Counterparties
INSERT INTO counterparties (name, type, mobile) VALUES
('ABC Pvt Ltd', 'VENDOR', '9876543210'),
('XYZ Corp', 'CLIENT', '9845123456'),
('QWERTY Supplies', 'VENDOR', '9999912345'),
('DEF Industries', 'VENDOR', '9123456789'),
('GHI Solutions', 'CLIENT', '9234567890'),
('JKL Traders', 'VENDOR', '9345678901');

-- Payments (grouped by time periods)
-- Q4 2024 Payments
INSERT INTO payments (amount, direction, category, status, description, created_by, employee_id, created_at) VALUES
(26000, 'OUTGOING', 'SALARY', 'COMPLETED', 'December 2024 Salary for Mark Twain',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM employees WHERE name='Mark Twain'),
 '2024-12-31 17:00:00'),
(28000, 'OUTGOING', 'SALARY', 'PROCESSING', 'December 2024 Salary for Sara Connor',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM employees WHERE name='Sara Connor'),
 '2024-12-31 17:10:00');

-- Q1 2025 Payments
INSERT INTO payments (amount, direction, category, status, description, created_by, counterparty_id, created_at) VALUES
(35000, 'OUTGOING', 'VENDOR_PAYMENT', 'PROCESSING', 'Invoice #101 for DEF Industries',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='DEF Industries'),
 '2025-01-15 12:30:00'),
(47000, 'OUTGOING', 'VENDOR_PAYMENT', 'COMPLETED', 'January 2025 Supplies from JKL Traders',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='JKL Traders'),
 '2025-02-05 11:00:00');

-- Q2 2025 Payments
INSERT INTO payments (amount, direction, category, status, description, created_by, counterparty_id, created_at) VALUES
(90000, 'INCOMING', 'CLIENT_INVOICE', 'PENDING', 'Invoice #2020 for GHI Solutions',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='GHI Solutions'),
 '2025-04-10 09:00:00'),
(85000, 'INCOMING', 'CLIENT_INVOICE', 'COMPLETED', 'April 2025 payment from XYZ Corp',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='XYZ Corp'),
 '2025-04-20 14:00:00');

-- Q3 2025 Payments
INSERT INTO payments (amount, direction, category, status, description, created_by, employee_id, created_at) VALUES
(25000, 'OUTGOING', 'SALARY', 'COMPLETED', 'Salary for July',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM employees WHERE name='John Doe'),
 '2025-07-01 09:00:00'),
(27000, 'OUTGOING', 'SALARY', 'PENDING', 'Salary for July',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM employees WHERE name='Jane Smith'),
 '2025-07-01 09:30:00'),
(23000, 'OUTGOING', 'SALARY', 'PENDING', 'July Salary for Tom Hanks',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM employees WHERE name='Tom Hanks'),
 '2025-07-31 18:00:00'),
(29000, 'OUTGOING', 'SALARY', 'COMPLETED', 'August Salary for Sara Connor',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM employees WHERE name='Sara Connor'),
 '2025-08-31 18:00:00');

INSERT INTO payments (amount, direction, category, status, description, created_by, counterparty_id, created_at) VALUES
(45000, 'OUTGOING', 'VENDOR_PAYMENT', 'PROCESSING', 'Invoice #789',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM counterparties WHERE name='ABC Pvt Ltd'),
 '2025-07-05 16:45:00'),
(80000, 'INCOMING', 'CLIENT_INVOICE', 'PENDING', 'July invoice payment',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM counterparties WHERE name='XYZ Corp'),
 '2025-07-11 11:10:00'),
(12000, 'INCOMING', 'CLIENT_INVOICE', 'COMPLETED', 'Refund for overpayment',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM counterparties WHERE name='QWERTY Supplies'),
 '2025-07-17 14:00:00'),
(31000, 'OUTGOING', 'VENDOR_PAYMENT', 'COMPLETED', 'Payment for office supplies',
 (SELECT id FROM users WHERE username='manager1'),
 (SELECT id FROM counterparties WHERE name='QWERTY Supplies'),
 '2025-07-19 10:15:00');

-- Q4 2025 Payments
INSERT INTO payments (amount, direction, category, status, description, created_by, counterparty_id, created_at) VALUES
(52000, 'OUTGOING', 'VENDOR_PAYMENT', 'PENDING', 'Year-end payment to DEF Industries',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='DEF Industries'),
 '2025-10-20 10:00:00'),
(67000, 'INCOMING', 'CLIENT_INVOICE', 'PROCESSING', 'Q4 invoice from GHI Solutions',
 (SELECT id FROM users WHERE username='finance_guy'),
 (SELECT id FROM counterparties WHERE name='GHI Solutions'),
 '2025-11-15 15:30:00');

-- Audit Logs
INSERT INTO audit_logs (payment_id, field_changed, old_value, new_value, changed_by) VALUES
((SELECT id FROM payments WHERE description='Salary for July' AND amount=25000), 'status', 'PENDING', 'COMPLETED',
 (SELECT id FROM users WHERE username='manager1')),
((SELECT id FROM payments WHERE description='Invoice #789'), 'status', 'PENDING', 'PROCESSING',
 (SELECT id FROM users WHERE username='manager1')),
((SELECT id FROM payments WHERE description='Payment for office supplies'), 'amount', '30000', '31000',
 (SELECT id FROM users WHERE username='manager1')),
((SELECT id FROM payments WHERE description='December 2024 Salary for Mark Twain'), 'status', 'PENDING', 'COMPLETED',
 (SELECT id FROM users WHERE username='finance_guy')),
((SELECT id FROM payments WHERE description='Invoice #101 for DEF Industries'), 'status', 'PENDING', 'PROCESSING',
 (SELECT id FROM users WHERE username='finance_guy')),
((SELECT id FROM payments WHERE description='Year-end payment to DEF Industries'), 'description', 'Year-end payment to DEF Industries', 'Year-end payment updated',
 (SELECT id FROM users WHERE username='finance_guy'));