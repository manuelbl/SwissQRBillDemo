//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { StringFormatter } from "./field-formatter";

const stringFormatter = new StringFormatter();

test('StringFormatter formatting works', () => {
  expect(stringFormatter.formattedValue('abc')).toEqual('abc');
  expect(stringFormatter.formattedValue('abc  def')).toEqual('abc  def');
  expect(stringFormatter.formattedValue(' abc ')).toEqual('abc');
  expect(stringFormatter.formattedValue(undefined)).toEqual('');
});

test('StringFormatter rawValue() works', () => {
  expect(stringFormatter.rawValue('abc')).toEqual('abc');
  expect(stringFormatter.rawValue('abc  def')).toEqual('abc  def');
  expect(stringFormatter.rawValue(' abc ')).toEqual('abc');
});
