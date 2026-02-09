-- Roles de profile no nosso sistema
INSERT INTO role (name)
VALUES ('system_admin'),
       ('school_admin'),
       ('collaborator'),
       ('legal_guardian')
ON CONFLICT (name) DO NOTHING;

-- Não quebrar o ROLE
SELECT setval(
    pg_get_serial_sequence('role', 'id'),
    (SELECT MAX(id) FROM role)
);