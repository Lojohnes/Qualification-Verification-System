import { adminApi, api, qualificationApi, verificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import { institutionService } from '@/features/institution/services/institutionService';
import { qualificationService } from '@/features/qualification/services/qualificationService';
import type { AuditEvent } from '@/features/audit/services/auditService';

export interface DashboardServiceStatus {
  name: string;
  status: 'operational' | 'down';
}

export interface DashboardData {
  users: number;
  qualifications: number;
  verifications: number;
  documents: number;
  recentActivity: AuditEvent[];
  services: DashboardServiceStatus[];
}

const healthCheck = async (client: typeof api, name: string): Promise<DashboardServiceStatus> => {
  try {
    await client.get('/actuator/health');
    return { name, status: 'operational' };
  } catch {
    return { name, status: 'down' };
  }
};

export const dashboardService = {
  getData: async (): Promise<DashboardData> => {
    const [usersResult, institutionsResult, auditResult, services] = await Promise.all([
      api.get<unknown[]>(API_ENDPOINTS.IDENTITY.USERS),
      institutionService.getInstitutions(),
      adminApi.get<AuditEvent[]>(API_ENDPOINTS.ADMIN.AUDIT_EVENTS),
      Promise.all([
        healthCheck(api, 'Identity Service'),
        healthCheck(qualificationApi, 'Qualification Service'),
        healthCheck(verificationApi, 'Verification Service'),
        healthCheck(adminApi, 'Admin Service'),
      ]),
    ]);

    const qualificationResults = await Promise.allSettled(
      institutionsResult.map((institution) =>
        qualificationService.getQualificationsByInstitution(institution.id)
      )
    );
    const qualifications = qualificationResults.flatMap((result) =>
      result.status === 'fulfilled' ? result.value : []
    );
    const documentResults = await Promise.allSettled(
      qualifications.map((qualification) => qualificationService.getDocuments(qualification.id))
    );
    const documents = documentResults.reduce(
      (total, result) => total + (result.status === 'fulfilled' ? result.value.length : 0),
      0
    );

    return {
      users: usersResult.data.length,
      qualifications: qualifications.length,
      verifications: auditResult.data.filter((event) => event.resourceType === 'verification').length,
      documents,
      recentActivity: auditResult.data.slice(0, 5),
      services,
    };
  },
};
