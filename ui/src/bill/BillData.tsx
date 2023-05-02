//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { useCallback, useEffect, useMemo, useState } from 'react';
import { Alert, Autocomplete, Box, Button, Card, CardContent, FormControl, FormGroup, InputLabel, MenuItem, Select, TextField, Typography } from '@mui/material';
import Grid from '@mui/material/Unstable_Grid2';
import { useTranslation } from 'react-i18next';
import { QrBill } from '../qrbill-api/qrbill';
import { validateBill } from '../qrbill-api/qrbill-api';
import { ValidationResponse } from '../qrbill-api/validation-response';
import { createISO11649, createQRReference, isQRIBAN, whiteSpaceRemoved } from './payments';
import { ibanFormatter, referenceFormatter } from './bill-helper';
import { AmountFormatter } from './amount-formatter';
import PreviewDialog from './PreviewDialog';
import FormattedTextEnh from './FormattedTextEnh';

/**
 * BillData props
 */
type BillDataProps = {
  /**
   * QR bill data to edit
   */
  bill: QrBill;

  /**
   * Function to update the bill data.
   */
  updateField: (path: string, value: any) => void;
};

/**
 * Form to edit QR bill data.
 */
const BillData: React.FC<BillDataProps> = props => {

  const { bill, updateField } = props;

  // Validation error messages currently display in the UI (pairs of field ID / message)
  const [errorMessages, setErrorMessages] = useState({} as { [fieldId: string]: string });

  // Server communication error message
  const [serverError, setServerError] = useState(undefined as string | undefined);

  const { t, i18n } = useTranslation();

  // Amount formatter (dependent on currently selected language)
  const amountFormatter = useMemo(() => new AmountFormatter(i18n.language + '-CH'), [i18n.language]);

  // Asynchronously validate bill on server-side
  const validateData = useCallback((bill: QrBill) => {
    validateBill(bill, i18n.language)
      .then((response) => { extractErrorMessages(response); setServerError(undefined); })
      .catch((reason) => setServerError(reason.message));
  }, [i18n.language]);

  // Whenever the bill changes, validate it (and on initial display)
  useEffect(() => {
    validateData(bill);
  }, [bill, validateData]);

  /**
   * Update a single field in the bill data
   * @param path path within `bill` (= field ID)
   * @param newValue new value
   */
  const updateBillField = (path: string, newValue: any) => {
    updateField(path, newValue);
  }

  /**
   * Extract the error messages from the validation response
   * @param validationResponse  validation response
   */
  const extractErrorMessages = (validationResponse: ValidationResponse) => {
    const messages: Record<string, string> = {};
    if (validationResponse.validationMessages) {
      validationResponse.validationMessages
        .filter(e => e.type === 'Error')
        .forEach(e => messages[e.field ?? 'x'] = e.message ?? 'x');
    }
    setErrorMessages(messages);
  }


  // State to open and close preview
  const [isPreviewOpen, setPreviewOpen] = useState(false);

  // bill ID required for preview
  const [billId, setBillId] = useState<string | undefined>();

  // Opens the preview dialog
  const openPreview = () => {
    setPreviewOpen(true);
    // validate the bill to get the bill ID (required for constructing the image URLs)
    validateBill(bill, i18n.language)
      .then((response) => { setBillId(response.billID); setServerError(undefined); })
      .catch((reason) => setServerError(reason.message));
  }

  // Closes the preview dialog
  const closePreview = () => {
    setPreviewOpen(false);
    setBillId(undefined);
  }

  return (
    <div>

      {serverError !== undefined ?
        <Alert severity="warning" sx={{ marginBottom: '1rem' }}>{t('connection_error', { err: serverError })}</Alert>
        : null
      }

      <form onSubmit={(event) => { event.preventDefault(); openPreview(); }}>
        <FormGroup>
          <Grid container columnSpacing={2}>
            <Grid md={6} xs={12}>
              <Card>
                <CardContent>
                  <Grid container rowSpacing={2} columnSpacing={1}>
                    <Grid>
                      <Typography gutterBottom variant="h6" component="div">{t('account_payable_to')}</Typography>
                    </Grid>
                    <Grid xs={12}>
                      <FormattedTextEnh fieldId='account' value={bill.account} labelKey='account' errorMessages={errorMessages} updateField={updateBillField} formatter={ibanFormatter} required />
                    </Grid>
                    <Grid xs={12}>
                      <FormattedTextEnh fieldId='creditor.name' value={bill.creditor?.name} labelKey='name' errorMessages={errorMessages} updateField={updateBillField} required />
                    </Grid>
                    <Grid xs={9}>
                      <FormattedTextEnh fieldId='creditor.street' value={bill.creditor?.street} labelKey='street' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={3}>
                      <FormattedTextEnh fieldId='creditor.houseNo' value={bill.creditor?.houseNo} labelKey='house_number' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={2}>
                      <FormattedTextEnh fieldId='creditor.countryCode' value={bill.creditor?.countryCode} labelKey='country' errorMessages={errorMessages} updateField={updateBillField} required />
                    </Grid>
                    <Grid xs={3}>
                      <FormattedTextEnh fieldId='creditor.postalCode' value={bill.creditor?.postalCode} labelKey='postal_code' errorMessages={errorMessages} updateField={updateBillField} required />
                    </Grid>
                    <Grid xs={7}>
                      <FormattedTextEnh fieldId='creditor.town' value={bill.creditor?.town} labelKey='town' errorMessages={errorMessages} updateField={updateBillField} required />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
            <Grid md={6} xs={12}>
              <Card>
                <CardContent>
                  <Grid container rowSpacing={2} columnSpacing={1}>
                    <Grid xs={12}>
                      <Typography gutterBottom variant="h6" component="div">{t('payment_details')}</Typography>
                    </Grid>
                    <Grid xs={3}>
                      <SelectEx fieldId='currency' value={bill.currency ?? 'CHF'} labelKey='currency' itemKeys={['CHF', 'EUR']} itemsLabelKey='currencies' updateField={updateBillField} />
                    </Grid>
                    <Grid xs={4}>
                      <FormattedTextEnh fieldId='amount' value={bill.amount} isNumeric={true} labelKey='amount' errorMessages={errorMessages} updateField={updateBillField} formatter={amountFormatter} />
                    </Grid>
                    <Grid xs={12}>
                      <ReferenceAutoComplete value={bill.reference} account={bill.account} errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={12}>
                      <FormattedTextEnh fieldId='unstructuredMessage' value={bill.unstructuredMessage} labelKey='unstructured_msg' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={12}>
                      <FormattedTextEnh fieldId='billInformation' value={bill.billInformation} labelKey='bill_information' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
            <Grid md={6} xs={12}>
              <Card>
                <CardContent>
                  <Grid container rowSpacing={2} columnSpacing={1}>
                    <Grid>
                      <Typography gutterBottom variant="h6" component="div">{t('payable_by')}</Typography>
                    </Grid>
                    <Grid xs={12}>
                      <FormattedTextEnh fieldId='debtor.name' value={bill.debtor?.name} labelKey='name' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={9}>
                      <FormattedTextEnh fieldId='debtor.street' value={bill.debtor?.street} labelKey='street' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={3}>
                      <FormattedTextEnh fieldId='debtor.houseNo' value={bill.debtor?.houseNo} labelKey='house_number' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={2}>
                      <FormattedTextEnh fieldId='debtor.countryCode' value={bill.debtor?.countryCode} labelKey='country' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={3}>
                      <FormattedTextEnh fieldId='debtor.postalCode' value={bill.debtor?.postalCode} labelKey='postal_code' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                    <Grid xs={7}>
                      <FormattedTextEnh fieldId='debtor.town' value={bill.debtor?.town} labelKey='town' errorMessages={errorMessages} updateField={updateBillField} />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
            <Grid md={6} xs={12}>
              <Card>
                <CardContent>
                  <Grid container rowSpacing={2} columnSpacing={1}>
                    <Grid xs={12}>
                      <Typography gutterBottom variant="h6" component="div">{t('bill_format')}</Typography>
                    </Grid>
                    <Grid xs={4}>
                      <SelectEx fieldId='format.language' value={bill.format?.language ?? 'de'} labelKey='language'
                        itemKeys={['de', 'fr', 'it', 'rm', 'en']} itemsLabelKey='languages' updateField={updateBillField} />
                    </Grid>
                    <Grid xs={8}>
                    </Grid>
                    <Grid xs={5}>
                      <SelectEx fieldId='format.outputSize' value={bill.format?.outputSize ?? 'qr-bill-only'} labelKey='output_size'
                        itemKeys={['qr-bill-only', 'a4-portrait-sheet', 'qr-code-only']} itemsLabelKey='output_sizes' updateField={updateBillField} />
                    </Grid>
                    <Grid xs={7}>
                      <SelectEx fieldId='format.separatorType' value={bill.format?.separatorType ?? 'dashed-line-with-scissors'} labelKey='separator_type'
                        itemKeys={['dashed-line-with-scissors', 'dashed-line', 'dotted-line-with-scissors', 'dotted-line', 'solid-line-with-scissors', 'solid-line', 'none']}
                        itemsLabelKey='separator_types' updateField={updateBillField} />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </FormGroup>

        <Box sx={{ position: 'sticky', bottom: '1rem', float: 'right', width: 'auto' }}>
          <Button type='submit' variant='contained' disabled={serverError !== undefined || Object.keys(errorMessages).length > 0}>{t('preview_n_download')}</Button>
        </Box>
      </form>

      <PreviewDialog isOpen={isPreviewOpen} close={closePreview} billId={billId} outputSize={bill.format?.outputSize ?? ''} />
    </div>
  );
}

