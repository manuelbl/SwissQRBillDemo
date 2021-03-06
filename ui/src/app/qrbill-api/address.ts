//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
export class Address {
  type?: string;
  name?: string;
  addressLine1?: string;
  addressLine2?: string;
  street?: string;
  houseNo?: string;
  postalCode?: string;
  town?: string;
  countryCode?: string;

  static clone(address: Address | undefined): Address | undefined {
    if (!address) {
      return undefined;
    }

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
}
