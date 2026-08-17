import { qualificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type {
  Department,
  DepartmentRequest,
  Faculty,
  FacultyRequest,
  Institution,
  InstitutionRequest,
  Program,
  ProgramRequest,
} from '@/types/institution';

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

  getFaculties: async (institutionId?: string) => {
    const response = await qualificationApi.get<Faculty[]>(API_ENDPOINTS.QUALIFICATION.FACULTIES, {
      params: institutionId ? { institutionId } : undefined,
    });
    return response.data;
  },

  createFaculty: async (payload: FacultyRequest) => {
    const response = await qualificationApi.post<Faculty>(
      API_ENDPOINTS.QUALIFICATION.FACULTIES,
      payload
    );
    return response.data;
  },

  updateFaculty: async (id: string, payload: FacultyRequest) => {
    const response = await qualificationApi.put<Faculty>(
      `${API_ENDPOINTS.QUALIFICATION.FACULTIES}/${id}`,
      payload
    );
    return response.data;
  },

  deleteFaculty: async (id: string) => {
    await qualificationApi.delete(`${API_ENDPOINTS.QUALIFICATION.FACULTIES}/${id}`);
  },

  getDepartments: async (facultyId?: string) => {
    const response = await qualificationApi.get<Department[]>(
      API_ENDPOINTS.QUALIFICATION.DEPARTMENTS,
      {
        params: facultyId ? { facultyId } : undefined,
      }
    );
    return response.data;
  },

  createDepartment: async (payload: DepartmentRequest) => {
    const response = await qualificationApi.post<Department>(
      API_ENDPOINTS.QUALIFICATION.DEPARTMENTS,
      payload
    );
    return response.data;
  },

  getDepartmentById: async (id: string) => {
    const response = await qualificationApi.get<Department>(
      `${API_ENDPOINTS.QUALIFICATION.DEPARTMENTS}/${id}`
    );
    return response.data;
  },

  updateDepartment: async (id: string, payload: DepartmentRequest) => {
    const response = await qualificationApi.put<Department>(
      `${API_ENDPOINTS.QUALIFICATION.DEPARTMENTS}/${id}`,
      payload
    );
    return response.data;
  },

  deleteDepartment: async (id: string) => {
    await qualificationApi.delete(`${API_ENDPOINTS.QUALIFICATION.DEPARTMENTS}/${id}`);
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
