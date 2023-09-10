//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

afterEach(() => {
  cleanup();
});

i18n
    .use(initReactI18next)
    .init({
        fallbackLng: 'en',
        resources: {
          'en': {
            
          }
        }
    })
    .then(() => {
      // nothing to do (just silence the linter)
    }, () => {
      // nothing to do (just silence the linter)
    });

export default i18n;
