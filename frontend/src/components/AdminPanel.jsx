import { useEffect, useState } from 'react'
import { adminApi } from '../api/admin'
import { IconClose } from './icons'

export default function AdminPanel({ currentUserId, onClose, onError }) {
  const [users, setUsers] = useState(null)
  const [busyId, setBusyId] = useState(null)

  async function load() {
    try {
      setUsers(await adminApi.listUsers())
    } catch (e) {
      onError?.(`Kullanıcılar yüklenemedi: ${e.message}`)
    }
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { load() }, [])

  async function approve(id) {
    setBusyId(id)
    try {
      await adminApi.approve(id)
      await load()
    } catch (e) {
      onError?.(`Onaylanamadı: ${e.message}`)
    } finally {
      setBusyId(null)
    }
  }

  async function reject(id) {
    if (!window.confirm('Bu kullanıcıyı silmek istediğine emin misin?')) return
    setBusyId(id)
    try {
      await adminApi.reject(id)
      await load()
    } catch (e) {
      onError?.(`Silinemedi: ${e.message}`)
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4" style={{ background: 'color-mix(in srgb, black 50%, transparent)' }}>
      <div
        className="w-full max-w-[560px] max-h-[80vh] flex flex-col rounded-[var(--ui-radius)]"
        style={{ background: 'var(--ui-surface)', boxShadow: 'var(--shadow-lg)' }}
      >
        <div className="flex items-center justify-between px-4 py-3 border-b-2" style={{ borderColor: 'var(--ui-line)' }}>
          <div className="font-heading font-extrabold text-[15px] tracking-[0.04em]">Kullanıcı Yönetimi</div>
          <button onClick={onClose} className="p-1 text-[var(--ui-muted)] hover:text-[var(--ui-text)] flex">
            <IconClose />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto min-h-0 p-3 flex flex-col gap-2">
          {!users && <div className="text-[13px] px-2 py-3" style={{ color: 'var(--ui-muted)' }}>Yükleniyor…</div>}
          {users?.length === 0 && <div className="text-[13px] px-2 py-3" style={{ color: 'var(--ui-muted)' }}>Kullanıcı yok</div>}
          {users?.map((u) => (
            <div
              key={u.id}
              className="flex items-center justify-between gap-3 px-3 py-2.5 rounded-[var(--ui-radius)]"
              style={{ background: 'var(--ui-bg)' }}
            >
              <div className="min-w-0">
                <div className="text-[13.5px] font-semibold overflow-hidden text-ellipsis whitespace-nowrap">
                  {u.displayName || u.email}
                  {u.id === currentUserId && <span style={{ color: 'var(--ui-muted)' }}> (sen)</span>}
                </div>
                <div className="text-[12px] overflow-hidden text-ellipsis whitespace-nowrap" style={{ color: 'var(--ui-muted)' }}>
                  {u.email} · {u.role === 'ADMIN' ? 'Yönetici' : 'Kullanıcı'}
                </div>
              </div>
              <div className="flex items-center gap-2 flex-shrink-0">
                {u.approved ? (
                  <span className="tag tag-accent">Onaylı</span>
                ) : (
                  <button
                    disabled={busyId === u.id}
                    onClick={() => approve(u.id)}
                    className="btn btn-primary text-[12.5px] h-8"
                  >
                    Onayla
                  </button>
                )}
                {u.id !== currentUserId && (
                  <button
                    disabled={busyId === u.id}
                    onClick={() => reject(u.id)}
                    className="btn btn-secondary text-[12.5px] h-8"
                  >
                    Sil
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
