export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  FORGOT_PASSWORD: '/forgot-password',
  RESET_PASSWORD: '/reset-password',
  UNAUTHORIZED: '/unauthorized',
  ACCESS_DENIED: '/access-denied',

  DASHBOARD: '/dashboard',

  USERS: '/identity/users',
  ROLES: '/identity/roles',
  PERMISSIONS: '/identity/permissions',

  INSTITUTION: '/institution',
  INSTITUTIONS: '/institution/institutions',
  FACULTIES: '/institution/faculties',
  DEPARTMENTS: '/institution/departments',
  PROGRAMS: '/institution/programs',
  QUALIFICATION: '/qualification',
  STUDENTS: '/qualification/students',
  QUALIFICATIONS: '/qualification/qualifications',
  VERIFICATION: '/verification',
  DOCUMENTS: '/documents',
  AUDIT: '/audit',
  NOTIFICATIONS: '/notifications',
  REPORTS: '/reports',
  SETTINGS: '/settings',
} as const;

export type RouteKey = keyof typeof ROUTES;
export type RoutePath = (typeof ROUTES)[RouteKey];
