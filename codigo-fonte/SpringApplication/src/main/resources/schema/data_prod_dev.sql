INSERT INTO role (name)
VALUES ('system_admin'),
       ('school_admin'),
       ('collaborator'),
       ('legal_guardian')
ON CONFLICT (name) DO NOTHING;

-- Não quebrar o role
SELECT setval(
    pg_get_serial_sequence('role', 'id'),
    (SELECT MAX(id) FROM role)
);

INSERT INTO class_type (name)
VALUES
    ('individual'),
    ('grupo'),
    ('revisao_intensiva'),
    ('apoio_tarefa'),
    ('preparacao_prova'),
    ('recuperacao_escolar'),
    ('oficina'),
    ('online')
ON CONFLICT (name) DO NOTHING;

-- Não quebrar o class_type
SELECT setval(
    pg_get_serial_sequence('class_type', 'id'),
    (SELECT MAX(id) FROM class_type)
);
