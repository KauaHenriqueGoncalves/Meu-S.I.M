export type Role = 'system_admin' | 'school_admin' | 'collaborator' | 'legal_guardian';

export const MENU_CONFIG: Record<Role, any[]> = {
  system_admin: [
    // { label: 'Dashboard', icon: 'home', route: '/dashboard' },
    // { label: 'Escolas', icon: 'school', route: '/schools' },
    // { label: 'Planos', icon: 'credit_card', route: '/billing' }
  ],

  school_admin: [
    // { label: 'Dashboard', route: '/dashboard' },
    // { label: 'Alunos', route: '/students' },
    // { label: 'Turmas', route: '/classrooms' }
  ],

  collaborator: [
    // { label: 'Minhas Turmas', route: '/classrooms' }
  ],

  legal_guardian: [
    // { label: 'Minhas Turmas', route: '/classrooms' }
  ]
};