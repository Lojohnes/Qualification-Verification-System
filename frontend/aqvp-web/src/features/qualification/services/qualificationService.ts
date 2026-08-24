import { qualificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';
import type {
  Qualification,
  QualificationAmendRequest,
  QualificationDocument,
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

  generateCertificate: async (id: string) => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/certificate`,
      { responseType: 'blob' }
    );
    return response.data;
  },

  generateTranscript: async (id: string) => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/transcript`,
      { responseType: 'blob' }
    );
    return response.data;
  },

  generateQrCode: async (id: string) => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/qr`,
      { responseType: 'blob' }
    );
    return response.data;
  },

  generateCertificateMetadata: async (id: string) => {
    const response = await qualificationApi.get<QualificationDocument>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/certificate/metadata`
    );
    return response.data;
  },

  generateTranscriptMetadata: async (id: string) => {
    const response = await qualificationApi.get<QualificationDocument>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/transcript/metadata`
    );
    return response.data;
  },

  generateQrCodeMetadata: async (id: string) => {
    const response = await qualificationApi.get<QualificationDocument>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/qr/metadata`
    );
    return response.data;
  },

  getDocuments: async (id: string) => {
    const response = await qualificationApi.get<QualificationDocument[]>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${id}/documents`
    );
    return response.data;
  },

  getDocument: async (documentId: string) => {
    const response = await qualificationApi.get<QualificationDocument>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/documents/${documentId}`
    );
    return response.data;
  },

  downloadDocument: async (documentId: string) => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/documents/${documentId}/download`,
      { responseType: 'blob' }
    );
    return response.data;
  },
};
