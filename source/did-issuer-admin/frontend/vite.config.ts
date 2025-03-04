import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    proxy: {
      // /issuer/admin/v1 → http://localhost:8091/issuer/admin/v1
      '/issuer/admin/v1': {
        target: 'http://localhost:8091',
        changeOrigin: true,
      },
    },
  },
});
