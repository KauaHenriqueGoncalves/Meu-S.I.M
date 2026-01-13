-- Roles de profile no nosso sistema
INSERT INTO role (id, name)
VALUES (1, 'system_admin'),
        (2, 'school_admin'),
        (3, 'collaborator'),
        (4, 'legal_guardian'),
ON CONFLICT (name) DO NOTHING;

-- Não quebrar o ROLE
SELECT setval(
    pg_get_serial_sequence('role', 'id'),
    (SELECT MAX(id) FROM role)
);