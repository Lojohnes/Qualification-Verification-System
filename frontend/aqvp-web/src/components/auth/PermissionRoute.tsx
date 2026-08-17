import { Navigate, Outlet } from 'react-router-dom';

import { usePermission } from '@/hooks/usePermission';
import { ROUTES } from '@/constants/routes';

interface PermissionRouteProps {
  permission?: string;
  anyOf?: string[];
}

export function PermissionRoute({ permission, anyOf }: PermissionRouteProps) {
  const { hasPermission, hasAnyPermission } = usePermission();

  const allowed = permission
    ? hasPermission(permission)
    : anyOf
      ? hasAnyPermission(anyOf)
      : true;

  if (!allowed) {
    return <Navigate to={ROUTES.ACCESS_DENIED} replace />;
  }

  return <Outlet />;
}
