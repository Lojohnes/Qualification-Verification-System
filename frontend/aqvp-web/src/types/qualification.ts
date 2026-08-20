// ---------------------------------------------------------------------------
// Shared base
// ---------------------------------------------------------------------------
export interface AuditFields {
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  version?: number;
}

// ---------------------------------------------------------------------------
// Student
// ---------------------------------------------------------------------------
export interface Student extends AuditFields {
  id: string;
  studentNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  dateOfBirth?: string;
  nationalId?: string;
  institutionId: string;
  active: boolean;
}

export interface StudentRequest {
  studentNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  dateOfBirth?: string;
  nationalId?: string;
  institutionId: string;
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  email?: string;
  dateOfBirth?: string;
  nationalId?: string;
}

// ---------------------------------------------------------------------------
// Qualification
// ---------------------------------------------------------------------------
export type QualificationStatus = 'DRAFT' | 'ISSUED' | 'AMENDED' | 'REVOKED' | 'WITHDRAWN';

export type QualificationType =
  | 'DEGREE'
  | 'DIPLOMA'
  | 'CERTIFICATE'
  | 'PROFESSIONAL_QUALIFICATION'
  | 'EXAMINATION_CERTIFICATE';

export interface QualificationStatusHistory {
  id: string;
  previousStatus?: string;
  newStatus: string;
  changedBy?: string;
  reason?: string;
  changedAt: string;
}

export interface Qualification extends AuditFields {
  id: string;
  qualificationNumber: string;
  studentId: string;
  institutionId: string;
  programId?: string;
  qualificationType: QualificationType;
  qualificationName: string;
  classification?: string;
  yearOfAward: number;
  status: QualificationStatus;
  securityIdentifier?: string;
  issuedAt?: string;
  revokedAt?: string;
  revocationReason?: string;
  notes?: string;
  statusHistory: QualificationStatusHistory[];
}

export interface QualificationRequest {
  qualificationNumber: string;
  studentId: string;
  institutionId: string;
  programId?: string;
  qualificationType: QualificationType;
  qualificationName: string;
  classification?: string;
  yearOfAward: number;
  notes?: string;
}

export interface QualificationIssueRequest {
  notes?: string;
}

export interface QualificationAmendRequest {
  reason: string;
  qualificationName: string;
  classification?: string;
  notes?: string;
}

export interface QualificationRevokeRequest {
  reason: string;
}

export const QUALIFICATION_TYPES: { value: QualificationType; label: string }[] = [
  { value: 'DEGREE', label: 'Degree' },
  { value: 'DIPLOMA', label: 'Diploma' },
  { value: 'CERTIFICATE', label: 'Certificate' },
  { value: 'PROFESSIONAL_QUALIFICATION', label: 'Professional Qualification' },
  { value: 'EXAMINATION_CERTIFICATE', label: 'Examination Certificate' },
];
