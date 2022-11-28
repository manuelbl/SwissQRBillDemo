//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { Typography } from "@mui/material";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";

const About: React.FC<{}> = _props => {

  const { t } = useTranslation();

  return (
    <Fragment>
      <div className="about-container">
        <Typography variant="h2" className="logo">
          <img src="swiss-qr-bill.svg" alt={t('logo') ?? 'Logo'} />
        </Typography>
        <h1>{t('app_name')}</h1>
        <p className="about">{t('copyright')}</p>
        <p className="about">
          {t('licensed_under')}&nbsp;<a href="https://opensource.org/licenses/MIT">{t('mit_license')}</a>
        </p>
        <p className="about">
          {t('source_code_on')}&nbsp;<a href="https://github.com/manuelbl/SwissQRBillDemo">{t('github')}</a>
        </p>
        <p className="about">
          {t('api_desc')}&nbsp;<a href="../qrbill-api/qrbill.yaml">{t('openapi_file')}</a>
        </p>
      </div>
    </Fragment>
  );
}

export default About;
