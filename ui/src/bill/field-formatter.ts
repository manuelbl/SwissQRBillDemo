/*
 * Swiss QR Bill Generator
 * Copyright (c) 2022 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

export interface FieldFormatter {
  formattedValue: (rawValue: any) => string;
  rawValue: (formattedValue: string) => any;
};


export class StringFormatter {
  formattedValue(rawValue: any): string {
    if (rawValue === undefined)
      return '';
    return rawValue.toString().trim();
  }

  rawValue(formattedValue: string): string {
    return formattedValue.trim();
  }
}
