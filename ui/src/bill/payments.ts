/*
 * Swiss QR Bill Generator
 * Copyright (c) 2022 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

// Helper functions for payments

const MOD_10 = [0, 9, 4, 6, 8, 2, 7, 1, 3, 5];

/**
 * Returns the specified string with all whitespace characters removed.
 * 
 * If `undefined` is passed, an empty string is returned.
 */
export function whiteSpaceRemoved(str: string | undefined): string {
  if (str === undefined)
    return '';
  return str.replace(/\s/g, '');
}

/**
 * Returns the specified string with all whitespace characters removed
 * and lowercase letters converted to uppercase.
 * 
 * If `undefined` is passed, an empty string is returned.
 */
export function whiteSpaceRemovedAndUpperCase(str: string | undefined): string {
  if (str === undefined)
    return '';
  return str.replace(/\s/g, '').toUpperCase();
}

/**
 * Returns the specified string with all whitespace and non-alphanumeric characters removed.
 * 
 * If `undefined` is passed, an empty string is returned. Lowercase letters are converted
 * to uppercase letters.
 */
export function nonAlnumRemoved(str: string | undefined): string {
  if (str === undefined)
    return '';
  str = str.toUpperCase();
  return str.replace(/[^A-Z0-9]/g, '');
}

/**
 * Returns the specified string with all characters except digits removed.
 * 
 * If `undefined` is passed, an empty string is returned.
 */
export function nonDigitRemoved(str: string | undefined): string {
  if (str === undefined)
    return '';
  return str.replace(/\D/g, '');
}

/**
 * Create an ISO 11649 reference by prepending RF and the check digits to the raw reference.
 * 
 * Lower-case letters are converted to uppercase, white-space is removed, illegal characters
 * are removed, the length is truncated if needed.
 * 
 * @param rawReference raw reference
 * @returns ISO 11649 reference
 */
export function createISO11649(rawReference: string): string {
  rawReference = nonAlnumRemoved(rawReference);
  if (rawReference === '')
    rawReference = '0';
  if (rawReference.length > 21)
    rawReference = rawReference.substring(0, 21);

  const modulo = calculateMod97('RF00' + rawReference);
  return 'RF' + ('00' + (98 - modulo)).slice(-2) + rawReference;
}

function calculateMod97(reference: string): number {
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
    }
    if (sum > 9999999) {
      sum = sum % 97;
    }
  }

  sum = sum % 97;
  return sum;
}

/**
 * Create a QR reference by appending the check digit to the given string.
 * 
 * Characters that aren't digits are discarded. If the string has more than
 * 26 digits, excess digits are removed.
 * 
 * @param reference reference without check digit
 * @returns QR reference
 */
export function createQRReference(reference: string): string {
  reference = nonDigitRemoved(reference);
  if (reference.length > 26)
    reference = reference.substring(0, 26);

  const mod10 = calcMod10(reference);
  return reference + String.fromCharCode(48 + mod10);
}

function calcMod10(reference: string): number {
  let carry = 0;
  const len = reference.length;

  for (let i = 0; i < len; i++) {
    const digit = reference.charCodeAt(i) - 48;
    carry = MOD_10[(carry + digit) % 10];
  }
  return (10 - carry) % 10;
}

/**
 * Checks if the specified account number is a QR IBAN or a regular IBAN
 * 
 * @param account IBAN account number
 * @returns `true` if it is a QR IBAN, `false` if it si a regular IBAN
 */
export function isQRIBAN(account: string | undefined): boolean {
  if (account === undefined)
    return false;

  account = whiteSpaceRemovedAndUpperCase(account);
  return /^(CH|LI)\d{2}3[01]/.test(account);
}

/**
 * Formats the specified IBAN by inserting spaces between groups of letters/digits.
 * 
 * If the IBAN contains invalid characters other than whitespace, it is not formatted.
 * Instead, the same value with leading and trailing whitespace trimmed is returned.
 */
export function formatIBAN(iban: string | undefined): string {
  if (iban === undefined)
    return '';

  const cleanedIBAN = whiteSpaceRemovedAndUpperCase(iban);
  if (!/^[A-Z]{2}[A-Z0-9]+$/.test(cleanedIBAN))
    return iban.trim();

  return formatInGroupsOfFour(cleanedIBAN);
}

/**
 * Formats an ISO payment reference by inserting spaces between groups of four letters/digits
 * (start at the left).
 * 
 * If the reference contains invalid characters other than whitespace, it is not formatted.
 * Instead, the same value with leading and trailing whitespace trimmed is returned.
 */
export function formatISOReference(reference: string): string {

  const cleanedReference = whiteSpaceRemovedAndUpperCase(reference);
  if (!/^[A-Z0-9]+$/.test(cleanedReference))
    return reference.trim();

  return formatInGroupsOfFour(cleanedReference);
}

function formatInGroupsOfFour(str: string): string {
  let formatted = '';
  const len = str.length;
  for (let p = 0; p < len; p += 4) {
    formatted += str.substring(p, p + 4);
    if (p + 4 < len)
      formatted += ' ';
  }
  return formatted;
}

/**
 * Formats a QR payment reference by inserting spaces between groups of five digits
 * (start at the right).
 * 
 * 
 * If the reference contains invalid characters other than whitespace, it is not formatted.
 * Instead, the same value with leading and trailing whitespace trimmed is returned.
 */
export function formatQRReference(reference: string): string {

  const cleanedReference = whiteSpaceRemovedAndUpperCase(reference);
  if (!/^\d+$/.test(cleanedReference))
    return reference.trim();

  let formatted = '';
  const len = cleanedReference.length;
  let t = 0;
  while (t < len) {
    const n = t + ((len - t - 1) % 5) + 1;
    if (t !== 0)
      formatted += ' ';
    formatted += cleanedReference.substring(t, n);
    t = n;
  }
  return formatted;
}
