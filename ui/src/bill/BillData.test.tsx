//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { fireEvent, screen } from '@testing-library/react';
import BillData from './BillData';
import { render } from '../app/test-utils';
import { Address } from '../qrbill-api/address';
import { BillFormat } from '../qrbill-api/bill-format';
import { QrBill } from '../qrbill-api/qrbill';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { validateBill } from '../qrbill-api/qrbill-api';

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
};

const mockValidationResponse: ValidationResponse = {
  valid: true,
  validatedBill: sampleBill,
  billID: 'abcdefg',
};

jest.mock('../qrbill-api/qrbill-api');


beforeEach(() => {
  (validateBill as jest.Mock).mockResolvedValue(mockValidationResponse);
});


test('bill data form is shown', async () => {
  const updateField = jest.fn();
  
  render(<BillData bill={sampleBill} updateField={updateField} />);

  const text = await screen.findByText(/account_payable_to/i);
  expect(text).toBeInTheDocument();

  const accountField = screen.getByLabelText(/^account/i);
  expect(accountField).toHaveAttribute('value', 'CH12 3123 123');
  expect(updateField).not.toHaveBeenCalled();
  expect(validateBill).toBeCalledTimes(1);
});


test('updateField is called', async () => {
  const updateField = jest.fn();

  render(<BillData bill={sampleBill} updateField={updateField} />);
  const accountField = await screen.findByLabelText(/^account/i);

  fireEvent.change(accountField, { target: { value: 'CH45679876'} });
  fireEvent.blur(accountField);
  await new Promise(process.nextTick);

  expect(accountField).toHaveAttribute('value', 'CH45 6798 76');
  expect(updateField).toHaveBeenCalled();
});

test('reference is formatted', async () => {
  const updateField = jest.fn();
  render(<BillData bill={sampleBill} updateField={updateField} />);
  
  const referenceField = await screen.findByLabelText(/^reference/i);

  fireEvent.change(referenceField, { target: { value: 'RF47ABC123'} });
  fireEvent.blur(referenceField);

  expect(referenceField).toHaveAttribute('value', 'RF47 ABC1 23');
  expect(updateField).toHaveBeenCalled();
});
