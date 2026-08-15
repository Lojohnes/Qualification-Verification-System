export interface Institution {
  id: string;
  name: string;
  code: string;
  description?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  version?: number;
}

export interface InstitutionRequest {
  name: string;
  code: string;
  description?: string;
  active?: boolean;
}

export interface Program {
  id: string;
  institutionId: string;
  institutionName?: string;
  departmentId: string;
  departmentName?: string;
  name: string;
  code: string;
  degreeLevel?: string;
  durationSemesters?: number;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  version?: number;
}

export interface ProgramRequest {
  institutionId: string;
  departmentId: string;
  name: string;
  code: string;
  degreeLevel?: string;
  durationSemesters?: number;
}
