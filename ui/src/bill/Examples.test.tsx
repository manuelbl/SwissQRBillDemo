//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ExampleList } from './example-list';
import { QrBill } from '../qrbill-api/qrbill';
import { render } from '../app/test-utils';
import Examples from './Examples';


test('example can be selected', async () => {

  const selectBill = jest.fn((_bill: QrBill) => { });
  const user = userEvent.setup();

  render(<Examples selectBill={selectBill} />);

  const buttons = screen.getAllByRole('button', { name: /select/i });
  expect(buttons.length).toBeGreaterThan(3);

  await user.click(buttons[3]);

  expect((selectBill.mock.calls[0][0] as QrBill).account).toEqual(ExampleList[3].bill.account)
  expect(window.location.pathname).toBe('/');
});
