export interface PaymentCalculationRequest {
  beneficiaryId: number;
  competenceMonth: number;
  grossAmount: number;
  nonJudicialDeduction: number;
  judicialDeduction: number;
  correctionAmount: number;
}

export interface PaymentResponse {
  id: number;
  beneficiaryId: number;
  competenceMonth: number;
  grossAmount: number;
  deductionAmount: number;
  correctionAmount: number;
  netAmount: number;
  paymentType: 'N' | 'D';
  status: string;
  corrected: boolean;
}
