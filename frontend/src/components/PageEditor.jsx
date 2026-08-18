import { useCallback, useEffect, useRef, useState } from 'react'
import { pagesApi } from '../api/pages'
import Block from './Block'
import SlashMenu from './SlashMenu'
import SelectionToolbar from './SelectionToolbar'

let clientIdCounter = 0
const newClientId = () => `c${Date.now()}-${clientIdCounter++}`

export default function PageEditor({ pageId, onPageMetaChange, onAiTextAction, onError }) {
  const [page, setPage] = useState(null)
  const [blocks, setBlocks] = useState([])
  const [loading, setLoading] = useState(true)
  const [focusClientId, setFocusClientId] = useState(null)
  const [slashMenu, setSlashMenu] = useState(null)
  const [selection, setSelection] = useState(null)
  const [aiBusy, setAiBusy] = useState(false)

  const containerRef = useRef(null)
  const toolbarRef = useRef(null)
  const blockRefs = useRef(new Map())
  const saveTimer = useRef(null)
  const titleRef = useRef(null)
  const titleSaveTimer = useRef(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setSlashMenu(null)
    setSelection(null)
    Promise.all([pagesApi.get(pageId), pagesApi.blocks(pageId)])
      .then(([p, bs]) => {
        if (cancelled) return
        setPage(p)
        setBlocks(bs.map((b) => ({ ...b, clientId: newClientId() })))
        setLoading(false)
        if (titleRef.current) titleRef.current.textContent = p.title || ''
      })
      .catch((e) => {
        if (cancelled) return
        setLoading(false)
        onError?.(`Sayfa yüklenemedi: ${e.message}`)
      })
    return () => { cancelled = true }
    // onError kasıtlı olarak dışarıda bırakıldı: her render'da yeni bir referans
    // alıyor, dahil edilirse sayfa değişmeden efekt gereksiz yere tekrar çalışır.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageId])

  const registerRef = useCallback((clientId, el) => {
    if (el) blockRefs.current.set(clientId, el)
    else blockRefs.current.delete(clientId)
  }, [])

  function scheduleSave(nextBlocks) {
    clearTimeout(saveTimer.current)
    saveTimer.current = setTimeout(async () => {
      const payload = nextBlocks.map((b) => ({
        id: b.id, type: b.type, content: b.content || '', orderIndex: b.orderIndex, checked: b.checked,
      }))
      try {
        const saved = await pagesApi.replaceBlocks(pageId, payload)
        setBlocks((current) => current.map((b, i) => (saved[i] ? { ...b, id: saved[i].id } : b)))
      } catch (e) {
        onError?.(`Bloklar kaydedilemedi: ${e.message}`)
      }
    }, 700)
  }

  function updateBlocks(mutator) {
    setBlocks((current) => {
      const next = mutator(current).map((b, i) => ({ ...b, orderIndex: i }))
      scheduleSave(next)
      return next
    })
  }

  function handleChange(id, content) {
    setBlocks((current) => {
      const next = current.map((b) => (b.clientId === id ? { ...b, content } : b))
      scheduleSave(next)
      return next
    })
  }

  function handleEnter(clientId) {
    updateBlocks((current) => {
      const idx = current.findIndex((b) => b.clientId === clientId)
      const created = { clientId: newClientId(), id: null, type: 'PARAGRAPH', content: '', checked: null }
      const next = [...current]
      next.splice(idx + 1, 0, created)
      setFocusClientId(created.clientId)
      return next
    })
  }

  function handleAddAfter(clientId) {
    handleEnter(clientId)
  }

  function focusBlockEnd(clientId) {
    const el = blockRefs.current.get(clientId)
    if (!el) return
    el.focus()
    const range = document.createRange()
    range.selectNodeContents(el)
    range.collapse(false)
    const sel = window.getSelection()
    sel.removeAllRanges()
    sel.addRange(range)
  }

  function handleBackspaceEmpty(clientId) {
    setBlocks((current) => {
      if (current.length <= 1) return current
      const idx = current.findIndex((b) => b.clientId === clientId)
      const prev = current[idx - 1]
      const next = current.filter((b) => b.clientId !== clientId).map((b, i) => ({ ...b, orderIndex: i }))
      scheduleSave(next)
      if (prev) setTimeout(() => focusBlockEnd(prev.clientId), 0)
      return next
    })
  }

  function handleToggleChecked(clientId) {
    updateBlocks((current) => current.map((b) => (b.clientId === clientId ? { ...b, checked: !b.checked } : b)))
  }

  function handleDeleteBlock(clientId) {
    updateBlocks((current) => {
      const filtered = current.filter((b) => b.clientId !== clientId)
      return filtered.length ? filtered : [{ clientId: newClientId(), id: null, type: 'PARAGRAPH', content: '', checked: null }]
    })
  }

  function handleSlashSelect(type) {
    if (!slashMenu) return
    updateBlocks((current) => current.map((b) => (
      b.clientId === slashMenu.blockId ? { ...b, type, content: '', checked: type === 'TODO' ? false : null } : b
    )))
    const id = slashMenu.blockId
    setSlashMenu(null)
    setTimeout(() => {
      const el = blockRefs.current.get(id)
      if (el) { el.textContent = ''; el.focus() }
    }, 0)
  }

  function handleTitleInput(e) {
    const title = e.currentTarget.textContent
    setPage((p) => (p ? { ...p, title } : p))
    clearTimeout(titleSaveTimer.current)
    titleSaveTimer.current = setTimeout(async () => {
      try {
        const updated = await pagesApi.update(pageId, { title, icon: page?.icon ?? null, parentId: page?.parentId ?? null })
        onPageMetaChange?.(updated)
      } catch (e) {
        onError?.(`Başlık kaydedilemedi: ${e.message}`)
      }
    }, 600)
  }

  function handleChangeIcon() {
    const next = window.prompt('Sayfa ikonu (emoji):', page?.icon || '📄')
    if (next === null) return
    setPage((p) => ({ ...p, icon: next }))
    pagesApi.update(pageId, { title: page?.title || '', icon: next, parentId: page?.parentId ?? null })
      .then((updated) => onPageMetaChange?.(updated))
      .catch((e) => onError?.(`İkon kaydedilemedi: ${e.message}`))
  }

  // Metin seçimi -> mini AI toolbar
  useEffect(() => {
    function onMouseUp() {
      setTimeout(() => {
        const sel = window.getSelection()
        if (!containerRef.current || !sel || sel.isCollapsed || !sel.toString().trim()) return
        if (!containerRef.current.contains(sel.anchorNode)) return
        const rect = sel.getRangeAt(0).getBoundingClientRect()
        setSelection({ x: Math.max(140, rect.left + rect.width / 2), y: Math.max(8, rect.top - 46), text: sel.toString().trim() })
      }, 0)
    }
    function onMouseDown(e) {
      if (selection && !(toolbarRef.current && toolbarRef.current.contains(e.target))) setSelection(null)
    }
    document.addEventListener('mouseup', onMouseUp)
    document.addEventListener('mousedown', onMouseDown)
    return () => {
      document.removeEventListener('mouseup', onMouseUp)
      document.removeEventListener('mousedown', onMouseDown)
    }
  }, [selection])

  async function handleAiAction(action) {
    if (!selection) return
    setAiBusy(true)
    try {
      await onAiTextAction(action, selection.text)
    } finally {
      setAiBusy(false)
      setSelection(null)
      window.getSelection()?.removeAllRanges()
    }
  }

  if (loading || !page) {
    return <div className="flex-1 flex items-center justify-center text-[var(--ui-muted)] text-sm">Yükleniyor…</div>
  }

  return (
    <div ref={containerRef} className="flex-1 overflow-y-auto min-h-0">
      <div className="max-w-[760px] mx-auto px-12 pt-14 pb-32">
        <button onClick={handleChangeIcon} className="text-[52px] leading-none mb-3.5 block" title="İkonu değiştir">
          {page.icon || '📄'}
        </button>
        <div
          ref={titleRef}
          contentEditable
          suppressContentEditableWarning
          spellCheck={false}
          onInput={handleTitleInput}
          className="font-heading text-[38px] font-extrabold leading-[1.15] outline-none mb-1.5"
        />
        <div className="text-[12.5px] border-b-2 pb-4.5 mb-5.5" style={{ color: 'var(--ui-muted)', borderColor: 'var(--ui-line)' }}>
          {blocks.length} blok
        </div>

        {blocks.length === 0 && (
          <div className="text-[15.5px] py-1" style={{ color: 'var(--ui-muted)' }}>
            Yazmaya başlamak için <span className="border-2 px-1.5 py-px text-[13px] font-bold rounded-[5px]" style={{ borderColor: 'var(--ui-line)', color: 'var(--ui-text)' }}>/</span> yazın
          </div>
        )}

        {blocks.map((b) => (
          <Block
            key={b.clientId}
            block={b}
            autoFocus={b.clientId === focusClientId}
            onChange={handleChange}
            onEnter={handleEnter}
            onBackspaceEmpty={handleBackspaceEmpty}
            onSlash={(id, pos) => setSlashMenu({ blockId: id, ...pos })}
            onToggleChecked={handleToggleChecked}
            onAddAfter={handleAddAfter}
            onDeleteBlock={handleDeleteBlock}
            registerRef={registerRef}
          />
        ))}
      </div>

      {slashMenu && (
        <SlashMenu x={slashMenu.x} y={slashMenu.y} onSelect={handleSlashSelect} onClose={() => setSlashMenu(null)} />
      )}
      {selection && (
        <div ref={toolbarRef}>
          <SelectionToolbar x={selection.x} y={selection.y} onAction={handleAiAction} busy={aiBusy} />
        </div>
      )}
    </div>
  )
}
