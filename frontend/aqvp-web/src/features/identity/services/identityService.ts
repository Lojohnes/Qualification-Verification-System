import { api } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type { Permission, Role, UserListItem } from '@/types/identity';

export const identityService = {
  getUsers: async () => {
    const response = await api.get<UserListItem[]>(API_ENDPOINTS.IDENTITY.USERS);
    return response.data;
  },

  getRoles: async () => {
    const response = await api.get<Role[]>(API_ENDPOINTS.IDENTITY.ROLES);
    return response.data;
  },

  getPermissions: async () => {
    const response = await api.get<Permission[]>(API_ENDPOINTS.IDENTITY.PERMISSIONS);
    return response.data;
  },
};
