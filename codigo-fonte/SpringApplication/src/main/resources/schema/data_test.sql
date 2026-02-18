-- Roles de profile no nosso sistema
INSERT INTO role (id, name)
VALUES (1, 'system_admin'),
       (2, 'school_admin'),
       (3, 'collaborator'),
       (4, 'legal_guardian');

-- Tipos de classe do sistema (reforço escolar)
INSERT INTO class_type (id, name)
VALUES
    (1, 'individual'),
    (2, 'grupo'),
    (3, 'revisao_intensiva'),
    (4, 'apoio_tarefa'),
    (5, 'preparacao_prova'),
    (6, 'recuperacao_escolar'),
    (7, 'oficina'),
    (8, 'online');