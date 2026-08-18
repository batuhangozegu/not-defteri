import { IconMoon, IconPanel, IconSparkle, IconSun } from './icons'

export default function Topbar({ sidebarOpen, onToggleSidebar, breadcrumb, dark, onToggleTheme, aiOpen, onToggleAi }) {
  return (
    <div
      className="flex items-center justify-between h-12 px-4 border-b-2 flex-shrink-0"
      style={{ borderColor: 'var(--ui-line)' }}
    >
      <div className="flex items-center gap-2.5 min-w-0">
        {!sidebarOpen && (
          <button
            onClick={onToggleSidebar}
            title="Kenar çubuğunu aç"
            className="p-1 text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex"
          >
            <IconPanel />
          </button>
        )}
        <div className="text-[13px] overflow-hidden text-ellipsis whitespace-nowrap" style={{ color: 'var(--ui-muted)' }}>
          {breadcrumb}
        </div>
      </div>
      <div className="flex items-center gap-2">
        <button
          onClick={onToggleTheme}
          title="Tema"
          className="w-8 h-8 flex items-center justify-center border-2 rounded-[var(--ui-radius)]"
          style={{ borderColor: 'var(--ui-line)', color: 'var(--ui-text)' }}
        >
          {dark ? <IconMoon /> : <IconSun />}
        </button>
        <button
          onClick={onToggleAi}
          className="flex items-center gap-1.5 h-8 px-3 text-[12.5px] font-bold tracking-[0.04em] border-2 rounded-[var(--ui-radius)]"
          style={{
            borderColor: 'var(--color-accent)',
            background: aiOpen ? 'var(--color-accent)' : 'transparent',
            color: aiOpen ? '#fff' : 'var(--color-accent-700)',
          }}
        >
          <IconSparkle /> AI Asistan
        </button>
      </div>
    </div>
  )
}
