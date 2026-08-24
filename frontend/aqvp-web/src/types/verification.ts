export interface VerificationRequest {
  qualificationNumber?: string;
  securityIdentifier?: string;
  method: 'MANUAL' | 'QR_SCAN';
}

export interface VerificationResponse {
  status: string;
  message: string;
  qualificationNumber: string;
  qualificationId: string;
  qualificationName: string;
  classification: string;
  yearOfAward: number;
  issuedAt: string;
  verifiedAt: string;
}
