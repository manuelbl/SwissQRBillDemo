//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import Backend from 'i18next-http-backend';

// Configuration for internationalization.
// Translation files are read from public/locales/<language>/translation.json
i18n
    .use(initReactI18next)
    .use(LanguageDetector)
    .use(Backend)
    .init({
        supportedLngs: [ 'de', 'en' ],
        fallbackLng: 'en',
        backend: {
            loadPath: '/qrbill/locales/{{lng}}/{{ns}}.json',
        },
    })
    .then(() => {
        // nothing to do (just silence the linter)
    }, () => {
        // nothing to do (just silence the linter)
    });

export default i18n;
