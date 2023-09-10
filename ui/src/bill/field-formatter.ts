//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { BillValue } from "./bill-helper";

export interface FieldFormatter {
  formattedValue: (rawValue: BillValue) => string;
  rawValue: (formattedValue: string) => BillValue;
}


export class StringFormatter {
  formattedValue(rawValue: BillValue): string {
    if (rawValue === undefined)
      return '';
    return rawValue.toString().trim();
  }

  rawValue(formattedValue: string): string {
    return formattedValue.trim();
  }
}
