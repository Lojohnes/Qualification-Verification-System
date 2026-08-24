export interface ConsentRequest {
  consentType: 'ATTESTED_BY_VERIFIER' | 'HOLDER_TOKEN' | 'DOCUMENTED_CONSENT';
  scope: 'BASIC_DETAILS' | 'FULL_DETAILS' | 'STATUS_ONLY';
  holderFirstName?: string;
  holderLastName?: string;
  dateOfBirth?: string;
  holderEmail?: string;
  grantedAt?: string;
  expiresAt?: string;
  consentReference?: string;
}

export interface EvidenceRequest {
  qualificationNumber?: string;
  studentNumber?: string;
  holderFirstName?: string;
  holderLastName?: string;
  yearOfAward?: number;
  qualificationName?: string;
  institutionId?: string;
  institutionName?: string;
}

export interface QrVerificationRequest {
  qrPayload: string;
  purpose: 'EMPLOYMENT' | 'EDUCATION' | 'IMMIGRATION' | 'OTHER';
  consent: ConsentRequest;
  evidence?: EvidenceRequest;
}

export interface VerifiedQualification {
  qualificationNumber: string;
  qualificationName: string;
  qualificationType: string;
  classification: string;
  yearOfAward: number;
  status: string;
  issuedAt: string;
  institutionName: string;
}

export interface VerifiedHolder {
  firstName: string;
  lastName: string;
}

export interface VerificationResultResponse {
  verificationRequestId: string;
  resultId: string;
  outcome: 'VERIFIED' | 'NOT_VERIFIED' | 'PARTIAL' | 'PENDING_CONSENT' | 'ERROR';
  confidence: 'HIGH' | 'MEDIUM' | 'LOW' | 'NONE';
  matchScore: number;
  qualification: VerifiedQualification;
  holder: VerifiedHolder;
  verifiedAt: string;
}
