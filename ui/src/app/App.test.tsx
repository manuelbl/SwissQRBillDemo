//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { screen } from '@testing-library/react';
import { render } from './test-utils';
import { expect, test } from 'vitest';
import App from './App';
import { act } from 'react';


test('nav bar and bill data form is shown', async () => {
  await act(async () => {
    render(<App />);
  })
  
  const link = screen.getByRole('link', { name: /navExamples/i });
  expect(link).toBeInTheDocument();
  const text = screen.getByText(/account_payable_to/i);
  expect(text).toBeInTheDocument();
});
