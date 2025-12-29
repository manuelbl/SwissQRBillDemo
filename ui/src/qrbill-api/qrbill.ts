//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Address } from './address';
import { BillFormat } from './bill-format';

export class QrBill {
  version = 'V2_0';
  amount?: number;
  currency?: string;
  account?: string;
  creditor?: Address;
  reference?: string;
  unstructuredMessage?: string;
  billInformation?: string;
  debtor?: Address;
  format?: BillFormat;
  characterSet?: string = 'extended-latin';
}
