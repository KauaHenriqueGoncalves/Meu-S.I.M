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

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_school_admin_user_id ON school_admin(user_id);
CREATE INDEX IF NOT EXISTS idx_school_admin_school_id ON school_admin(school_id);

CREATE INDEX IF NOT EXISTS idx_collaborator_user_id ON collaborator(user_id);
CREATE INDEX IF NOT EXISTS idx_collaborator_school_id ON collaborator(school_id);

CREATE INDEX IF NOT EXISTS idx_legal_guardian_user_id ON legal_guardian(user_id);
CREATE INDEX IF NOT EXISTS idx_legal_guardian_school_id ON legal_guardian(school_id);

CREATE INDEX IF NOT EXISTS idx_student_school_id ON student(school_id);

CREATE INDEX IF NOT EXISTS idx_subject_school_id ON subject(school_id);
