CREATE TABLE beneficiaries (
  id BIGSERIAL PRIMARY KEY,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  operation_type CHAR(1) NOT NULL,
  status VARCHAR(1) NOT NULL,
  birth_date DATE NOT NULL,
  region_code INTEGER NOT NULL,
  program_type VARCHAR(1) NOT NULL,
  family_income NUMERIC(14,2) NOT NULL,
  dependents INTEGER NOT NULL,
  documents_ok BOOLEAN NOT NULL,
  nis VARCHAR(16)
);

CREATE TABLE payments (
  id BIGSERIAL PRIMARY KEY,
  beneficiary_id BIGINT NOT NULL,
  competence_month INTEGER NOT NULL,
  gross_amount NUMERIC(14,2) NOT NULL,
  deduction_amount NUMERIC(14,2) NOT NULL,
  correction_amount NUMERIC(14,2) NOT NULL,
  net_amount NUMERIC(14,2) NOT NULL,
  payment_type VARCHAR(1) NOT NULL,
  corrected BOOLEAN NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_payments_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiaries(id)
);
