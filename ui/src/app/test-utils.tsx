//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import React, { FC, ReactElement } from 'react'
import { render, RenderOptions } from '@testing-library/react'
import { ThemeProvider } from '@mui/material'
import { baseTheme } from './theme'
import { BrowserRouter } from 'react-router-dom'

const AllTheProviders: FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <BrowserRouter>
      <ThemeProvider theme={baseTheme}>
        {children}
      </ThemeProvider>
    </BrowserRouter>
  )
}

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>,
) => render(ui, { wrapper: AllTheProviders, ...options })

export * from '@testing-library/react'
export { customRender as render }
