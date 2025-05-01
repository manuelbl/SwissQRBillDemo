//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import React, { Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter} from 'react-router';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { baseTheme } from './app/theme';
import App from './app/App';
import './app/i18n';
import './main.css';

const root = ReactDOM.createRoot(
  document.getElementById('root')!
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
