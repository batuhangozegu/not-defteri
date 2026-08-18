import { IconSparkle } from './icons'

export default function SelectionToolbar({ x, y, onAction, busy }) {
  return (
    <div
      className="fixed z-50 flex items-stretch overflow-hidden rounded-[var(--ui-radius)]"
      style={{ left: x, top: y, transform: 'translateX(-50%)', background: 'var(--ui-text)', color: 'var(--ui-bg)', boxShadow: 'var(--shadow-md)' }}
    >
      <span className="flex items-center pl-2.5 pr-2" style={{ color: 'var(--color-accent)' }}>
        <IconSparkle />
      </span>
      {[
        { key: 'summarize', label: 'Özetle' },
        { key: 'expand', label: 'Genişlet' },
        { key: 'fix', label: 'Düzelt' },
      ].map((a) => (
        <button
          key={a.key}
          disabled={busy}
          onClick={() => onAction(a.key)}
          className="border-l border-white/20 px-3 py-2 text-[12.5px] font-bold disabled:opacity-50 hover:text-[var(--color-accent)]"
        >
          {a.label}
        </button>
      ))}
    </div>
  )
}
