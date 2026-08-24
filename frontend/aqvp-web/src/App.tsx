import { Routes, Route, Navigate } from 'react-router-dom';

import { PublicRoute } from '@/components/auth/PublicRoute';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
import { PermissionRoute } from '@/components/auth/PermissionRoute';
import { SessionExpiredDialog } from '@/components/auth/SessionExpiredDialog';
import { MainLayout } from '@/layouts/MainLayout';
import { LoginPage } from '@/features/auth/pages/LoginPage';
import { RegisterPage } from '@/features/auth/pages/RegisterPage';
import { ForgotPasswordPage } from '@/features/auth/pages/ForgotPasswordPage';
import { ResetPasswordPage } from '@/features/auth/pages/ResetPasswordPage';
import { UnauthorizedPage } from '@/pages/UnauthorizedPage';
import { AccessDeniedPage } from '@/pages/AccessDeniedPage';
import { NotFoundPage } from '@/pages/NotFoundPage';
import { DashboardPage } from '@/features/dashboard/pages/DashboardPage';
import { UsersPage } from '@/features/identity/pages/UsersPage';
import { RolesPage } from '@/features/identity/pages/RolesPage';
import { PermissionsPage } from '@/features/identity/pages/PermissionsPage';
import { InstitutionsPage } from '@/features/institution/pages/InstitutionsPage';
import { FacultiesPage } from '@/features/institution/pages/FacultiesPage';
import { DepartmentsPage } from '@/features/institution/pages/DepartmentsPage';
import { ProgramsPage } from '@/features/institution/pages/ProgramsPage';
import { StudentsPage } from '@/features/qualification/pages/StudentsPage';
import { QualificationsPage } from '@/features/qualification/pages/QualificationsPage';
import { VerificationPage } from '@/features/verification/pages/VerificationPage';
import { DocumentsPage } from '@/features/documents/pages/DocumentsPage';
import { SettingsPage } from '@/features/settings/pages/SettingsPage';
import { PlaceholderPage } from '@/pages/PlaceholderPage';
import { ROUTES } from '@/constants/routes';

function App() {
  return (
    <>
      <Routes>
        <Route element={<PublicRoute />}>
          <Route path={ROUTES.LOGIN} element={<LoginPage />} />
          <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
          <Route path={ROUTES.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
          <Route path={ROUTES.RESET_PASSWORD} element={<ResetPasswordPage />} />
        </Route>

        <Route path={ROUTES.UNAUTHORIZED} element={<UnauthorizedPage />} />
        <Route path={ROUTES.ACCESS_DENIED} element={<AccessDeniedPage />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />

            <Route element={<PermissionRoute permission="user:read" />}>
              <Route path={ROUTES.USERS} element={<UsersPage />} />
            </Route>
            <Route element={<PermissionRoute permission="role:read" />}>
              <Route path={ROUTES.ROLES} element={<RolesPage />} />
              <Route path={ROUTES.PERMISSIONS} element={<PermissionsPage />} />
            </Route>

            <Route
              path={ROUTES.INSTITUTION}
              element={<Navigate to={ROUTES.INSTITUTIONS} replace />}
            />
            <Route element={<PermissionRoute permission="institution:read" />}>
              <Route path={ROUTES.INSTITUTIONS} element={<InstitutionsPage />} />
            </Route>
            <Route element={<PermissionRoute permission="faculty:read" />}>
              <Route path={ROUTES.FACULTIES} element={<FacultiesPage />} />
            </Route>
            <Route element={<PermissionRoute permission="department:read" />}>
              <Route path={ROUTES.DEPARTMENTS} element={<DepartmentsPage />} />
            </Route>
            <Route element={<PermissionRoute permission="program:read" />}>
              <Route path={ROUTES.PROGRAMS} element={<ProgramsPage />} />
            </Route>
            <Route
              path={ROUTES.QUALIFICATION}
              element={<Navigate to={ROUTES.STUDENTS} replace />}
            />
            <Route
              element={<PermissionRoute permission="student:read" />}
            >
              <Route path={ROUTES.STUDENTS} element={<StudentsPage />} />
            </Route>
            <Route
              element={<PermissionRoute permission="qualification:read" />}
            >
              <Route path={ROUTES.QUALIFICATIONS} element={<QualificationsPage />} />
            </Route>
            <Route
              element={<PermissionRoute permission="verification:read" />}
            >
              <Route path={ROUTES.VERIFICATION} element={<VerificationPage />} />
            </Route>
            <Route
              element={<PermissionRoute permission="qualification:read" />}
            >
              <Route path={ROUTES.DOCUMENTS} element={<DocumentsPage />} />
            </Route>
            <Route
              path={ROUTES.AUDIT}
              element={<PlaceholderPage module="Audit" sprint="Sprint 5" />}
            />
            <Route
              path={ROUTES.REPORTS}
              element={<PlaceholderPage module="Reports" sprint="Sprint 5" />}
            />
            <Route path={ROUTES.SETTINGS} element={<SettingsPage />} />
          </Route>
        </Route>

        <Route path="/" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
      <SessionExpiredDialog />
    </>
  );
}

export default App;
