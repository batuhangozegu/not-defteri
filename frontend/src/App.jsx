import { useCallback, useEffect, useState } from 'react'
import Sidebar from './components/Sidebar'
import Topbar from './components/Topbar'
import PageEditor from './components/PageEditor'
import AIPanel from './components/AIPanel'
import { pagesApi } from './api/pages'
import { aiApi } from './api/ai'
import { useTheme } from './hooks/useTheme'

const ACTION_LABELS = { summarize: 'Özetle', expand: 'Genişlet', fix: 'Düzelt' }

function findPath(nodes, id, trail = []) {
  for (const node of nodes) {
    const path = [...trail, node]
    if (node.id === id) return path
    const found = findPath(node.children || [], id, path)
    if (found) return found
  }
  return null
}

function flatten(nodes) {
  return nodes.flatMap((n) => [n, ...flatten(n.children || [])])
}

export default function App() {
  const { theme, dark, toggle: toggleTheme } = useTheme()
  const [tree, setTree] = useState([])
  const [activeId, setActiveId] = useState(null)
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [aiOpen, setAiOpen] = useState(true)
  const [messages, setMessages] = useState([
    { who: 'ai', text: 'Merhaba! Bu sayfayla ilgili soru sorabilir, metin seçip Özetle / Genişlet / Düzelt aksiyonlarını kullanabilirsin.' },
  ])
  const [typing, setTyping] = useState(false)
  const [error, setError] = useState(null)

  const loadTree = useCallback(async () => {
    const t = await pagesApi.tree()
    setTree(t)
    return t
  }, [])

  useEffect(() => {
    loadTree()
      .then((t) => {
        const first = flatten(t)[0]
        if (first) setActiveId(first.id)
      })
      .catch((e) => setError(`Sayfalar yüklenemedi: ${e.message}`))
  }, [loadTree])

  async function handleNewPage(parentId) {
    try {
      const created = await pagesApi.create({ title: 'Adsız', icon: '📄', parentId })
      await loadTree()
      setActiveId(created.id)
    } catch (e) {
      setError(`Sayfa oluşturulamadı: ${e.message}`)
    }
  }

  async function handleSearch(query) {
    try {
      return await pagesApi.search(query)
    } catch (e) {
      setError(`Arama başarısız: ${e.message}`)
      return []
    }
  }

  async function handlePageMetaChange() {
    try {
      await loadTree()
    } catch (e) {
      setError(`Sayfa listesi güncellenemedi: ${e.message}`)
    }
  }

  async function askAi(question, userLabel) {
    setMessages((m) => [...m, { who: 'user', text: userLabel ?? question }])
    setAiOpen(true)
    setTyping(true)
    try {
      const res = await aiApi.ask(question)
      setMessages((m) => [...m, { who: 'ai', text: res.answer }])
    } catch (e) {
      setMessages((m) => [...m, { who: 'ai', text: `Bir hata oluştu: ${e.message}` }])
    } finally {
      setTyping(false)
    }
  }

  async function handleAiTextAction(action, text) {
    const label = `${ACTION_LABELS[action]}: "${text.slice(0, 90)}${text.length > 90 ? '…' : ''}"`
    setMessages((m) => [...m, { who: 'user', text: label }])
    setAiOpen(true)
    setTyping(true)
    try {
      const res = await aiApi[action](text)
      setMessages((m) => [...m, { who: 'ai', text: res.result }])
    } catch (e) {
      setMessages((m) => [...m, { who: 'ai', text: `Bir hata oluştu: ${e.message}` }])
    } finally {
      setTyping(false)
    }
  }

  const path = activeId ? findPath(tree, activeId) : null
  const breadcrumb = path ? path.map((p) => p.title || 'Adsız').join(' / ') : ''

  return (
    <div
      data-theme={theme}
      className="flex h-screen overflow-hidden"
      style={{ background: 'var(--ui-bg)', color: 'var(--ui-text)', fontFamily: 'var(--font-body)' }}
    >
      {sidebarOpen && (
        <Sidebar
          tree={tree}
          activeId={activeId}
          onSelect={setActiveId}
          onNewPage={handleNewPage}
          onToggleSidebar={() => setSidebarOpen(false)}
          onSearch={handleSearch}
        />
      )}

      <main className="flex-1 min-w-0 flex flex-col min-h-0">
        <Topbar
          sidebarOpen={sidebarOpen}
          onToggleSidebar={() => setSidebarOpen(true)}
          breadcrumb={breadcrumb}
          dark={dark}
          onToggleTheme={toggleTheme}
          aiOpen={aiOpen}
          onToggleAi={() => setAiOpen((o) => !o)}
        />
        {error && (
          <div
            className="flex items-center justify-between gap-3 px-4 py-2 text-[13px] border-b-2 flex-shrink-0"
            style={{ background: 'var(--ui-accent-soft)', color: 'var(--ui-text)', borderColor: 'var(--ui-line)' }}
          >
            <span>{error}</span>
            <button onClick={() => setError(null)} className="font-bold px-1">✕</button>
          </div>
        )}
        {activeId ? (
          <PageEditor
            key={activeId}
            pageId={activeId}
            onPageMetaChange={handlePageMetaChange}
            onAiTextAction={handleAiTextAction}
            onError={(msg) => setError(msg)}
          />
        ) : (
          <div className="flex-1 flex items-center justify-center text-[var(--ui-muted)] text-sm">
            Başlamak için soldan bir sayfa seç ya da "Yeni Sayfa" ile oluştur.
          </div>
        )}
      </main>

      {aiOpen && (
        <AIPanel
          messages={messages}
          typing={typing}
          onClose={() => setAiOpen(false)}
          onSend={(text) => askAi(text)}
        />
      )}
    </div>
  )
}
