/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { InputFormatter } from './input-formatter';
import { Injectable } from '@angular/core';

/** Formatter for IBAN account number */
@Injectable()
export class IBANFormatter implements InputFormatter {

  rawValue(formattedValue: string): any | undefined {
    return formattedValue.replace(/\s/g, '');
  }

  formattedValue(rawValue: any | undefined): string {
    if (!rawValue) {
      return '';
    }
    let rawString: string = String(rawValue);
    rawString = rawString.replace(/\s/g, '').toUpperCase();

    let formatted = '';
    const len = rawString.length;
    for (let p = 0; p < len; p += 4) {
      const e = p + 4 <= len ? p + 4 : len;
      formatted += rawString.substring(p, p + 4);
      if (e < len) {
        formatted += ' ';
      }
    }
    return formatted;
  }
}
