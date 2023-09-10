//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { AppBar, Box, Button, IconButton, Toolbar } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { useTranslation } from 'react-i18next';

/**
 * Navigation bar at the top
 */
const NavBar = () => {

  const { t } = useTranslation();

  return (
    <AppBar component="nav" sx={{ backgroundColor: 'white' }}>
      <Toolbar>
        <Box>
          <Button className='title' href='/' key='bill' sx={{ color: '#000', fontWeight: 'bold' }} startIcon={<img src='/qrbill/swiss-qr-bill.svg' alt={t('logo_alt') ?? ''}/>}>{t('navTitle')}</Button>
          <Button href='/examples' key='examples' sx={{ color: '#000' }}>{t('navExamples')}</Button>
          <Button href='/about' key='about' sx={{ color: '#000' }}>{t('navAbout')}</Button>
          <IconButton href='/settings' key='more' sx={{ color: '#000' }}>
            <MoreVertIcon/>
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default NavBar;