type SelectExProps = {
  fieldId: string;
  value: string;
  labelKey: string;
  itemKeys: string[];
  itemsLabelKey: string;
  updateField: (path: string, value: any) => void;
};

/**
 * Select (list of options) with translated label and items
 */
const SelectEx: React.FC<SelectExProps> = props => {

  const { fieldId, value, labelKey, itemKeys, itemsLabelKey, updateField } = props;

  const { t } = useTranslation();

  return (
    <FormControl fullWidth size='small'>
      <InputLabel id={`${fieldId}-label`}>{t(labelKey)}</InputLabel>
      <Select id={fieldId} value={value}
        onChange={(e) => updateField(fieldId, e.target.value)}
        labelId={`${fieldId}-label`} label={t(labelKey)}>
        {
          itemKeys.map(item => <MenuItem key={item} value={item}>{t(`${itemsLabelKey}.${item}`)}</MenuItem>)
        }
      </Select>
    </FormControl>
  );
}

type ReferenceAutoCompleteProps = {
  /** Value to edit */
  value: any;
  /** Account number */
  account?: string;
  /** Error messages (pairs of field ID and error message) */
  errorMessages?: { [fieldId: string]: string };
  /** Functions to update the value */
  updateField: (fieldId: string, value: any) => void;
}

/**
 * Text field for reference number supporting auto completion and formatting.
 * 
 * Depending if a QR IBAN or a regular IBAN has been entered, the formatting and
 * autocompletion work differently.
 */
