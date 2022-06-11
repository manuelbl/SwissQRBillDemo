/*
 * Swiss QR Bill Generator
 * Copyright (c) 2018 Manuel Bleichenbacher
 * Licensed under MIT License
 * https://opensource.org/licenses/MIT
 */

/**
 * Formats the value displayed in a `InputWithFormatDirective` input field.
 */
export interface InputFormatter {
  /** Remove the formatting and return the raw value */
  rawValue(formattedValue: string): any | undefined;

  /** Apply the formatting */
  formattedValue(rawValue: any | undefined): string;

  /** Provide the value while input field is in focus (if different than formatted value) */
  editValue?(rawValue: any | undefined): string;
}
