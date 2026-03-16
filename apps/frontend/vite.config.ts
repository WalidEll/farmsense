import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { VitePWA } from 'vite-plugin-pwa'
import path from 'path'

export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'masked-icon.svg'],
      manifest: {
        name: 'FarmSense',
        short_name: 'FarmSense',
        description: 'مراقبة النباتات الذكية — Surveillance intelligente des plantes',
        theme_color: '#3D6B4F',
        background_color: '#F4F6F0',
        display: 'standalone',
        orientation: 'portrait',
        start_url: '/',
        icons: [
          { src: 'pwa-192x192.png', sizes: '192x192', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png', purpose: 'any maskable' }
        ]
      },
      workbox: {
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/api\.farmsense\.ma\/api\/v1\/(plants|readings|alerts|devices)/,
            handler: 'StaleWhileRevalidate',
            options: {
              cacheName: 'farmsense-api-cache',
              expiration: { maxEntries: 200, maxAgeSeconds: 3600 },
              cacheableResponse: { statuses: [0, 200] }
            }
          },
          {
            urlPattern: /^https:\/\/api\.farmsense\.ma\/api\/v1\/breeds$/,
            handler: 'StaleWhileRevalidate',
            options: {
              cacheName: 'breeds-list-cache',
              expiration: { maxEntries: 50, maxAgeSeconds: 3600 },
              cacheableResponse: { statuses: [0, 200] }
            }
          },
          {
            urlPattern: /^https:\/\/api\.farmsense\.ma\/api\/v1\/breeds\/[a-f0-9-]+$/,
            handler: 'StaleWhileRevalidate',
            options: {
              cacheName: 'breeds-detail-cache',
              expiration: { maxEntries: 100, maxAgeSeconds: 3600 },
              cacheableResponse: { statuses: [0, 200] }
            }
          }
        ]
      }
    })
  ],
  resolve: {
    alias: { '@': path.resolve(__dirname, './src') }
  },
  optimizeDeps: {
    include: ['pouchdb', 'pouchdb-find']
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
