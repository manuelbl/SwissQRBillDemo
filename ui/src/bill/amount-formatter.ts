//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { BillValue } from "./bill-helper";
import { FieldFormatter } from "./field-formatter";

/**
 * Formatter for amount (thousand's separator and rounded to two fractional digits)
 * */
export class AmountFormatter implements FieldFormatter {
  private language: string;
  private systemDecimalSeparator: string;
  private userDecimalSeparator: string;
  private cleaner: RegExp;

  constructor(language: string) {
    this.language = language;
    this.systemDecimalSeparator = (1.1).toFixed(1).substring(1, 2);
    this.userDecimalSeparator = (1.1)
      .toLocaleString(this.language)
      .substring(1, 2);
    this.cleaner = new RegExp('[^0-9' + this.userDecimalSeparator + ']', 'g');
  }

  private static rounded(rawValue: number): number {
    // avoid 'toFixed()' for rounded as it is buggy
    return Math.round(rawValue * 100) / 100;
  }

  rawValue(formattedValue: string): BillValue {
    let cleanedValue = formattedValue.replace(this.cleaner, '');
    if (cleanedValue === '')
      return undefined;
    
    if (this.userDecimalSeparator !== this.systemDecimalSeparator) {
      cleanedValue = cleanedValue.replace(
        this.userDecimalSeparator,
        this.systemDecimalSeparator
      );
    }
    const num = Number(cleanedValue);
    return AmountFormatter.rounded(num);
  }

  formattedValue(rawValue: BillValue): string {
    if (rawValue === undefined)
      return '';
    
    const n = AmountFormatter.rounded(Number(rawValue));
    return n.toLocaleString(this.language, { minimumFractionDigits: 2 });
  }

  editValue(rawValue: number | string | undefined): string {
    if (rawValue === undefined)
      return '';

    const n = AmountFormatter.rounded(Number(rawValue));
    return n
      .toLocaleString(this.language, { minimumFractionDigits: 2 })
      .replace(this.cleaner, '');
  }
}
