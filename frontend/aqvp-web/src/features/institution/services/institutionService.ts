import { qualificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type { Institution, InstitutionRequest, Program, ProgramRequest } from '@/types/institution';

export const institutionService = {
  getInstitutions: async () => {
    const response = await qualificationApi.get<Institution[]>(
      API_ENDPOINTS.QUALIFICATION.INSTITUTIONS
    );
    return response.data;
  },

  getInstitutionById: async (id: string) => {
    const response = await qualificationApi.get<Institution>(
      `${API_ENDPOINTS.QUALIFICATION.INSTITUTIONS}/${id}`
    );
    return response.data;
  },

  createInstitution: async (payload: InstitutionRequest) => {
    const response = await qualificationApi.post<Institution>(
      API_ENDPOINTS.QUALIFICATION.INSTITUTIONS,
      payload
    );
    return response.data;
  },

  updateInstitution: async (id: string, payload: InstitutionRequest) => {
    const response = await qualificationApi.put<Institution>(
      `${API_ENDPOINTS.QUALIFICATION.INSTITUTIONS}/${id}`,
      payload
    );
    return response.data;
  },

  deactivateInstitution: async (id: string) => {
    await qualificationApi.delete(`${API_ENDPOINTS.QUALIFICATION.INSTITUTIONS}/${id}`);
  },

  getPrograms: async (institutionId?: string) => {
    const response = await qualificationApi.get<Program[]>(API_ENDPOINTS.QUALIFICATION.PROGRAMS, {
      params: institutionId ? { institutionId } : undefined,
    });
    return response.data;
  },

  getProgramById: async (id: string) => {
    const response = await qualificationApi.get<Program>(
      `${API_ENDPOINTS.QUALIFICATION.PROGRAMS}/${id}`
    );
    return response.data;
  },

  createProgram: async (payload: ProgramRequest) => {
    const response = await qualificationApi.post<Program>(
      API_ENDPOINTS.QUALIFICATION.PROGRAMS,
      payload
    );
    return response.data;
  },

  updateProgram: async (id: string, payload: ProgramRequest) => {
    const response = await qualificationApi.put<Program>(
      `${API_ENDPOINTS.QUALIFICATION.PROGRAMS}/${id}`,
      payload
    );
    return response.data;
  },

  deleteProgram: async (id: string) => {
    await qualificationApi.delete(`${API_ENDPOINTS.QUALIFICATION.PROGRAMS}/${id}`);
  },
};
