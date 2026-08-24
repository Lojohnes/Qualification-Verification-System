import { API_ENDPOINTS } from '@/constants/api';
import { verificationApi } from '@/config/axios';
import type {
  QrVerificationRequest,
  VerificationResultResponse,
} from '@/types/verification';

export const verificationService = {
  verifyQr: async (request: QrVerificationRequest) => {
    const response = await verificationApi.post<VerificationResultResponse>(
      API_ENDPOINTS.VERIFICATION.VERIFY_QR,
      request
    );
    return response.data;
  },
};
