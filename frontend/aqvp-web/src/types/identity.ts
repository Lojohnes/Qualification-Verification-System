export interface Role {
  id: string;
  name: string;
  description?: string;
  permissions: string[];
}

export interface Permission {
  id: string;
  name: string;
  resource: string;
  action: string;
  description?: string;
}

export interface UserListItem {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  enabled: boolean;
  emailVerified?: boolean;
  mfaEnabled?: boolean;
  roles: string[];
}

export interface UserCreateRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  roleIds: string[];
}

export interface UserUpdateRequest {
  email: string;
  firstName?: string;
  lastName?: string;
  enabled?: boolean;
  roleIds: string[];
}

export interface RoleRequest {
  name: string;
  description?: string;
  permissionIds: string[];
}

export interface CurrentUserProfile {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  enabled: boolean;
  emailVerified?: boolean;
  mfaEnabled?: boolean;
  roles: string[];
  permissions: string[];
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
