import { API_ENDPOINTS } from '@/constants/api';
import { verificationApi } from '@/config/axios';
import type {
  VerificationRequest,
  VerificationResponse,
} from '@/types/verification';

export const verificationService = {
  verify: async (request: VerificationRequest) => {
    const response = await verificationApi.post<VerificationResponse>(
      API_ENDPOINTS.VERIFICATION.VERIFY,
      request
    );
    return response.data;
  },
};