const ReferenceAutoComplete: React.FC<ReferenceAutoCompleteProps> = props => {

  const { value, account, errorMessages, updateField } = props;

  const formattedValue = useMemo(() => referenceFormatter.formattedValue(value), [value]);
  const [editValue, setEditValue] = useState(formattedValue);
  const [options, setOptions] = useState<readonly string[]>([]);

  const { t } = useTranslation();

  /**
   * Update the displayed options.
   * 
   * @param value current value in text field
   */
  const updateOptions = (value: string) => {
    const suggestions: string[] = [];
    let str = whiteSpaceRemoved(value.toUpperCase());

    // add check digit at the end
    if (isQRIBAN(account)) {
      if (str.length > 0 && str.length <= 26 && str.replace(/\D/g, '') === str) {
        suggestions.push(createQRReference(str));
      }

    } else {
      // replace check digits after RF
      if (str.startsWith('RF') && str.length > 4) {
        suggestions.push(createISO11649(str.substring(4)));
      }
      // prepend RFxx to value
      if (str.length > 0 && str.length <= 21) {
        suggestions.push(createISO11649(str));
      }
    }

    setOptions(suggestions);
  }

  const onBlur = () => {
    const rawValue = referenceFormatter.rawValue(editValue);
    const newFormattedValue = referenceFormatter.formattedValue(rawValue);
    setEditValue(newFormattedValue);
    if (newFormattedValue !== formattedValue)
      updateField('reference', rawValue);
  }

  return (
    <Autocomplete freeSolo disableClearable
      inputValue={editValue}
      options={options}
      onFocus={() => setEditValue(formattedValue)}
      onInputChange={(_, value) => { setEditValue(value); updateOptions(value); }}
      onBlur={onBlur}

      renderInput={params => (
        <TextField
          {...params}
          id="reference"
          error={errorMessages?.reference !== undefined} helperText={errorMessages?.reference}
          label={t(isQRIBAN(account) ? 'reference_qr' : 'reference_iso')}
          fullWidth variant="outlined" size="small" />
      )}

      renderOption={(props, option) => {
        if (option.startsWith('RF')) {
          return (
            <li {...props}>
              <span className="light">{option.substring(0, 4)}</span>
              <span>{option.substring(4)}</span>
            </li>
          )
        } else {
          return (
            <li {...props}>
              <span>{option.substring(0, option.length - 1)}</span>
              <span className="light">{option.substring(option.length - 1)}</span>
            </li>
          )
        }
      }}

      filterOptions={(x) => x}
    />
  );

}

export default BillData;
