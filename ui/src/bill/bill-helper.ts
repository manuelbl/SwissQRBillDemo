/*
 * Swiss QR Bill Generator
 * Copyright (c) 2022 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

import { FieldFormatter } from './field-formatter';
import { formatIBAN, formatISOReference, formatQRReference, whiteSpaceRemoved } from './payments';
import { Address } from "../qrbill-api/address";
import { BillFormat } from "../qrbill-api/bill-format";
import { QrBill } from "../qrbill-api/qrbill";
import _ from 'lodash';

export class PaymentValidationError extends Error {
}

// Helper functions for modying bills

/**
 * Returns a new bill instance with the field at `path` changed to `value`
 * @param bill the bill to copy
 * @param path the path to the modified field
 * @param value the new value for the field
 * @returns new modified bill instance
 */
export function updateBillField(bill: QrBill, path: string, value: any): QrBill {
  bill = cloneBill(bill);
  _.set(bill, path, value);
  return bill;
}

export function cloneAddress(address: Address | undefined): Address | undefined {
  if (!address)
    return undefined;

  return {
    type: address.type,
    name: address.name,
    addressLine1: address.addressLine1,
    addressLine2: address.addressLine2,
    street: address.street,
    houseNo: address.houseNo,
    postalCode: address.postalCode,
    town: address.town,
    countryCode: address.countryCode
  };
}

export function cloneFormat(format: BillFormat | undefined): BillFormat | undefined {
  if (!format)
    return undefined;

  return {
    language: format.language,
    graphicsFormat: format.graphicsFormat,
    outputSize: format.outputSize,
    separatorType: format.separatorType,
    fontFamily: format.fontFamily
  };
}

export function cloneBill(bill: QrBill): QrBill {
  return {
    version: bill.version,
    amount: bill.amount,
    currency: bill.currency,
    account: bill.account,
    creditor: cloneAddress(bill.creditor),
    reference: bill.reference,
    unstructuredMessage: bill.unstructuredMessage,
    billInformation: bill.billInformation,
    debtor: cloneAddress(bill.debtor),
    format: cloneFormat(bill.format)
  };
}

class IBANFormatter implements FieldFormatter {
  formattedValue(rawValue: any): string {
    return formatIBAN(rawValue);
  }

  rawValue(formattedValue: string): any {
    return whiteSpaceRemoved(formattedValue);
  }
}

export const ibanFormatter = new IBANFormatter();


class ReferenceFormatter implements FieldFormatter {
  formattedValue(rawValue: any): string {

    rawValue = whiteSpaceRemoved(rawValue.toString().toUpperCase());

    return rawValue.startsWith('RF') ? formatISOReference(rawValue) : formatQRReference(rawValue);
  }

  rawValue(formattedValue: string): any {
    return whiteSpaceRemoved(formattedValue.toUpperCase());
  }
}

export const referenceFormatter = new ReferenceFormatter();
