-- Insert default permissions
INSERT INTO permissions (name, description, active) VALUES
    ('USER_READ', 'Read user information', TRUE),
    ('USER_WRITE', 'Create and update users', TRUE),
    ('USER_DELETE', 'Delete users', TRUE),
    ('ROLE_READ', 'Read role information', TRUE),
    ('ROLE_WRITE', 'Create and update roles', TRUE),
    ('ROLE_DELETE', 'Delete roles', TRUE),
    ('PERMISSION_READ', 'Read permission information', TRUE),
    ('PERMISSION_WRITE', 'Create and update permissions', TRUE),
    ('PERMISSION_DELETE', 'Delete permissions', TRUE),
    ('DASHBOARD_ACCESS', 'Access dashboard', TRUE);

-- Insert default roles
INSERT INTO roles (name, description, active) VALUES
    ('ADMIN', 'Administrator with full access', TRUE),
    ('USER_MANAGER', 'Can manage users', TRUE),
    ('USER', 'Regular user with basic access', TRUE);

-- Assign permissions to ADMIN role (all permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Assign permissions to USER_MANAGER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER_MANAGER'
  AND p.name IN ('USER_READ', 'USER_WRITE', 'USER_DELETE', 'DASHBOARD_ACCESS');

-- Assign permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER'
  AND p.name IN ('DASHBOARD_ACCESS');

-- Insert default admin user (password: admin123)
-- Note: This is a BCrypt hash of "admin123" with strength 12
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, login_attempts)
VALUES ('admin', 'admin@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKhBe2B3FW', 'System', 'Administrator', TRUE, TRUE, TRUE, TRUE, 0);

-- Insert default regular user (password: user123)
-- Note: This is a BCrypt hash of "user123" with strength 12
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, login_attempts)
VALUES ('user', 'user@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKhBe2B3FW', 'Regular', 'User', TRUE, TRUE, TRUE, TRUE, 0);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Assign USER role to regular user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'user' AND r.name = 'USER';
