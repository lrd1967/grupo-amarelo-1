import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SifapApiService } from '../services/sifap-api.service';
import { BeneficiaryRequest, BeneficiaryResponse } from '../models/beneficiary.model';
import { PaymentCalculationRequest, PaymentResponse } from '../models/payment.model';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <h1>SIFAP Moderno - Java 21 + Angular</h1>

      <section class="card">
        <h2>Cadastro de Beneficiario (REQ-CAD/REQ-VAL)</h2>
        <div class="grid">
          <input [(ngModel)]="beneficiary.cpf" placeholder="CPF (11 digitos)" />
          <select [(ngModel)]="beneficiary.operationType">
            <option value="I">Inclusao</option>
            <option value="A">Alteracao</option>
          </select>
          <input [(ngModel)]="beneficiary.birthDate" type="date" />
          <input [(ngModel)]="beneficiary.regionCode" type="number" placeholder="Codigo regiao" />
          <select [(ngModel)]="beneficiary.programType">
            <option value="A">Assistencial</option>
            <option value="P">Previdenciario</option>
            <option value="T">Trabalho</option>
          </select>
          <input [(ngModel)]="beneficiary.familyIncome" type="number" placeholder="Renda familiar" />
          <input [(ngModel)]="beneficiary.dependents" type="number" placeholder="Dependentes" />
          <input [(ngModel)]="beneficiary.nis" placeholder="NIS" />
          <label>
            <input [(ngModel)]="beneficiary.documentsOk" type="checkbox" />
            Documentos OK
          </label>
          <button (click)="saveBeneficiary()">Salvar beneficiario</button>
        </div>
      </section>

      <section class="card">
        <h2>Calculo de Pagamento (REQ-PAY/REQ-DSCT)</h2>
        <div class="grid">
          <input [(ngModel)]="payment.beneficiaryId" type="number" placeholder="Beneficiary ID" />
          <input [(ngModel)]="payment.competenceMonth" type="number" placeholder="Competencia (1-12)" />
          <input [(ngModel)]="payment.grossAmount" type="number" placeholder="Valor bruto" />
          <input [(ngModel)]="payment.nonJudicialDeduction" type="number" placeholder="Desconto nao judicial" />
          <input [(ngModel)]="payment.judicialDeduction" type="number" placeholder="Desconto judicial" />
          <input [(ngModel)]="payment.correctionAmount" type="number" placeholder="Correcao" />
          <button (click)="calculatePayment()">Calcular pagamento</button>
        </div>
      </section>

      <section class="card">
        <h2>Beneficiarios</h2>
        <button (click)="refresh()">Atualizar listas</button>
        <ul>
          <li *ngFor="let b of beneficiaries">
            ID {{ b.id }} - CPF {{ b.cpf }} - Status {{ b.status }} - Elegivel: {{ b.eligible ? 'Sim' : 'Nao' }}
          </li>
        </ul>
      </section>

      <section class="card">
        <h2>Pagamentos</h2>
        <ul>
          <li *ngFor="let p of payments">
            Pgto {{ p.id }} - Beneficiario {{ p.beneficiaryId }} - Liquido {{ p.netAmount }} - Tipo {{ p.paymentType }}
          </li>
        </ul>
      </section>

      <section class="card" *ngIf="message">
        <strong>{{ message }}</strong>
      </section>
    </div>
  `
})
export class HomePageComponent implements OnInit {
  public beneficiary: BeneficiaryRequest = {
    cpf: '',
    operationType: 'I',
    birthDate: '1990-01-01',
    regionCode: 1,
    programType: 'A',
    familyIncome: 0,
    dependents: 0,
    documentsOk: true,
    nis: ''
  };

  public payment: PaymentCalculationRequest = {
    beneficiaryId: 0,
    competenceMonth: 1,
    grossAmount: 1000,
    nonJudicialDeduction: 0,
    judicialDeduction: 0,
    correctionAmount: 0
  };

  public beneficiaries: BeneficiaryResponse[] = [];
  public payments: PaymentResponse[] = [];
  public message = '';

  constructor(private readonly api: SifapApiService) {}

  public ngOnInit(): void {
    this.refresh();
  }

  public saveBeneficiary(): void {
    this.api.createOrUpdateBeneficiary(this.beneficiary).subscribe({
      next: (saved) => {
        this.message = `Beneficiario salvo com ID ${saved.id}`;
        this.payment.beneficiaryId = saved.id;
        this.refresh();
      },
      error: (err: { error?: { detail?: string } }) => {
        this.message = err.error?.detail ?? 'Falha ao salvar beneficiario';
      }
    });
  }

  public calculatePayment(): void {
    this.api.calculatePayment(this.payment).subscribe({
      next: (saved) => {
        this.message = `Pagamento calculado com liquido ${saved.netAmount}`;
        this.refresh();
      },
      error: (err: { error?: { detail?: string } }) => {
        this.message = err.error?.detail ?? 'Falha ao calcular pagamento';
      }
    });
  }

  public refresh(): void {
    this.api.listBeneficiaries().subscribe((list) => {
      this.beneficiaries = list;
    });
    this.api.listPayments().subscribe((list) => {
      this.payments = list;
    });
  }
}
