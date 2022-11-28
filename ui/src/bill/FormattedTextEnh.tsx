//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { TextField } from "@mui/material";
import { useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { FieldFormatter, StringFormatter } from "./field-formatter";

/**
 * `FormattedTextEnh` props
 */
interface FormattedTextEnhProps {
  /** Field ID */
  fieldId: string;
  /** Value to edit */
  value: any;
  /** Indicates if the text field requires input */
  required?: boolean;
  /** Indicates if text field is for entering a number (defaults to false) */
  isNumeric?: boolean;
  /** Label key */
  labelKey: string;
  /** Error messages (pairs of field ID and error message) */
  errorMessages?: { [fieldId: string]: string };
  /** Formatter for converting between formatted string and raw value */
  formatter?: FieldFormatter;
  /** Functions to update the value */
  updateField: (fieldId: string, value: any) => void;
};

const defaultFormatter = new StringFormatter();

/**
 * Enhanced ext input field with text formatting and autocompletion.
 * 
 * For editing, a string is used. The stored raw value can be of any type.
 * The provided formatter converts between formatted string value and raw value.
 * If no formatter is provided, leading and trailing white space is removed and
 * the resulting value is stored as a string.
 * 
 * On leaving the field, the value is updated using the `updateField` function.
 * 
 * If `errorMessages` contains a key matching the `fieldId`, the associated value
 * is displayed as an error message below the text field.
 */
const FormattedTextEnh: React.FC<FormattedTextEnhProps> = props => {

  const { fieldId, value, labelKey, errorMessages, updateField } = props;
  const formatter = props.formatter ?? defaultFormatter;
  const isNumeric = props.isNumeric ?? false;
  const required = props.required ?? false;
  const formattedValue = useMemo(() => formatter.formattedValue(value), [value, formatter]);
  const [editValue, setEditValue] = useState(formattedValue);

  const { t } = useTranslation();

  const onBlur = () => {
    const rawValue = formatter.rawValue(editValue);
    const newFormattedValue = formatter.formattedValue(rawValue);
    setEditValue(newFormattedValue);
    if (newFormattedValue !== formattedValue)
      updateField(fieldId, rawValue);
  }

  return (
    <TextField id={fieldId} value={editValue}
      onFocus={() => setEditValue(formattedValue)}
      onChange={(e) => setEditValue(e.target.value)}
      onBlur={onBlur}
      label={t(labelKey)}
      error={errorMessages?.[fieldId] !== undefined} helperText={errorMessages?.[fieldId]}
      title={isNumeric ? t('enter_number') ?? '' : ''}
      fullWidth variant="outlined" size="small"
      required={required}
      inputProps={isNumeric ? { inputMode: 'numeric', pattern: '[0-9.,\'’]*' } : {}} />
  );
}

export default FormattedTextEnh;
