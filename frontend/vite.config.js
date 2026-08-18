import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  // Tek bir .env dosyası (repo kökünde) hem backend hem frontend tarafından kullanılsın diye.
  envDir: '../',
  server: {
    port: 5173,
    // Vite varsayılan olarak sadece localhost'a bağlanır; Pi/LAN üzerinden
    // "npm run dev" ile erişilebilmesi için tüm arayüzlerde dinlesin.
    host: true,
  },
})
