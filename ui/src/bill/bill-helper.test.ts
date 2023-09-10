//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Address } from "../qrbill-api/address";
import { BillFormat } from "../qrbill-api/bill-format";
import { QrBill } from "../qrbill-api/qrbill";
import { expect, test } from 'vitest';
import { cloneBill, ibanFormatter, referenceFormatter, updateBillField } from "./bill-helper";

const sampleAddress: Address = {
  name: 'name',
  street: 'street',
  houseNo: '123',
  countryCode: 'CH',
  postalCode: '1234',
  town: 'Zurich',
};

const sampleFormat: BillFormat = {
  language: 'de',
  outputSize: 'qr-bill-only',
  separatorType: 'dashed-line-with-scissors',
}

const sampleBill: QrBill = {
  version: 'V2_0',
  account: 'CH123123123',
  creditor: sampleAddress,
  currency: 'CHF',
  format: sampleFormat,
};

test('updateBillField() returns modified copy', () => {
  const bill = updateBillField(sampleBill, 'creditor.street', 'road');
  expect(bill).not.toBe(sampleBill);
  expect(bill.creditor?.street ?? '').toEqual('road');
});

test('cloneBill() returns a deep copy', () => {
  const bill = cloneBill(sampleBill);
  expect(bill).toEqual(sampleBill);
  expect(bill).not.toBe(sampleBill);
  expect(bill.creditor).not.toBe(sampleBill.creditor);
  expect(bill.format).not.toBe(sampleBill.format);
});


test('IBANFormatter formats account numbers', () => {
  expect(ibanFormatter.formattedValue('CH6009000000300000375')).toEqual('CH60 0900 0000 3000 0037 5');
  expect(ibanFormatter.formattedValue('  CH6009000000300000375 ')).toEqual('CH60 0900 0000 3000 0037 5');
  expect(ibanFormatter.formattedValue('ch60 0900 0000 3000 0037 5')).toEqual('CH60 0900 0000 3000 0037 5');
});

test('IBANFormatter works with undefined', () => {
  expect(ibanFormatter.formattedValue(undefined)).toEqual('');
});

test('IBANFormatter works with garbage', () => {
  expect(ibanFormatter.formattedValue('  CH')).toEqual('CH');
  expect(ibanFormatter.formattedValue('  ABCD')).toEqual('ABCD');
  expect(ibanFormatter.formattedValue('ABCDEF ')).toEqual('ABCD EF');
  expect(ibanFormatter.formattedValue(' ABCDEFGH ')).toEqual('ABCD EFGH');
});

test('IBANFormatter raw value removes white space', () => {
  expect(ibanFormatter.rawValue('')).toEqual('');
  expect(ibanFormatter.rawValue('   ')).toEqual('');
  expect(ibanFormatter.rawValue('CH234234234')).toEqual('CH234234234');
  expect(ibanFormatter.rawValue('  CH23423  4234 ')).toEqual('CH234234234');
});


test('referenceFormatter formats ISO references', () => {
  expect(referenceFormatter.formattedValue('RF123456ABCDEF')).toEqual('RF12 3456 ABCD EF');
  expect(referenceFormatter.formattedValue(' RF1 23456A BCDEF ')).toEqual('RF12 3456 ABCD EF');
  expect(referenceFormatter.formattedValue(' rf1 23456A bcDEF ')).toEqual('RF12 3456 ABCD EF');
});

test('referenceFormatter formats QR references', () => {
  expect(referenceFormatter.formattedValue('1234567890')).toEqual('12345 67890');
  expect(referenceFormatter.formattedValue(' 123 456  7890 ')).toEqual('12345 67890');
});

test('referenceFormatter works with garbage', () => {
  expect(referenceFormatter.formattedValue(undefined)).toEqual('');
  expect(referenceFormatter.formattedValue('     ')).toEqual('');
  expect(referenceFormatter.formattedValue('  AB CD EF GH   ')).toEqual('AB CD EF GH');
  expect(referenceFormatter.formattedValue('  12 ab --   ')).toEqual('12 ab --');
});

test('referenceFormatter raw value removes white space', () => {
  expect(ibanFormatter.rawValue('')).toEqual('');
  expect(ibanFormatter.rawValue('   ')).toEqual('');
  expect(ibanFormatter.rawValue('  AB CD EF GH ')).toEqual('ABCDEFGH');
  expect(ibanFormatter.rawValue('  RF1 23456A BCDEF ')).toEqual('RF123456ABCDEF');
});
