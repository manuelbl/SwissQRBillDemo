import type { InlineConfig } from 'vitest/node';
import type { UserConfig } from 'vite';
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

type ViteConfig = UserConfig & { test: InlineConfig };

// https://vite.dev/config/
const config: ViteConfig = {
  plugins: [react()],

  base: '/qrbill/',

  server: {
    proxy: {
      '/qrbill-api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    }
  },

  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/setupTests.ts'],
  },

  build: {
    chunkSizeWarningLimit: 700,
  },
};

export default defineConfig(config);
