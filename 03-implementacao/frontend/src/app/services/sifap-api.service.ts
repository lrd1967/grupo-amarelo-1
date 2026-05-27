import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BeneficiaryRequest, BeneficiaryResponse } from '../models/beneficiary.model';
import { PaymentCalculationRequest, PaymentResponse } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class SifapApiService {
  private readonly baseUrl = 'http://localhost:8080/api/v1';

  constructor(private readonly http: HttpClient) {}

  public createOrUpdateBeneficiary(payload: BeneficiaryRequest): Observable<BeneficiaryResponse> {
    return this.http.post<BeneficiaryResponse>(`${this.baseUrl}/beneficiaries`, payload);
  }

  public listBeneficiaries(): Observable<BeneficiaryResponse[]> {
    return this.http.get<BeneficiaryResponse[]>(`${this.baseUrl}/beneficiaries`);
  }

  public calculatePayment(payload: PaymentCalculationRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.baseUrl}/payments/calculate`, payload);
  }

  public listPayments(): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(`${this.baseUrl}/payments`);
  }
}
