import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import path from 'path'

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      'uv-ui': path.resolve(__dirname, 'src/empty.js')
    }
  },
  server: {
    port: 9000,
    historyApiFallback: true,
    proxy: {
      '/user': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/article': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/album': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/column': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/comment': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/favorite': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/follow': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/oauth': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/vip': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/link': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/ai': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/tag': {
        target: 'http://localhost:5000',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://localhost:5000',
        changeOrigin: true,
        ws: true
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/styles/variables.scss"; @import "@/styles/mixins.scss";`,
        silenceDeprecations: ['legacy-js-api', 'import']
      }
    }
  }
})
