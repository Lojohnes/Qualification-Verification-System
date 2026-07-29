import { Routes, Route, Navigate } from 'react-router-dom';

import { PublicRoute } from '@/components/auth/PublicRoute';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
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

            <Route path={ROUTES.USERS} element={<UsersPage />} />
            <Route path={ROUTES.ROLES} element={<RolesPage />} />
            <Route path={ROUTES.PERMISSIONS} element={<PermissionsPage />} />

            <Route
              path={ROUTES.INSTITUTION}
              element={<PlaceholderPage module="Institution" sprint="Sprint 3" />}
            />
            <Route
              path={ROUTES.QUALIFICATION}
              element={<PlaceholderPage module="Qualification" sprint="Sprint 3" />}
            />
            <Route
              path={ROUTES.VERIFICATION}
              element={<PlaceholderPage module="Verification" sprint="Sprint 4" />}
            />
            <Route
              path={ROUTES.DOCUMENTS}
              element={<PlaceholderPage module="Documents" sprint="Sprint 4" />}
            />
            <Route
              path={ROUTES.AUDIT}
              element={<PlaceholderPage module="Audit" sprint="Sprint 5" />}
            />
            <Route
              path={ROUTES.REPORTS}
              element={<PlaceholderPage module="Reports" sprint="Sprint 5" />}
            />
            <Route
              path={ROUTES.SETTINGS}
              element={<PlaceholderPage module="Settings" sprint="Sprint 6" />}
            />
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
