//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

import { forwardRef } from 'react';
import { Link as RouterLink, LinkProps as RouterLinkProps } from 'react-router-dom';
import { createTheme, LinkProps } from '@mui/material';

/**
 * Forward ref to HTML 'a' element, replacing 'href' with 'to'
 */
const LinkBehavior = forwardRef<
  HTMLAnchorElement,
  Omit<RouterLinkProps, 'to'> & { href: RouterLinkProps['to'] }
>((props, ref) => {
  const { href, ...other } = props;
  return <RouterLink ref={ref} to={href} {...other} />;
});

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
