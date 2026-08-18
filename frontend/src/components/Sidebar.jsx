import { useMemo, useState } from 'react'
import { IconChevron, IconPanel, IconPlus, IconSearch } from './icons'

function PageRow({ page, depth, activeId, expanded, onToggleExpand, onSelect }) {
  const hasKids = page.children?.length > 0
  const isExpanded = !!expanded[page.id]
  const isActive = page.id === activeId

  return (
    <>
      <div
        onClick={() => onSelect(page.id)}
        className="flex items-center gap-1.5 py-1.5 pr-2 cursor-pointer text-[13.5px] rounded-[var(--ui-radius)]"
        style={{
          paddingLeft: `${10 + depth * 18}px`,
          background: isActive ? 'var(--ui-hover)' : 'transparent',
          borderLeft: isActive ? '2px solid var(--color-accent)' : '2px solid transparent',
          color: isActive ? 'var(--ui-text)' : 'var(--ui-muted)',
          fontWeight: isActive ? 700 : 500,
        }}
      >
        {hasKids ? (
          <span
            onClick={(e) => { e.stopPropagation(); onToggleExpand(page.id) }}
            className="flex text-[var(--ui-muted)] transition-transform"
            style={{ transform: isExpanded ? 'rotate(90deg)' : 'none' }}
          >
            <IconChevron />
          </span>
        ) : (
          <span className="w-3 flex-shrink-0" />
        )}
        <span className="text-sm">{page.icon || '📄'}</span>
        <span className="overflow-hidden text-ellipsis whitespace-nowrap">{page.title || 'Adsız'}</span>
      </div>
      {hasKids && isExpanded && page.children.map((child) => (
        <PageRow
          key={child.id}
          page={child}
          depth={depth + 1}
          activeId={activeId}
          expanded={expanded}
          onToggleExpand={onToggleExpand}
          onSelect={onSelect}
        />
      ))}
    </>
  )
}

export default function Sidebar({ tree, activeId, onSelect, onNewPage, onToggleSidebar, onSearch }) {
  const [expanded, setExpanded] = useState({})
  const [query, setQuery] = useState('')
  const [results, setResults] = useState(null)

  const toggleExpand = (id) => setExpanded((e) => ({ ...e, [id]: !e[id] }))

  async function handleQueryChange(e) {
    const value = e.target.value
    setQuery(value)
    if (!value.trim()) {
      setResults(null)
      return
    }
    const found = await onSearch(value)
    setResults(found)
  }

  const listItems = useMemo(() => results ?? null, [results])

  return (
    <aside
      className="w-[264px] flex-shrink-0 border-r-2 flex flex-col min-h-0"
      style={{ borderColor: 'var(--ui-border)', background: 'var(--ui-surface)' }}
    >
      <div className="flex items-center justify-between px-3.5 pt-3.5 pb-2.5 pl-4">
        <div className="font-heading font-extrabold tracking-[0.08em] text-[15px]">
          NOTA<span style={{ color: 'var(--color-accent)' }}>.</span>
        </div>
        <button
          onClick={onToggleSidebar}
          title="Kenar çubuğunu daralt"
          className="p-1 text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex"
        >
          <IconPanel />
        </button>
      </div>

      <div className="px-3 pb-3">
        <div className="relative">
          <IconSearch className="absolute left-2.5 top-1/2 -translate-y-1/2 text-[var(--ui-muted)]" />
          <input
            className="input pl-8 text-[13px] h-[34px]"
            placeholder="Ara…"
            value={query}
            onChange={handleQueryChange}
          />
        </div>
      </div>

      <div className="px-3 pb-2">
        <button
          onClick={() => onNewPage(null)}
          className="btn btn-secondary btn-block flex items-center gap-2 justify-start text-[13px] h-[34px]"
        >
          <IconPlus /> Yeni Sayfa
        </button>
      </div>

      {!listItems && (
        <div
          className="text-[11px] font-bold tracking-[0.1em] uppercase px-4 pt-2.5 pb-1.5 border-t-2"
          style={{ color: 'var(--ui-muted)', borderColor: 'var(--ui-line)' }}
        >
          Sayfalar
        </div>
      )}

      <nav className="flex-1 overflow-y-auto min-h-0 px-2 py-0.5">
        {listItems ? (
          listItems.length === 0 ? (
            <div className="text-[13px] px-2 py-3" style={{ color: 'var(--ui-muted)' }}>Sonuç yok</div>
          ) : (
            listItems.map((p) => (
              <PageRow
                key={p.id}
                page={{ ...p, children: [] }}
                depth={0}
                activeId={activeId}
                expanded={expanded}
                onToggleExpand={toggleExpand}
                onSelect={onSelect}
              />
            ))
          )
        ) : (
          tree.map((p) => (
            <PageRow
              key={p.id}
              page={p}
              depth={0}
              activeId={activeId}
              expanded={expanded}
              onToggleExpand={toggleExpand}
              onSelect={onSelect}
            />
          ))
        )}
      </nav>
    </aside>
  )
}
