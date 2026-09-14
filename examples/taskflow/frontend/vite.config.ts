// `defineConfig` comes from `vitest/config`, not `vite` — that is the variant
// whose type includes the `test` block. Importing it from `vite` compiles
// everything else fine and then fails with
// "'test' does not exist in type 'UserConfigExport'".
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

/**
 * Vite + Vitest configuration.
 *
 * The `/api` proxy is what makes local development painless: the frontend
 * calls same-origin relative URLs (`/api/v1/tasks`), so there is no base-URL
 * juggling between dev and production and no CORS preflight in the common
 * path. The backend's CORS config is the belt to this braces.
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.ts'],
    css: false,
  },
});
