//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { QrBill } from "./qrbill";
import { ValidationResponse } from "./validation-response";

export async function validateBill(bill: QrBill, language: string): Promise<ValidationResponse> {

    const response = await window.fetch('/qrbill-api/bill/validated', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': language,            
        },
        body: JSON.stringify(bill)
    });

    if (response.status !== 200)
        throw new Error(`${response.status}: ${response.statusText}`);

    const validationResponse: ValidationResponse = await response.json() as ValidationResponse;
    return validationResponse;
}
