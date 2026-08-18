import { useEffect, useRef, useState } from 'react'
import { IconClose, IconSend, IconSparkle } from './icons'

export default function AIPanel({ messages, typing, onClose, onSend }) {
  const [input, setInput] = useState('')
  const scrollRef = useRef(null)

  useEffect(() => {
    if (scrollRef.current) scrollRef.current.scrollTop = scrollRef.current.scrollHeight
  }, [messages, typing])

  function handleSend() {
    const text = input.trim()
    if (!text || typing) return
    setInput('')
    onSend(text)
  }

  return (
    <aside
      className="w-[340px] flex-shrink-0 border-l-2 flex flex-col min-h-0"
      style={{ borderColor: 'var(--ui-border)', background: 'var(--ui-surface)' }}
    >
      <div className="flex items-center justify-between px-3.5 py-3 border-b-2" style={{ borderColor: 'var(--ui-line)' }}>
        <div className="flex items-center gap-2">
          <span className="flex" style={{ color: 'var(--color-accent)' }}><IconSparkle /></span>
          <span className="font-heading text-[13px] font-extrabold tracking-[0.08em]">AI ASİSTAN</span>
        </div>
        <button onClick={onClose} title="Kapat" className="p-1 text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex">
          <IconClose />
        </button>
      </div>

      <div ref={scrollRef} className="flex-1 overflow-y-auto min-h-0 px-3.5 py-4 flex flex-col gap-3">
        {messages.map((m, i) => (
          <div key={i} className="flex flex-col" style={{ alignItems: m.who === 'user' ? 'flex-end' : 'flex-start' }}>
            {m.who === 'ai' && (
              <div className="text-[10.5px] font-extrabold tracking-[0.1em] mb-1" style={{ color: 'var(--color-accent)' }}>AI</div>
            )}
            <div
              className="max-w-[88%] text-[13.5px] leading-[1.6] px-3 py-2.5 rounded-[var(--ui-radius)] whitespace-pre-wrap"
              style={m.who === 'ai'
                ? { border: '2px solid var(--ui-line)', background: 'var(--ui-bg)', borderTopLeftRadius: 2 }
                : { background: 'var(--ui-text)', color: 'var(--ui-bg)', borderTopRightRadius: 2 }}
            >
              {m.text}
            </div>
          </div>
        ))}
        {typing && (
          <div className="flex flex-col items-start">
            <div className="text-[10.5px] font-extrabold tracking-[0.1em] mb-1" style={{ color: 'var(--color-accent)' }}>AI</div>
            <div className="flex gap-1.5 px-3.5 py-3 rounded-[var(--ui-radius)]" style={{ border: '2px solid var(--ui-line)', background: 'var(--ui-bg)', borderTopLeftRadius: 2 }}>
              {[0, 0.18, 0.36].map((delay, i) => (
                <span key={i} className="w-1.5 h-1.5 rounded-full" style={{ background: 'var(--ui-muted)', animation: `typingDot 1.1s infinite ${delay}s` }} />
              ))}
            </div>
          </div>
        )}
      </div>

      <div className="border-t-2 p-3 flex gap-2" style={{ borderColor: 'var(--ui-border)' }}>
        <input
          className="input flex-1 min-w-0 text-[13.5px] h-[38px]"
          placeholder="AI'ya sor…"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') handleSend() }}
        />
        <button onClick={handleSend} title="Gönder" className="btn btn-primary w-[38px] h-[38px] p-0 flex-shrink-0">
          <IconSend />
        </button>
      </div>
    </aside>
  )
}
