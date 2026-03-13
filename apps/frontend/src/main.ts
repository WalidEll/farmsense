import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as Sentry from '@sentry/vue'
import router from '@/router'
import App from './App.vue'
import './assets/main.css'

const app = createApp(App)
app.use(createPinia())

Sentry.init({
  app,
  dsn: 'https://c3a015f259e2415951ff7c89750776f3@o4511038357700608.ingest.de.sentry.io/4511039117590608',
  integrations: [
    Sentry.browserTracingIntegration({ router }),
    Sentry.replayIntegration(),
  ],
  tracesSampleRate: 1.0,
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0,
})

app.use(router)
app.mount('#app')
