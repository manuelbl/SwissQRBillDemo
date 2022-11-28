//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { useState } from 'react';
import { Navigate, useRoutes } from 'react-router-dom';
import { Box, Toolbar } from '@mui/material';
import { ExampleList } from '../bill/example-list';
import { QrBill } from '../qrbill-api/qrbill';
import { cloneBill, updateBillField } from '../bill/bill-helper';
import About from './About';
import BillData from '../bill/BillData';
import Examples from '../bill/Examples';
import NavBar from './NavBar';
import Settings from './Settings';
import './App.css';

/**
 * App component with navigation bar and router 
 */
const App: React.FC<{}> = _props => {

  // QR bill data being edited
  const [bill, setBill] = useState(ExampleList[0].bill);

  /**
   * Updates a single field in the state.
   * @param path field path, e.g. `format.outputSize`
   * @param value new value
   */
  function updateField(path: string, value: any) {
    setBill(bill => updateBillField(bill, path, value));
  }

  /**
   * Selects a new bill for editing
   * @param bill selected bill
   */
  function selectBill(bill: QrBill) {
    setBill(_ => cloneBill(bill));
  }

  let routes = useRoutes([
    {
      path: "/",
      element: <BillData bill={bill} updateField={updateField} />,
    },
    {
      path: "/examples",
      element: <Examples selectBill={selectBill} />,
    },
    {
      path: "/about",
      element: <About />,
    },
    {
      path: "/settings",
      element: <Settings />,
    },
    {
      path: "/*",
      element: <Navigate to="/" replace />
    }
  ]);

  return (
    <Box sx={{ display: 'flex', width: '100%' }}>
      <NavBar />
      <Box component="main" sx={{ p: 3 }}>
        <Toolbar />
        {routes}
      </Box>
    </Box>
  );
}

export default App;
