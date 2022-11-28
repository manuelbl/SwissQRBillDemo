//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import React, { Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { baseTheme } from './app/theme';
import App from './app/App';
import './app/i18n';
import './index.css';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

// Base app setup:
// - strict mode
// - base theme replacing MUI links with React router links
// - router
// - suspense (for loading the translations)
//
root.render(
  <React.StrictMode>
    <CssBaseline>
      <BrowserRouter basename='/qrbill'>
        <ThemeProvider theme={baseTheme}>
          <Suspense fallback='loading'>
            <App />
          </Suspense>
        </ThemeProvider>
      </BrowserRouter>
    </CssBaseline>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
//reportWebVitals();
