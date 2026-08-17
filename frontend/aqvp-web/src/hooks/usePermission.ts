import { useAppSelector } from '@/hooks/redux';

export function usePermission() {
  const permissions = useAppSelector((state) => state.auth.user?.permissions ?? []);

  const hasPermission = (permission: string) => permissions.includes(permission);

  const hasAnyPermission = (required: string[]) =>
    required.some((permission) => permissions.includes(permission));

  const hasAllPermissions = (required: string[]) =>
    required.every((permission) => permissions.includes(permission));

  return { permissions, hasPermission, hasAnyPermission, hasAllPermissions };
}
