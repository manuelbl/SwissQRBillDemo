//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import { useTranslation } from 'react-i18next';

// Dummy images as a data URL encoded SVG
const qrBillDummyImage = "data:image/svg+xml,%3C%3Fxml version='1.0' encoding='UTF-8'%3F%3E%3Csvg width='794px' height='397px' viewBox='0 0 794 397' version='1.1' xmlns='http://www.w3.org/2000/svg' style='background: %23FFFFFF;'%3E%3Cg stroke='none' fill='none'%3E%3Crect fill='%23DDDDDD' x='19' y='18' width='174' height='179'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='19' y='256' width='101' height='27'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='446' y='18' width='256' height='235'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='18' width='88' height='16'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='63' width='176' height='175'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='256' width='107' height='27'%3E%3C/rect%3E%3Cpath d='M235.5,0 L235.5,397' stroke='%23DDDDDD' stroke-width='2' stroke-linecap='square'%3E%3C/path%3E%3C/g%3E%3C/svg%3E";
const a4PortraitDummyImage = "data:image/svg+xml,%3C%3Fxml version='1.0' encoding='UTF-8'%3F%3E%3Csvg width='794px' height='1123px' viewBox='0 0 794 1123' version='1.1' xmlns='http://www.w3.org/2000/svg' style='background: %23FFFFFF;'%3E%3Cg stroke='none' fill='none'%3E%3Crect fill='%23DDDDDD' x='19' y='744' width='174' height='179'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='19' y='982' width='101' height='27'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='446' y='744' width='256' height='235'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='744' width='88' height='16'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='789' width='176' height='175'%3E%3C/rect%3E%3Crect fill='%23DDDDDD' x='252' y='982' width='107' height='27'%3E%3C/rect%3E%3Cpath d='M235.5,726 L235.5,1123' stroke='%23DDDDDD' stroke-width='2' stroke-linecap='square'%3E%3C/path%3E%3C/g%3E%3C/svg%3E";
const qrCodeDummyImage = "data:image/svg+xml,%3C%3Fxml version='1.0' encoding='UTF-8'%3F%3E%3Csvg width='174px' height='174px' viewBox='0 0 174 174' version='1.1' xmlns='http://www.w3.org/2000/svg' style='background: %23DDDDDD;'%3E%3C/svg%3E";

interface PreviewDialogProps {
  /**
   * Bill ID
   */
  billId: string | undefined,
  /**
   * Indicates if the dialog is open
   */
  isOpen: boolean,
  /**
   * The QR bill output size
   */
  outputSize: string,
  /**
   * Function to call to close the dialog.
   */
  close: () => void,
}

/**
 * Dialog for previewing QR bill.
 * 
 * Until the bill ID is set, dummy images are displayed.
 * 
 * The QR bill output size is used to set the image size
 * while the dialog is opening and the bill ID has not been
 * set yet. It prevents resizing during the opening animation.
 */
const PreviewDialog = ({ billId, isOpen, outputSize, close } :PreviewDialogProps) => {

  const { t } = useTranslation();

  let imageWidth: string;
  let imageHeight: string;
  let imageAltKey: string;
  let dummyImage: string;

  // The values are calculated as: dim_in_mm / 25.4 mm/in * 96 px/in
  if (outputSize === 'qr-bill-only') {
    imageWidth = '793.700';
    imageHeight = '396.850';
    imageAltKey = 'img_qrbill_payment_part';
    dummyImage = qrBillDummyImage;
  } else if (outputSize === 'a4-portrait-sheet') {
    imageWidth = '793.700';
    imageHeight = '1122.519';
    imageAltKey = 'img_qrbill_sheet';
    dummyImage = a4PortraitDummyImage;
  } else {
    imageWidth = '173.858';
    imageHeight = '173.858';
    imageAltKey = 'img_qrcode';
    dummyImage = qrCodeDummyImage;
  }

  const imageUrl = billId !== undefined ? `/qrbill-api/bill/image/${billId}` : dummyImage;

  return (
    <Dialog
      open={isOpen}
      onClose={close}
      maxWidth='lg'
      aria-labelledby='preview-dialog-title'>
      <DialogTitle id="preview-dialog-title">{t('preview_n_download')}</DialogTitle>
      <DialogContent>
        <img src={imageUrl} alt={`${t(imageAltKey)}`} width={imageWidth} height={imageHeight} style={outputSize !== 'qr-code-only' ? { border: '1px solid #666' } : {}} />
      </DialogContent>
      <DialogActions>
        { /* component='a' is needed to prevent the conversion of the link into a router link*/}
        <Button component='a' href={`/qrbill-api/bill/image/${billId}?graphicsFormat=pdf`} disabled={billId === undefined} variant='contained' target='_blank' download='qrbill.pdf' sx={{ marginLeft: '16px' }}>{t('download_pdf')}</Button>
        <Button component='a' href={`/qrbill-api/bill/image/${billId}?graphicsFormat=svg`} disabled={billId === undefined} variant='contained' target='_blank' download='qrbill.svg'>{t('download_svg')}</Button>
        <Button variant='contained' onClick={close} sx={{ marginLeft: '8px', marginRight: '16px' }}>{t('close')}</Button>
      </DialogActions>
    </Dialog>
  );
}

export default PreviewDialog;
