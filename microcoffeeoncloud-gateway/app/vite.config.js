import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(() => {
  return {
    build: {
      outDir: 'build',
    },
    plugins: [react()],
    test: {
      root: './src',
      globals: true,
      environment: 'jsdom',
      setupFiles: './setupTests.js'
    }
  };
});
