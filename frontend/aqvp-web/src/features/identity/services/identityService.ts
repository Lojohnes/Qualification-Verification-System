import { api } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type {
  ChangePasswordRequest,
  CurrentUserProfile,
  Permission,
  Role,
  RoleRequest,
  UserCreateRequest,
  UserListItem,
  UserUpdateRequest,
} from '@/types/identity';

export const identityService = {
  getUsers: async () => {
    const response = await api.get<UserListItem[]>(API_ENDPOINTS.IDENTITY.USERS);
    return response.data;
  },

  createUser: async (payload: UserCreateRequest) => {
    const response = await api.post<UserListItem>(API_ENDPOINTS.IDENTITY.USERS, payload);
    return response.data;
  },

  updateUser: async (id: string, payload: UserUpdateRequest) => {
    const response = await api.put<UserListItem>(
      `${API_ENDPOINTS.IDENTITY.USERS}/${id}`,
      payload
    );
    return response.data;
  },

  deleteUser: async (id: string) => {
    await api.delete(`${API_ENDPOINTS.IDENTITY.USERS}/${id}`);
  },

  getRoles: async () => {
    const response = await api.get<Role[]>(API_ENDPOINTS.IDENTITY.ROLES);
    return response.data;
  },

  createRole: async (payload: RoleRequest) => {
    const response = await api.post<Role>(API_ENDPOINTS.IDENTITY.ROLES, payload);
    return response.data;
  },

  updateRole: async (id: string, payload: RoleRequest) => {
    const response = await api.put<Role>(`${API_ENDPOINTS.IDENTITY.ROLES}/${id}`, payload);
    return response.data;
  },

  getPermissions: async () => {
    const response = await api.get<Permission[]>(API_ENDPOINTS.IDENTITY.PERMISSIONS);
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get<CurrentUserProfile>(API_ENDPOINTS.AUTH.ME);
    return response.data;
  },

  changePassword: async (payload: ChangePasswordRequest) => {
    await api.post(API_ENDPOINTS.AUTH.CHANGE_PASSWORD, payload);
  },
};
