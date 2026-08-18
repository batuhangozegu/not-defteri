// Tasarım importundaki (Modernist / Lucide tarzı) ikonlar birebir taşındı.
const base = { fill: 'none', stroke: 'currentColor', strokeWidth: 2 }

export const IconPanel = (p) => (
  <svg width="16" height="16" viewBox="0 0 24 24" {...base} {...p}>
    <rect x="3" y="3" width="18" height="18" />
    <line x1="9" y1="3" x2="9" y2="21" />
  </svg>
)

export const IconSearch = (p) => (
  <svg width="14" height="14" viewBox="0 0 24 24" {...base} {...p}>
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
)

export const IconPlus = (p) => (
  <svg width="14" height="14" viewBox="0 0 24 24" {...base} {...p}>
    <path d="M12 5v14" />
    <path d="M5 12h14" />
  </svg>
)

export const IconChevron = (p) => (
  <svg width="12" height="12" viewBox="0 0 24 24" {...base} strokeWidth={2.5} {...p}>
    <polyline points="9 18 15 12 9 6" />
  </svg>
)

export const IconGrip = (p) => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="none" {...p}>
    <circle cx="9" cy="5" r="1.6" /><circle cx="9" cy="12" r="1.6" /><circle cx="9" cy="19" r="1.6" />
    <circle cx="15" cy="5" r="1.6" /><circle cx="15" cy="12" r="1.6" /><circle cx="15" cy="19" r="1.6" />
  </svg>
)

export const IconSun = (p) => (
  <svg width="15" height="15" viewBox="0 0 24 24" {...base} {...p}>
    <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
  </svg>
)

export const IconMoon = (p) => (
  <svg width="15" height="15" viewBox="0 0 24 24" {...base} {...p}>
    <circle cx="12" cy="12" r="5" />
    <line x1="12" y1="1" x2="12" y2="3" /><line x1="12" y1="21" x2="12" y2="23" />
    <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" /><line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
    <line x1="1" y1="12" x2="3" y2="12" /><line x1="21" y1="12" x2="23" y2="12" />
    <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" /><line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
  </svg>
)

export const IconSparkle = (p) => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="none" {...p}>
    <path d="M12 2.5l2.1 6.4 6.4 2.1-6.4 2.1L12 19.5l-2.1-6.4-6.4-2.1 6.4-2.1z" />
  </svg>
)

export const IconClose = (p) => (
  <svg width="16" height="16" viewBox="0 0 24 24" {...base} {...p}>
    <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
  </svg>
)

export const IconSend = (p) => (
  <svg width="15" height="15" viewBox="0 0 24 24" {...base} {...p}>
    <line x1="22" y1="2" x2="11" y2="13" />
    <polygon points="22 2 15 22 11 13 2 9 22 2" />
  </svg>
)

export const IconCheck = (p) => (
  <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth={3.5} {...p}>
    <polyline points="20 6 9 17 4 12" />
  </svg>
)

export const IconShield = (p) => (
  <svg width="15" height="15" viewBox="0 0 24 24" {...base} {...p}>
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
  </svg>
)

export const IconLogout = (p) => (
  <svg width="15" height="15" viewBox="0 0 24 24" {...base} {...p}>
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
    <polyline points="16 17 21 12 16 7" />
    <line x1="21" y1="12" x2="9" y2="12" />
  </svg>
)

export const IconTrash = (p) => (
  <svg width="14" height="14" viewBox="0 0 24 24" {...base} {...p}>
    <polyline points="3 6 5 6 21 6" />
    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
    <path d="M10 11v6" /><path d="M14 11v6" />
    <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
  </svg>
)
