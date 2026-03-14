export interface NavBadge {
  type: 'danger' | 'count';
  key: string;
}

export interface NavItem {
  icon: string;
  labelKey: string;
  to: string;
  exact?: boolean;
  badge?: NavBadge | 'new';
}

export interface NavGroup {
  nameKey: string;
  icon: string;
  items: NavItem[];
  defaultOpen?: boolean;
}

/* ── SVG icon helper (all 24×24, stroke-based) ── */
const s = (d: string) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`

/* ── Icon library ── */
const icons = {
  // Plant Management
  fields:     s('<rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18M9 21V9"/>'),
  crops:      s('<path d="M7 20h10M10 20c5.5-2.5.8-6.4 3-10"/><path d="M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z"/><path d="M14.1 6a7 7 0 0 0-1.5 3.5 9.5 9.5 0 0 0 5.3 1.5c.3-2.7-.8-4.8-3.8-5z"/>'),
  plans:      s('<path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="M9 12h6M9 16h4"/>'),
  irrigation: s('<path d="M12 22a7 7 0 0 0 7-7c0-3.87-7-13-7-13S5 11.13 5 15a7 7 0 0 0 7 7z"/>'),
  harvest:    s('<path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>'),
  locations:  s('<path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/>'),

  // Poultry
  poultry:    s('<circle cx="12" cy="12" r="10"/><path d="M12 8c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4z"/>'),
  flocks:     s('<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>'),
  eggs:       s('<ellipse cx="12" cy="12" rx="5" ry="7"/><path d="M12 5V2M12 19v3"/>'),
  weight:     s('<path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>'),
  feed:       s('<path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/>'),

  // Finance
  dashboard$: s('<path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>'),
  txn:        s('<rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/>'),
  receipts:   s('<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/>'),
  labor:      s('<circle cx="12" cy="5" r="3"/><path d="M12 8v4l3 5M12 12l-3 5"/><path d="M7 21h10"/>'),
  tags:       s('<path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/>'),

  // Partners
  customers:  s('<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>'),
  suppliers:  s('<path d="M21 10V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l2-1.14"/><path d="M16.5 9.4L7.55 4.24"/><polyline points="3.29 7 12 12 20.71 7"/><line x1="12" y1="22" x2="12" y2="12"/>'),

  // Admin
  sensors:    s('<path d="M5.12 5.12A7 7 0 0 1 12 5c3.87 0 7 3.13 7 7a7 7 0 0 1-1.12 3.88"/><circle cx="12" cy="12" r="3"/><path d="M2 2l20 20"/>'),
  alerts:     s('<path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0"/>'),
  team:       s('<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>'),
  settings:   s('<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>'),

  // Group icons
  plant:      s('<path d="M12 22V12M12 12C12 12 7 9 4 4c3 0 6 1.5 8 5"/><path d="M12 12c0 0 5-3 8-8-3 0-6 1.5-8 5"/><path d="M5 20h14"/>'),
  finance:    s('<rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/>'),
  partners:   s('<path d="M16 3.13a4 4 0 0 1 0 7.75"/><path d="M21 21v-2a4 4 0 0 0-3-3.87"/><path d="M5 21v-2a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v2"/><circle cx="11" cy="7" r="4"/>'),
  admin:      s('<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/>'),
}

/* ──────────────────────────────────────────────
 *  NAVIGATION CONFIG — Domain-based grouping
 *  Priority: high-use items first in each group
 * ────────────────────────────────────────────── */
export const navigationConfig: NavGroup[] = [
  /* ── 1. Plant Management ── */
  {
    nameKey: 'nav.group.plant',
    icon: icons.plant,
    defaultOpen: true,
    items: [
      { icon: icons.fields,     labelKey: 'nav.fields',           to: '/fields' },
      { icon: icons.crops,      labelKey: 'nav.crops',            to: '/crops' },
      { icon: icons.plans,      labelKey: 'nav.planting_records', to: '/plans' },
      { icon: icons.locations,  labelKey: 'nav.locations',        to: '/locations' },
      { icon: icons.irrigation, labelKey: 'nav.irrigation',       to: '/irrigation' },
      { icon: icons.harvest,    labelKey: 'nav.harvest_records',  to: '/harvests' },
    ]
  },

  /* ── 2. Poultry Management ── */
  {
    nameKey: 'nav.group.poultry',
    icon: icons.poultry,
    items: [
      { icon: icons.flocks, labelKey: 'nav.flocks',          to: '/poultry/flocks' },
      { icon: icons.eggs,   labelKey: 'nav.egg_collection',  to: '/poultry/eggs' },
      { icon: icons.feed,   labelKey: 'nav.feed_records',    to: '/poultry/feed' },
      { icon: icons.weight, labelKey: 'nav.bird_weight',     to: '/poultry/weight' },
    ]
  },

  /* ── 3. Inventory ── */
  {
    nameKey: 'nav.group.inventory',
    icon: s('<path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>'),
    items: [
      { icon: s('<rect x="2" y="3" width="20" height="18" rx="2"/><path d="M2 9h20M8 21V9"/>'), labelKey: 'nav.inventory_dashboard', to: '/inventory', exact: true },
      { icon: s('<path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>'),  labelKey: 'nav.inventory_items',     to: '/inventory/items' },
      { icon: s('<polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>'), labelKey: 'nav.stock_movements', to: '/inventory/movements' },
    ]
  },

  /* ── 4. Finance ── */
  {
    nameKey: 'nav.group.finance',
    icon: icons.finance,
    items: [
      { icon: icons.dashboard$, labelKey: 'nav.accounting',    to: '/accounting', exact: true },
      { icon: icons.txn,        labelKey: 'nav.transactions',  to: '/accounting/transactions' },
      { icon: icons.receipts,   labelKey: 'nav.receipts',      to: '/accounting/receipts' },
      { icon: icons.labor,      labelKey: 'nav.labor',         to: '/accounting/labor' },
      { icon: icons.tags,       labelKey: 'nav.tags',          to: '/accounting/tags' },
    ]
  },

  /* ── 5. Partners ── */
  {
    nameKey: 'nav.group.partners',
    icon: icons.partners,
    items: [
      { icon: icons.customers, labelKey: 'nav.customers',  to: '/poultry/customers' },
      { icon: icons.suppliers, labelKey: 'nav.suppliers',   to: '/poultry/suppliers' },
    ]
  },

  /* ── 6. Administration ── */
  {
    nameKey: 'nav.group.admin',
    icon: icons.admin,
    items: [
      { icon: icons.sensors,  labelKey: 'nav.sensors',   to: '/sensors' },
      { icon: icons.alerts,   labelKey: 'nav.alerts',    to: '/alerts', badge: { type: 'danger', key: 'alerts' } },
      { icon: icons.team,     labelKey: 'nav.team',      to: '/accounting/team' },
      { icon: icons.settings, labelKey: 'nav.settings',  to: '/settings' },
    ]
  },
]
