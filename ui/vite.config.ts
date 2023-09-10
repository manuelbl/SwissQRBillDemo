/// <reference types="vitest" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
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
    chunkSizeWarningLimit: 600,
  },
})
