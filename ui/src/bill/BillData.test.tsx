//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { useState } from 'react';
import { fireEvent, screen } from '@testing-library/react';
import { render } from '../app/test-utils';
import { Address } from '../qrbill-api/address';
import { BillFormat } from '../qrbill-api/bill-format';
import { QrBill } from '../qrbill-api/qrbill';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { beforeEach, expect, test, vi } from 'vitest';
import { BillValue, ibanFormatter, updateBillField } from './bill-helper';
import BillData from './BillData';

vi.mock('../qrbill-api/qrbill-api');

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
  reference: '',
  characterSet: 'extended-latin',
};

const mockValidationResponse: ValidationResponse = {
  valid: true,
  validatedBill: sampleBill,
  billID: 'abcdefg',
};

const simpleMocks = {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    updateField: (_path: string, _value: BillValue) => {
    // nothing to do in this test
  }
}

// Account the external-reset test switches to (distinct from `sampleBill.account`).
const externalResetAccount = 'CH4431999123000889012';

/**
 * Renders `BillData` inside a stateful parent that mirrors `App`'s controlled behaviour:
 * every `updateField` produces a new bill via `updateBillField`, which flows straight back
 * through the `bill` prop. The extra button lets a test mutate the bill from outside the
 * form (a reset / programmatic fill) while a field stays unfocused.
 */
function ControlledBillData({ initialBill }: { initialBill: QrBill }) {
  const [bill, setBill] = useState(initialBill);
  const updateField = (path: string, value: BillValue) => {
    simpleMocks.updateField(path, value);
    setBill(prev => updateBillField(prev, path, value));
  };
  return (
    <>
      <button onClick={() => setBill(prev => updateBillField(prev, 'account', externalResetAccount))}>
        external reset
      </button>
      <BillData bill={bill} updateField={updateField} />
    </>
  );
}

beforeEach(async () => {
  vi.spyOn(simpleMocks, 'updateField');
  // Auto-mocked module: give every test a resolved validation response so the order in
  // which tests run cannot affect the mock (previously only test 1 set this up).
  const api = await import('../qrbill-api/qrbill-api');
  api.validateBill = vi.fn().mockResolvedValue(mockValidationResponse);
});


test('bill data form is shown', async () => {
  const api = await import('../qrbill-api/qrbill-api');
  api.validateBill = vi.fn().mockResolvedValue(mockValidationResponse);
  
  render(<BillData bill={sampleBill} updateField={simpleMocks.updateField} />);

  const text = await screen.findByText(/account_payable_to/i);
  expect(text).toBeInTheDocument();

  const accountField = screen.getByLabelText(/^account/i);
  expect(accountField).toHaveAttribute('value', 'CH12 3123 123');
  expect(simpleMocks.updateField).not.toHaveBeenCalled();
  expect(api.validateBill).toBeCalledTimes(1);
});


test('editing a field commits the raw value and shows the reformatted value', async () => {
  render(<ControlledBillData initialBill={sampleBill} />);
  const accountField = await screen.findByLabelText(/^account/i);

  fireEvent.change(accountField, { target: { value: 'CH45679876'} });
  fireEvent.blur(accountField);

  // The committed (raw) value round-trips through the parent and comes back formatted.
  // Awaiting the reformatted display value also lets the re-render's server validation
  // settle inside act(), so its state updates don't leak past the end of the test.
  expect(simpleMocks.updateField).toHaveBeenCalledWith('account', 'CH45679876');
  expect(await screen.findByDisplayValue('CH45 6798 76')).toBe(accountField);
});

test('reference is formatted', async () => {
  render(<ControlledBillData initialBill={sampleBill} />);

  const referenceField = await screen.findByLabelText(/^reference/i);

  fireEvent.change(referenceField, { target: { value: 'RF47ABC123'} });
  fireEvent.blur(referenceField);

  expect(simpleMocks.updateField).toHaveBeenCalledWith('reference', 'RF47ABC123');
  // Awaiting the reformatted display value lets the re-render's server validation settle
  // inside act(), so its state updates don't leak past the end of the test.
  expect(await screen.findByDisplayValue('RF47 ABC1 23')).toBe(referenceField);
});

// A field that is mounted but unfocused must reflect an external change to
// its `value` prop (reset / cross-field derivation / programmatic fill).
test('field re-syncs when the bill changes externally while unfocused', async () => {
  render(<ControlledBillData initialBill={sampleBill} />);
  const accountField = await screen.findByLabelText(/^account/i);
  expect(accountField).toHaveAttribute('value', 'CH12 3123 123');

  // Change the bill from outside the field, without ever focusing it.
  fireEvent.click(screen.getByRole('button', { name: /external reset/i }));

  const expected = ibanFormatter.formattedValue(externalResetAccount);
  expect(await screen.findByDisplayValue(expected)).toBe(accountField);
});
