/*
 * Swiss QR Bill Generator
 * Copyright (c) 2022 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

export class PaymentValidationError extends Error {
}

// Helper functions for payments

const MOD_10 = [0, 9, 4, 6, 8, 2, 7, 1, 3, 5];

/**
 * Create an ISO 11649 reference by prepending RF and the check digits to the raw reference.
 * @param rawReference raw reference
 * @returns ISO 11649 reference
 */
export function createISO11649(rawReference: string): string {
  rawReference = whiteSpaceRemoved(rawReference);
  const modulo = calculateMod97('RF00' + rawReference);
  return 'RF' + ('00' + (98 - modulo)).slice(-2) + rawReference;
}

export function whiteSpaceRemoved(str: string | undefined): string {
  if (str === undefined)
    return '';
  return str.replace(/\s/g, '');
}

export function calculateMod97(reference: string): number {
  const rearranged = reference.substring(4) + reference.substring(0, 4);
  const len = rearranged.length;
  let sum = 0;
  for (let i = 0; i < len; i++) {
    const cc = rearranged.charCodeAt(i);
    if (cc >= 48 && cc <= 57) {
      sum = sum * 10 + (cc - 48);
    } else if (cc >= 65 && cc <= 90) {
      sum = sum * 100 + (cc - 65 + 10);
    } else if (cc >= 97 && cc <= 122) {
      sum = sum * 100 + (cc - 97 + 10);
    } else {
      throw new PaymentValidationError(
        'Invalid character in reference: ' + reference
      );
    }
    if (sum > 9999999) {
      sum = sum % 97;
    }
  }

  sum = sum % 97;
  return sum;
}

/**
 * Create a QR reference by appending the check digit to the given string
 * @param reference reference without check digit
 * @returns QR reference
 */
export function createQRReference(reference: string): string {
  reference = whiteSpaceRemoved(reference);
  if (reference.length > 26) {
    throw new PaymentValidationError(
      'Reference number too long: ' + reference
    );
  }
  const mod10 = calcMod10(reference);
  return reference + String.fromCharCode(48 + mod10);
}

function calcMod10(reference: string): number {
  let carry = 0;
  const len = reference.length;
  if (reference.replace(/\D/g, '') !== reference) {
    throw new PaymentValidationError(
      'Invalid character in reference number: ' + reference
    );
  }

  for (let i = 0; i < len; i++) {
    const digit = reference.charCodeAt(i) - 48;
    carry = MOD_10[(carry + digit) % 10];
  }
  return (10 - carry) % 10;
}

/**
 * Checks if the specified account number is a QR IBAN or a regular IBAN
 * @param account IBAN account number
 * @returns `true` if it is a QR IBAN, `false` if it si a regular IBAN
 */
export function isQRIBAN(account: string | undefined): boolean {
  if (account === undefined)
    return false;

  account = whiteSpaceRemoved(account);
  return account.length > 6
    && (account.substring(4, 6) === '30' || account.substring(4, 6) === '31');
}

/**
 * Formats the specified IBAN by inserting spaces between groups of letters/digits
 * @param iban IBAN
 * @returns formatted IBAN
 */
export function formatIBAN(iban: string | undefined): string {
  if (iban === undefined)
    return '';

  iban = iban.replace(/\s/g, '').toUpperCase();

  let formatted = '';
  const len = iban.length;
  for (let p = 0; p < len; p += 4) {
    const e = p + 4 <= len ? p + 4 : len;
    formatted += iban.substring(p, p + 4);
    if (e < len) {
      formatted += ' ';
    }
  }
  return formatted;
}

/**
 * Formats an ISO payment reference by inserting spaces between groups of four letters/digits
 * (start at the left).
 * @param reference unformatted reference
 * @returns formatted reference
 */
export function formatISOReference(reference: string): string {
  reference = whiteSpaceRemoved(reference);
  let formatted = '';
  const len = reference.length;
  for (let p = 0; p < len; p += 4) {
    formatted += reference.substring(p, p + 4);
    if (p + 4 < len)
      formatted += ' ';
  }
  return formatted;
}

/**
 * Formats a QR payment reference by inserting spaces between groups of five digits
 * (start at the right).
 * @param reference unformatted reference
 * @returns formatted reference
 */
export function formatQRReference(reference: string): string {
  reference = whiteSpaceRemoved(reference);
  let formatted = '';
  const len = reference.length;
  let t = 0;
  while (t < len) {
    const n = t + ((len - t - 1) % 5) + 1;
    if (t !== 0)
      formatted += ' ';
    formatted += reference.substring(t, n);
    t = n;
  }
  return formatted;
}
