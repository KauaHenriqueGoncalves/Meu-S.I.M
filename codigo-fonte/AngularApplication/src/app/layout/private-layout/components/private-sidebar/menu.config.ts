import { MenuItem } from "./menu-item.model";
import { Role } from "./role.type";

export const MENU_CONFIG: Record<Role, MenuItem[]> = {
  system_admin: [
    { label: 'system_admin', icon: '📊', route: '/dashboard' },
  ],

  school_admin: [
    { 
      label: 'Início', 
      icon: 'dashboard', 
      route: '/app/dashboard' 
    },
    {
      label: 'Acadêmico',
      icon: 'academic',
      expanded: false,
      children: [
        { label: 'Turmas', route: '/app/classrooms' },
        { label: 'Estudantes', route: '/app/students' },
        { label: 'Disciplinas', route: '/app/subjects' },
      ]
    },
    {
      label: 'Comunidade',
      icon: 'users',
      expanded: false,
      children: [
        { label: 'Administradores', route: '/app/school-admins' },
        { label: 'Colaboradores', route: '/app/collaborators' },
        { label: 'Responsáveis', route: '/app/legal-guardians' },
      ]
    },
    {
      label: 'Assinatura',
      icon: 'card',
      expanded: false,
      children: [
        { label: 'Minhas Licenças', route: '/app/my-subscriptions' },
        { label: 'Nova Licença', route: '/app/new-subscription' },
      ]
    },
  ],

  collaborator: [
    { label: 'collaborator', icon: '📊', route: '/dashboard' },
  ],

  legal_guardian: [
    { label: 'legal_guardian', icon: '📊', route: '/dashboard' },
  ]
};