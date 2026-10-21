/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      // Registration lives in src/pwa.ts so an updated worker reloads the page
      // instead of leaving the tab on a shell whose chunks no longer exist.
      injectRegister: null,
      includeAssets: ['favicon.svg', 'apple-touch-icon.png'],
      manifest: {
        name: 'Shelfmates',
        short_name: 'Shelfmates',
        description: 'A readers & authors guild — shelves, reading progress, and book discovery.',
        theme_color: '#991B1B',
        background_color: '#121214',
        display: 'standalone',
        orientation: 'portrait-primary',
        scope: '/',
        start_url: '/',
        icons: [
          { src: '/icon.svg', sizes: 'any', type: 'image/svg+xml' },
          { src: '/icon.svg', sizes: '512x512', type: 'image/svg+xml', purpose: 'any maskable' },
        ],
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}'],
        navigateFallback: '/index.html',
        navigateFallbackDenylist: [/^\/api/],
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/www\.googleapis\.com\/books\/v1\//,
            handler: 'NetworkFirst',
            // Only public catalogue metadata is cached. The API key travels in
            // the `X-Goog-Api-Key` header, never in the URL, so no credential
            // can end up in the cache key or the Cache Storage payload.
            options: { cacheName: 'google-books', expiration: { maxEntries: 50, maxAgeSeconds: 60 * 60 } },
          },
        ],
      },
    }),
  ],
  server: { port: 5173, host: true },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    globals: true,
  },
})
