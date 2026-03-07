# FarmSense Frontend — Vue 3 PWA

Offline-first PWA for plant monitoring. Arabic/Darija RTL support.

## Stack

- **Vue 3** + Composition API + TypeScript
- **Pinia** — state management
- **Vue Router 4** — client-side routing
- **Vite** + **vite-plugin-pwa** (Workbox) — PWA + service worker
- **PouchDB** — offline queue (IndexedDB)
- **Chart.js** + vue-chartjs — sensor history charts
- **Tailwind CSS** — utility-first styling

## Project Structure

```
src/
├── components/
│   ├── plants/       # PlantCard, PlantForm, CareSchedule
│   ├── sensors/      # SensorChart, SensorCard, ReadingBadge
│   ├── alerts/       # AlertFeed, AlertBadge
│   ├── auth/         # LoginForm, RegisterForm
│   └── shared/       # OfflineBanner, LanguageSwitcher, StatusDot
├── views/
│   ├── DashboardView.vue
│   ├── PlantDetailView.vue
│   ├── DiagnoseView.vue
│   ├── DevicesView.vue
│   ├── AlertsView.vue
│   └── AuthView.vue
├── stores/
│   ├── auth.store.ts
│   ├── plants.store.ts
│   ├── readings.store.ts
│   └── alerts.store.ts
├── services/
│   ├── api.ts            # Axios with JWT auto-refresh
│   └── offline-queue.ts  # PouchDB sync queue
├── i18n/
│   └── index.ts          # FR / AR / DARIJA translations
└── types/
    └── index.ts
```

## Development

```bash
npm install
npm run dev           # http://localhost:5173
```

## PWA / Offline

The app works fully offline after first load:
- Workbox caches API GET responses (plants, readings, alerts)
- PouchDB queues POST/PUT requests when offline
- Auto-syncs when connection restores

## RTL Support

When user language is `AR` or `DARIJA`:
- `document.documentElement.dir = 'rtl'` applied
- Tailwind `rtl:` variants used for layout mirroring
- Charts and icons remain LTR (visual data)

## Environment Variables

```env
VITE_API_URL=http://localhost:8080/api/v1
```
