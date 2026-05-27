| #   | Termo | Expansão | Programa | Contexto |
| --- | ----- | -------- | -------- | -------- |
| 1   | STATUS | Status do Beneficiário | CADBENEF.NSN | A=ativo S=suspenso C=cancelado I=inativo D=desligado |
| 2   | #OPER | Código da Operação | CADBENEF.NSN | I=INCLUSAO A=ALTERACAO |
| 3   | PARENTESCO | Grau de Parentesco | CADDEPEND.NSN BENEFICIARIO.ddm| FI=FILHO CO=CONJUGE IR=IRMAO NT=NETO TU=TUTEL |
| 4   | TIPO | Tipo de Programa | CADPROG.NSN | A=ASSISTENCIAL P=PREVIDENC T=TRABALHO | 
| 5   | COMPETENCIA | Competência do Benefício | CALCBENF.NSN | Formato AAAAMM  |
| 6   | TIPO-PGTO  | Tipo de Pagamento | CALCBENF.NSN | N=NORMAL D=DECIMO T=TERCEIRO |
| 7   | #TAB-REG | Unidade da Federação | CALCBENF.NSN | Sigla do estado com duas letras         |
| 8   | TIPO-DSCT | Tipo de Desconto | CALCDSCT.NSN | C=CONTRIB I=IMPOSTO J=JUDICIAL S=SINDICAL P=PENSAO A=ADMIN         |
| 9   | #TIPO-BUSCA | Tipo da Busca | CONSBENF.NSN | C=CPF N=NIS |
| 10  | #TIPO-SAIDA | Tipo da saída da Informação | RELAUDIT.NSN |T=TELA I=IMPRESSORA |
| 11  | #RESULTADO | VALIDACAO DADOS CADASTRAIS DO BENEFICIARIO | VALBENEF.NSN | V=VALIDO I=INVALIDO |
| 12  | #DIAS-MES | Quantidade de dias do mês | VALBENEF.NSN | Numérico considerando 29 dias para fevereiro |
| 13  | #CNAB-BANCO | Código do Banco | BATCHCON.NSN | Numérido de 3 dígitos |
| 14  | #MAX-LINHAS | Numero máximo de Linhas | BATCHREL.NSN | PADRAO IMPRESSORA MAINFRAME = 66 |
| 15  | #LINHA | Posição de Linha | BATCHREL.NSN | FORCA CABECALHO NA PRIMEIRA PAG = 99 |
| 16  | SIT-BENEFICIARIO  | Situação do Beneficiário | BENEFICIARIO.ddm | A=ATV S=SUSP C=CANC I=INAT D=DESL |
| 17  | COD-ACAO | Código da Ação | AUDITORIA.ddm  | IN=INCLUSAO AL=ALTERACAO EX=EXCLUSAO CO=CONSULTA  |
| 18  | EST-CIVIL | Estado Civil | BENEFICIARIO.ddm | S=SOLT C=CAS D=DIV V=VIUV U=UN.EST |
| 19  | SIT-PAGAMENTO | Situação do Pagamento | PAGAMENTO.ddm |  P=PEND G=GERADO E=EMITIDO C=CONFIRMADO D=DEVOLVIDO X=CANCELADO R=REPROCESSADO |
| 20  | TIPO-CONTA | Tipo de Conta | PAGAMENTO.ddm | C=CORRENTE P=POUPANCA |
| 21  |       |          |          |          |
| 22  |       |          |          |          |
| 23  |       |          |          |          |
| 24  |       |          |          |          |
| 25  |       |          |          |          |
| 26  |       |          |          |          |
| 27  |       |          |          |          |
| 28  |       |          |          |          |
| 29  |       |          |          |          |
| 30  |       |          |          |          |
