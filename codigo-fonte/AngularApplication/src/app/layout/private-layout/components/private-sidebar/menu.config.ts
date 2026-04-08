import { MenuItem } from "./menu-item.model";
import { Role } from "./role.type";

export const MENU_CONFIG: Record<Role, MenuItem[]> = {
  system_admin: [
    { label: 'system_admin', icon: '📊', route: '/dashboard' },
  ],

  school_admin: [
    { label: 'Início', icon: 'dashboard', route: '/app/dashboard' },
    {
      label: 'Acadêmico',
      icon: 'academic',
      expanded: false,
      children: [
        { label: 'Turmas', route: '/classrooms' },
        { label: 'Estudantes', route: '/students' },
        { label: 'Disciplinas', route: '/subjects' },
      ]
    },
    {
      label: 'Comunidade',
      icon: 'users',
      expanded: false,
      children: [
        { label: 'Administradores', route: '/school-admin' },
        { label: 'Colaboradores', route: '/collaborators' },
        { label: 'Responsáveis', route: '/legal-guardians' },
      ]
    },
    {
      label: 'Assinatura',
      icon: 'card',
      expanded: false,
      children: [
        { label: 'Minhas Licenças', route: '/subscriptions' },
        { label: 'Nova Licença', route: '/buy-subscription' },
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