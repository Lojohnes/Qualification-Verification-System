import { adminApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';

export interface AuditEvent {
  id: string;
  eventType: string;
  action: string;
  actorId?: string;
  actorName?: string;
  actorRole?: string;
  organizationId?: string;
  resourceType?: string;
  resourceId?: string;
  resourceName?: string;
  previousValues?: string;
  newValues?: string;
  ipAddress?: string;
  deviceInfo?: string;
  occurredAt: string;
}

export interface AuditEventFilters {
  actorName?: string;
  resourceType?: string;
  fromDate?: string;
  toDate?: string;
}

export const auditService = {
  searchEvents: async (filters: AuditEventFilters): Promise<AuditEvent[]> => {
    const response = await adminApi.get<AuditEvent[]>(API_ENDPOINTS.ADMIN.AUDIT_EVENTS, {
      params: filters,
    });
    return response.data;
  },
};
