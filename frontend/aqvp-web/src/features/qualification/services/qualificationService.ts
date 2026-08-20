import { qualificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type {
  Qualification,
  QualificationAmendRequest,
  QualificationIssueRequest,
  QualificationRequest,
  QualificationRevokeRequest,
  Student,
  StudentRequest,
  StudentUpdateRequest,
} from '@/types/qualification';

// ---------------------------------------------------------------------------
// Students
// ---------------------------------------------------------------------------
export const studentService = {
  getStudentsByInstitution: async (institutionId: string) => {
    const response = await qualificationApi.get<Student[]>(API_ENDPOINTS.QUALIFICATION.STUDENTS, {
      params: { institutionId },
    });
    return response.data;
  },

  getStudentById: async (id: string) => {
    const response = await qualificationApi.get<Student>(
      `${API_ENDPOINTS.QUALIFICATION.STUDENTS}/${id}`
    );
    return response.data;
  },

  createStudent: async (payload: StudentRequest) => {
    const response = await qualificationApi.post<Student>(
      API_ENDPOINTS.QUALIFICATION.STUDENTS,
      payload
    );
    return response.data;
  },

  updateStudent: async (id: string, payload: StudentUpdateRequest) => {
    const response = await qualificationApi.put<Student>(
      `${API_ENDPOINTS.QUALIFICATION.STUDENTS}/${id}`,
      payload
    );
    return response.data;
  },

  deactivateStudent: async (id: string) => {
    await qualificationApi.delete(`${API_ENDPOINTS.QUALIFICATION.STUDENTS}/${id}`);
  },
};

// ---------------------------------------------------------------------------
// Qualifications
// ---------------------------------------------------------------------------
export const qualificationService = {
  getQualificationsByInstitution: async (institutionId: string) => {
    const response = await qualificationApi.get<Qualification[]>(
      API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS,
      { params: { institutionId } }
    );
    return response.data;
  },

  getQualificationsByStudent: async (studentId: string) => {
    const response = await qualificationApi.get<Qualification[]>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/by-student/${studentId}`
    );
    return response.data;
  },

  getQualificationById: async (id: string) => {
    const response = await qualificationApi.get<Qualification>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}`
    );
    return response.data;
  },

  createQualification: async (payload: QualificationRequest) => {
    const response = await qualificationApi.post<Qualification>(
      API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS,
      payload
    );
    return response.data;
  },

  updateQualification: async (id: string, payload: QualificationRequest) => {
    const response = await qualificationApi.put<Qualification>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}`,
      payload
    );
    return response.data;
  },

  issueQualification: async (id: string, payload: QualificationIssueRequest) => {
    const response = await qualificationApi.post<Qualification>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/issue`,
      payload
    );
    return response.data;
  },

  amendQualification: async (id: string, payload: QualificationAmendRequest) => {
    const response = await qualificationApi.post<Qualification>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/amend`,
      payload
    );
    return response.data;
  },

  revokeQualification: async (id: string, payload: QualificationRevokeRequest) => {
    const response = await qualificationApi.post<Qualification>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/revoke`,
      payload
    );
    return response.data;
  },
};
