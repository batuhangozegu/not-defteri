const OPTIONS = [
  { type: 'PARAGRAPH', label: 'Paragraf', hint: 'Düz metin' },
  { type: 'HEADING1', label: 'Başlık 1', hint: 'Büyük başlık' },
  { type: 'HEADING2', label: 'Başlık 2', hint: 'Orta başlık' },
  { type: 'HEADING3', label: 'Başlık 3', hint: 'Küçük başlık' },
  { type: 'BULLET', label: 'Madde İşareti', hint: 'Sırasız liste' },
  { type: 'TODO', label: 'Yapılacak', hint: 'Onay kutulu görev' },
]

export default function SlashMenu({ x, y, onSelect, onClose }) {
  return (
    <>
      <div className="fixed inset-0 z-40" onClick={onClose} />
      <div
        className="fixed z-50 w-56 py-1.5 border-2 rounded-[var(--ui-radius)] overflow-hidden"
        style={{ left: x, top: y, background: 'var(--ui-surface)', borderColor: 'var(--ui-border)', boxShadow: 'var(--shadow-md)' }}
      >
        {OPTIONS.map((opt) => (
          <button
            key={opt.type}
            onClick={() => onSelect(opt.type)}
            className="w-full text-left px-3 py-1.5 flex flex-col hover:bg-[var(--ui-hover)]"
          >
            <span className="text-[13.5px] font-semibold">{opt.label}</span>
            <span className="text-[11px]" style={{ color: 'var(--ui-muted)' }}>{opt.hint}</span>
          </button>
        ))}
      </div>
    </>
  )
}
