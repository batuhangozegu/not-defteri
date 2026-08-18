import { useCallback, useEffect, useRef } from 'react'
import { IconCheck, IconGrip, IconPlus, IconTrash } from './icons'

const STYLE_BY_TYPE = {
  HEADING1: 'font-heading text-[28px] font-extrabold leading-[1.25] pt-[18px] pb-1',
  HEADING2: 'font-heading text-[21px] font-bold leading-[1.3] pt-3.5 pb-[3px]',
  HEADING3: 'font-heading text-[15px] font-extrabold tracking-[0.06em] uppercase leading-[1.4] pt-3 pb-0.5',
  PARAGRAPH: 'text-[15.5px] leading-[1.7] py-1',
}

export default function Block({
  block, autoFocus, placeholder, onChange, onEnter, onBackspaceEmpty, onSlash, onToggleChecked, onAddAfter, onDeleteBlock,
  registerRef,
}) {
  const ref = useRef(null)

  // Callback ref (useEffect değil): blok tipi değişince (örn. "/" ile paragraftan
  // checkbox'a) React farklı bir DOM yapısı oluşturduğu için contentEditable
  // düğümü baştan yaratılıyor. Bir useEffect + [block.clientId] bağımlılığıyla
  // kayıt tutulsaydı, clientId aynı kaldığı için tip değişiminde effect tekrar
  // çalışmaz ve blockRefs eski/kopmuş düğümü göstermeye devam ederdi — slash
  // menüsünden seçim sonrası odaklanma/yazma bu yüzden sessizce başarısız oluyordu.
  const setRef = useCallback((el) => {
    ref.current = el
    registerRef?.(block.clientId, el)
  }, [block.clientId, registerRef])

  useEffect(() => {
    if (ref.current && ref.current.textContent !== (block.content || '')) {
      ref.current.textContent = block.content || ''
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    if (autoFocus && ref.current) {
      ref.current.focus()
      const range = document.createRange()
      range.selectNodeContents(ref.current)
      range.collapse(false)
      const sel = window.getSelection()
      sel.removeAllRanges()
      sel.addRange(range)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  function handleInput(e) {
    const text = e.currentTarget.textContent
    if (text === '/') {
      const rect = e.currentTarget.getBoundingClientRect()
      onSlash(block.clientId, { x: rect.left, y: rect.bottom + 4 })
    }
    onChange(block.clientId, text)
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter') {
      e.preventDefault()
      onEnter(block.clientId)
    } else if (e.key === 'Backspace' && !e.currentTarget.textContent) {
      e.preventDefault()
      onBackspaceEmpty(block.clientId)
    }
  }

  const editable = (extraClass) => (
    <div
      ref={setRef}
      contentEditable
      suppressContentEditableWarning
      spellCheck={false}
      onInput={handleInput}
      onKeyDown={handleKeyDown}
      data-placeholder={placeholder}
      className={`outline-none min-w-0 block-placeholder ${extraClass}`}
    />
  )

  return (
    <div className="group flex items-start -ml-[52px]">
      <div className="flex gap-0.5 w-[52px] flex-shrink-0 justify-end pr-1.5 pt-2 opacity-0 group-hover:opacity-100 transition-opacity">
        <button
          title="Blok ekle"
          onClick={() => onAddAfter(block.clientId)}
          className="text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex p-0.5"
        >
          <IconPlus />
        </button>
        <button
          title="Sil"
          onClick={() => onDeleteBlock(block.clientId)}
          className="text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex p-0.5 cursor-pointer"
        >
          <IconTrash />
        </button>
        <span title="Sürükle" className="text-[var(--ui-muted)] flex p-0.5 cursor-grab">
          <IconGrip />
        </span>
      </div>
      <div className="flex-1 min-w-0">
        {block.type === 'TODO' ? (
          <div className="flex items-start gap-2.5 py-[3px] text-[15.5px] leading-[1.6]">
            <button
              onClick={() => onToggleChecked(block.clientId)}
              className="w-[17px] h-[17px] flex-shrink-0 mt-[3px] flex items-center justify-center rounded-[5px] border-2"
              style={{
                borderColor: block.checked ? 'var(--color-accent)' : 'var(--ui-muted)',
                background: block.checked ? 'var(--color-accent)' : 'transparent',
              }}
            >
              {block.checked && <IconCheck />}
            </button>
            {editable(block.checked ? 'line-through text-[var(--ui-muted)]' : '')}
          </div>
        ) : block.type === 'BULLET' ? (
          <div className="flex items-baseline gap-3 py-[3px] text-[15.5px] leading-[1.7]">
            <span className="w-[7px] h-[7px] flex-shrink-0 -translate-y-px" style={{ background: 'var(--ui-text)' }} />
            {editable('flex-1')}
          </div>
        ) : (
          editable(STYLE_BY_TYPE[block.type] || STYLE_BY_TYPE.PARAGRAPH)
        )}
      </div>
    </div>
  )
}
