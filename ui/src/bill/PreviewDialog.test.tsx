//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { screen } from '@testing-library/react';
import { render } from '../app/test-utils';
import { expect, test, vi } from 'vitest';
import PreviewDialog from './PreviewDialog';


test('preview dialog is shown', () => {
  const close = vi.fn();
  const {rerender} = render(<PreviewDialog billId={undefined} outputSize='qr-bill-only' isOpen={false} close={close} />);

  rerender(<PreviewDialog billId={undefined} outputSize='qr-bill-only' isOpen={true} close={close} />);
  const image = screen.getByRole('img');
  expect(image).toBeInTheDocument();
});

test('download button becomes active', () => {
  const close = vi.fn();
  const {rerender} = render(<PreviewDialog billId={undefined} outputSize='qr-bill-only' isOpen={true} close={close} />);
  
  const downloadPdfButton = screen.getByRole('link', {name: 'download_pdf'});
  expect(downloadPdfButton).toBeInTheDocument();
  expect(downloadPdfButton).toHaveAttribute('aria-disabled', 'true');

  rerender(<PreviewDialog billId={'abc'} outputSize='qr-bill-only' isOpen={false} close={close} />);
  expect(downloadPdfButton).not.toHaveAttribute('aria-disabled');
});
