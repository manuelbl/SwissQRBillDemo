/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { InputFormatter } from './input-formatter';
import { Injectable } from '@angular/core';

/** Formatter for refrence numbers (ISO11659 creditor reference or QR reference number) */
@Injectable()
export class ReferenceNumberFormatter implements InputFormatter {
  rawValue(formattedValue: string): any {
    if (!formattedValue) {
      return '';
    }
    return formattedValue.replace(/\s/g, '');
  }

  formattedValue(rawValue: any): string {
    if (!rawValue) {
      return '';
    }

    let rawString: string = String(rawValue);
    rawString = rawString.replace(/\s/g, '').toUpperCase();
    let formatted = '';

    if (rawString.startsWith('RF')) {
      // groups of 4 digits, starting on the left hand side
      const len = rawString.length;
      for (let p = 0; p < len; p += 4) {
        const e = p + 4 <= len ? p + 4 : len;
        formatted += rawString.substring(p, p + 4);
        if (e < len) {
          formatted += ' ';
        }
      }
    } else {
      // groups of 5 characters, starting at the end
      const len = rawValue.length;
      let t = 0;
      while (t < len) {
        const n = t + ((len - t - 1) % 5) + 1;
        if (t !== 0) {
          formatted += ' ';
        }
        formatted += rawValue.substring(t, n);
        t = n;
      }
    }

    return formatted;
  }
}
