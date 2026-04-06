export type Role = 'system_admin' | 'school_admin' | 'collaborator' | 'legal_guardian';

export interface MenuItem {
  label: string;
  icon: string;
  route: string;
}

export const MENU_CONFIG: Record<Role, MenuItem[]> = {
  system_admin: [
    { label: 'system_admin', icon: '📊', route: '/dashboard' },
  ],

  school_admin: [
    { label: 'Dashboard', icon: '📊', route: '/dashboard' },
    { label: 'Escolas', icon: '🏫', route: '/schools' },
    { label: 'Planos', icon: '💳', route: '/billing' },
    { label: 'Planos', icon: '💳', route: '/billing' }
  ],

  collaborator: [
    { label: 'collaborator', icon: '📊', route: '/dashboard' },
  ],

  legal_guardian: [
    { label: 'legal_guardian', icon: '📊', route: '/dashboard' },
  ]
};