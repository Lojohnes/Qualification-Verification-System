import { qualificationApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';

export const documentService = {
  downloadCertificate: async (qualificationId: string): Promise<Blob> => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${qualificationId}/certificate`,
      { responseType: 'blob' }
    );
    return response.data;
  },

  downloadTranscript: async (qualificationId: string): Promise<Blob> => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${qualificationId}/transcript`,
      { responseType: 'blob' }
    );
    return response.data;
  },

  downloadQrCode: async (qualificationId: string): Promise<Blob> => {
    const response = await qualificationApi.get<Blob>(
      `${API_ENDPOINTS.QUALIFICATION.QUALIFICATIONS}/${qualificationId}/qr`,
      { responseType: 'blob' }
    );
    return response.data;
  },
};
