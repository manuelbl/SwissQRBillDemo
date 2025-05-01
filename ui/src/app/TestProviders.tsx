//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { ThemeProvider } from '@mui/material'
import { baseTheme } from './theme'
import { BrowserRouter } from 'react-router'

export const TestProviders = ({children}: {children: React.ReactNode}) => {
  return (
    <BrowserRouter>
      <ThemeProvider theme={baseTheme}>
        {children}
      </ThemeProvider>
    </BrowserRouter>
  )
}
