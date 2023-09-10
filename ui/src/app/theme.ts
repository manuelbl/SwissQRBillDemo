//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { createTheme, LinkProps } from '@mui/material';
import { LinkBehavior } from './LinkBehavior';

/**
 * Base theme for app.
 * 
 * Modifies MUI links and buttons (withi links) to use React router links instead.
 */
export const baseTheme = createTheme({
  components: {
    MuiLink: {
      defaultProps: {
        component: LinkBehavior,
      } as LinkProps,
    },
    MuiButtonBase: {
      defaultProps: {
        LinkComponent: LinkBehavior,
      },
    },
  },
});
