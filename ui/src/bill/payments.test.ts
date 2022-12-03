//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { createISO11649, createQRReference, formatIBAN, formatISOReference, formatQRReference, isQRIBAN, nonAlnumRemoved, nonDigitRemoved, whiteSpaceRemoved, whiteSpaceRemovedAndUpperCase } from "./payments";

test('whitespaceRemoved() works correctly', () => {
  expect(whiteSpaceRemoved('as34-')).toEqual('as34-');
  expect(whiteSpaceRemoved(' as 34- ')).toEqual('as34-');
  expect(whiteSpaceRemoved('as\t34-')).toEqual('as34-');
  expect(whiteSpaceRemoved('  AS  \t  34 - ')).toEqual('AS34-');
  expect(whiteSpaceRemoved('')).toEqual('');
  expect(whiteSpaceRemoved(undefined)).toEqual('');
});

test('whiteSpaceRemovedAndUpperCase() works correctly', () => {
  expect(whiteSpaceRemovedAndUpperCase('as34-')).toEqual('AS34-');
  expect(whiteSpaceRemovedAndUpperCase(' as 34- ')).toEqual('AS34-');
  expect(whiteSpaceRemovedAndUpperCase('as\t34-')).toEqual('AS34-');
  expect(whiteSpaceRemovedAndUpperCase('  ASas  \t  34 - ')).toEqual('ASAS34-');
  expect(whiteSpaceRemovedAndUpperCase('')).toEqual('');
  expect(whiteSpaceRemovedAndUpperCase(undefined)).toEqual('');
});

test('nonAlnumRemoved() works correctly', () => {
  expect(nonAlnumRemoved('ABC')).toEqual('ABC');
  expect(nonAlnumRemoved('  A-BC ')).toEqual('ABC');
  expect(nonAlnumRemoved(' A B C ')).toEqual('ABC');
  expect(nonAlnumRemoved(' AéäB C ')).toEqual('ABC');
  expect(nonAlnumRemoved('abc')).toEqual('ABC');
  expect(nonAlnumRemoved('')).toEqual('');
  expect(nonAlnumRemoved(undefined)).toEqual('');
});

test('nonDigitRemoved() works correctly', () => {
  expect(nonDigitRemoved('1234')).toEqual('1234');
  expect(nonDigitRemoved('  1-23BC4 ')).toEqual('1234');
  expect(nonDigitRemoved(' 1 2 34 ')).toEqual('1234');
  expect(nonDigitRemoved('')).toEqual('');
  expect(nonDigitRemoved(undefined)).toEqual('');
});

test('createISO11649() creates valid reference', () => {
  expect(createISO11649('ABCD1234')).toEqual('RF39ABCD1234');
  expect(createISO11649('ABCD1234')).toEqual('RF39ABCD1234');
  expect(createISO11649('12345678901234567904')).toEqual('RF3012345678901234567904');
  expect(createISO11649('1355')).toEqual('RF851355');
});

test('createISO11649() deals with whitespace and illegal characters', () => {
  expect(createISO11649('  abCD1 234 ')).toEqual('RF39ABCD1234');
  expect(createISO11649(' 123-4567/8901..234 567904 ')).toEqual('RF3012345678901234567904');
});

test('createISO11649() deals with too short and too long references', () => {
  expect(createISO11649('')).toEqual('RF040');
  expect(createISO11649('0')).toEqual('RF040');
  expect(createISO11649('123456789012345678901')).toEqual('RF40123456789012345678901');
  expect(createISO11649('1234567890123456789012')).toEqual('RF40123456789012345678901');
});


test('createQRReference creates correct reference', () => {
  expect(createQRReference('12345')).toEqual('123457');
  expect(createQRReference('21000000000313947143000901')).toEqual('210000000003139471430009017');
});


test('createQRReference deals with additional characters', () => {
  expect(createQRReference(' 12 345 ')).toEqual('123457');
  expect(createQRReference(' 12 3b4-5 ')).toEqual('123457');
  expect(createQRReference('21000000000313947143000901999')).toEqual('210000000003139471430009017');
});


test('isQRIBAN() detects correctly', () => {
  expect(isQRIBAN('CH1604835164487011000')).toBeFalsy();
  expect(isQRIBAN('CH31 0483 5146 2769 0100 0')).toBeFalsy();
  expect(isQRIBAN('DE12500105170648489890')).toBeFalsy();
  expect(isQRIBAN('RF18 0000 3129 2830 4823 402')).toBeFalsy();
  expect(isQRIBAN('CH6431961000004421557')).toBeTruthy();
  expect(isQRIBAN('CH44 3199 9123 0008 8901 2')).toBeTruthy();
});


test('formatIBAN() inserts correct spaces', () => {
  expect(formatIBAN('CH1604835164487011000')).toEqual('CH16 0483 5164 4870 1100 0');
  expect(formatIBAN('DE12500105170648489890')).toEqual('DE12 5001 0517 0648 4898 90');
  expect(formatIBAN('  CH 6509 000 00 090 000755 9  ')).toEqual('CH65 0900 0000 9000 0755 9');
});

test('formatIBAN() deals with garbage', () => {
  expect(formatIBAN(undefined)).toEqual('');
  expect(formatIBAN('')).toEqual('');
  expect(formatIBAN('  ')).toEqual('');
  expect(formatIBAN(' abc.def ')).toEqual('abc.def');
  expect(formatIBAN(' ab c.d ef ')).toEqual('ab c.d ef');
});


test('formatISOReference() inserts correct spaces', () => {
  expect(formatISOReference('RF02ABCDE1234')).toEqual('RF02 ABCD E123 4');
  expect(formatISOReference('RF74ABCDEFGH')).toEqual('RF74 ABCD EFGH');
  expect(formatISOReference('RF2012345678')).toEqual('RF20 1234 5678');
  expect(formatISOReference('  RF0 2AB  CDE12   34  ')).toEqual('RF02 ABCD E123 4');
});

test('formatISOReference() deals with garbage', () => {
  expect(formatISOReference('')).toEqual('');
  expect(formatISOReference('  ')).toEqual('');
  expect(formatISOReference(' abc.def ')).toEqual('abc.def');
  expect(formatISOReference(' ab c.d ef ')).toEqual('ab c.d ef');
});


test('formatQRReference() inserts correct spaces', () => {
  expect(formatQRReference('210000000003132342352344')).toEqual('2100 00000 00313 23423 52344');
  expect(formatQRReference('2100000000031323423523440')).toEqual('21000 00000 03132 34235 23440');
  expect(formatQRReference('  2100 00000  00313 2342 3523440  ')).toEqual('21000 00000 03132 34235 23440');
});

test('formatQRReference() deals with garbage', () => {
  expect(formatQRReference('')).toEqual('');
  expect(formatQRReference('  ')).toEqual('');
  expect(formatQRReference('12345678A')).toEqual('12345678A');
  expect(formatQRReference(' abc.def ')).toEqual('abc.def');
  expect(formatQRReference(' ab c.d ef ')).toEqual('ab c.d ef');
});
