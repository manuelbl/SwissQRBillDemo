//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { expect, test } from 'vitest';
import { AmountFormatter } from "./amount-formatter";


test('amount formatting de-CH works', () => {
  const formatter = new AmountFormatter('de-CH');
  expect(formatter.formattedValue(undefined)).toBe('');
  expect(formatter.formattedValue(3)).toBe('3.00');
  expect(formatter.formattedValue(3.005)).toBe('3.01');
  expect(formatter.formattedValue(12345.67)).toMatch(/12['’]345\.67/);
});

test('amount unformatting de-CH works', () => {
  const formatter = new AmountFormatter('de-CH');
  expect(formatter.rawValue('')).toBe(undefined);
  expect(formatter.rawValue('  ')).toBe(undefined);
  expect(formatter.rawValue('0')).toBe(0);
  expect(formatter.rawValue('  3  ')).toBe(3);
  expect(formatter.rawValue('5.12')).toBe(5.12);
  expect(formatter.rawValue("12’345.67")).toBe(12345.67);
  expect(formatter.rawValue("12'345.67")).toBe(12345.67);
  expect(formatter.rawValue('567.892')).toBe(567.89);
});

test('amount formatting de-DE works', () => {
  const formatter = new AmountFormatter('de-DE');
  expect(formatter.formattedValue(3)).toBe('3,00');
  expect(formatter.formattedValue(12345.67)).toBe('12.345,67');
});

test('amount unformatting de-DE works', () => {
  const formatter = new AmountFormatter('de-DE');
  expect(formatter.rawValue('5,12')).toBe(5.12);
  expect(formatter.rawValue("12.345,67")).toBe(12345.67);
});

