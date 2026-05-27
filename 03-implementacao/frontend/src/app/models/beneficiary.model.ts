export interface BeneficiaryRequest {
  cpf: string;
  operationType: 'I' | 'A';
  birthDate: string;
  regionCode: number;
  programType: 'A' | 'P' | 'T';
  familyIncome: number;
  dependents: number;
  documentsOk: boolean;
  nis?: string;
}

export interface BeneficiaryResponse {
  id: number;
  cpf: string;
  status: 'A' | 'S' | 'C' | 'I' | 'D';
  birthDate: string;
  regionCode: number;
  programType: 'A' | 'P' | 'T';
  familyIncome: number;
  dependents: number;
  documentsOk: boolean;
  nis?: string;
  eligible: boolean;
}
