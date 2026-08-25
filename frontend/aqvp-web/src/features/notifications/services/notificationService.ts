import { adminApi } from '@/config/axios';
import { API_ENDPOINTS } from '@/constants/api';

export type NotificationChannel = 'EMAIL' | 'SMS';
export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED' | 'RETRYING';

export interface SendNotificationRequest {
  templateCode: string;
  channel: NotificationChannel;
  recipient: string;
  subject?: string;
  message: string;
}

export interface Notification extends SendNotificationRequest {
  id: string;
  status: NotificationStatus;
  attempts?: number;
  sentAt?: string;
  errorMessage?: string;
  providerReference?: string;
  providerResponse?: string;
}

export const notificationService = {
  send: async (payload: SendNotificationRequest): Promise<Notification> => {
    const response = await adminApi.post<Notification>(API_ENDPOINTS.ADMIN.NOTIFICATIONS, payload);
    return response.data;
  },
};
