import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react({
      babel: {
        plugins: [['babel-plugin-react-compiler']],
      },
    }),
  ],
   server: {
    proxy: {
      "/auth": "http://localhost:8080",
      "/user": "http://localhost:8080",
      "/tickets": "http://localhost:8080",
      "/api": "http://localhost:8080",       
      "/uploads": "http://localhost:8080",
      "/meta": "http://localhost:8080",
      "/ws": { target: "http://localhost:8080", ws: true },
    },
  },
})
