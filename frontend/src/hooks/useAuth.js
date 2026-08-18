import { useCallback, useEffect, useState } from 'react'
import { authApi } from '../api/auth'
import { AUTH_STORAGE_KEY, setAuthToken, setUnauthorizedHandler } from '../api/client'

function loadStored() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function useAuth() {
  const [auth, setAuth] = useState(loadStored)
  const [authError, setAuthError] = useState(null)
  const [authNotice, setAuthNotice] = useState(null)
  const [busy, setBusy] = useState(false)

  // Yedek/temizlik amaçlı: localStorage senkronu ve (varsa) token değişikliklerini
  // yansıtır. Asıl kritik olan senkron setAuthToken çağrısı login/register
  // içinde, state güncellenmeden ÖNCE yapılıyor (bkz. aşağı) — burada tekrar
  // çağrılması zararsız (idempotent).
  useEffect(() => {
    setAuthToken(auth?.token ?? null)
    if (auth) localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth))
    else localStorage.removeItem(AUTH_STORAGE_KEY)
  }, [auth])

  const logout = useCallback(() => {
    setAuthToken(null)
    setAuth(null)
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => logout())
  }, [logout])

  async function login(email, password) {
    setBusy(true)
    setAuthError(null)
    setAuthNotice(null)
    try {
      const res = await authApi.login(email, password)
      // Bilerek setAuth'tan ÖNCE: setAuth tetiklediği re-render'da (ör. NotesApp
      // ilk kez mount olurken) API istemcisinin token'ı zaten hazır olsun diye.
      // Bir useEffect'e bırakılırsa, çocuk bileşenin mount effect'i bu effect'ten
      // önce çalışabiliyor ve ilk istek token'sız gidip 401 ile anında çıkışa yol açıyor.
      setAuthToken(res.token)
      setAuth(res)
    } catch (e) {
      setAuthError(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function register(email, password, displayName) {
    setBusy(true)
    setAuthError(null)
    setAuthNotice(null)
    try {
      const res = await authApi.register(email, password, displayName)
      if (res.token) {
        setAuthToken(res.token)
        setAuth(res)
      } else {
        // Onay bekleyen kayıt: token yok, oturum açılmaz.
        setAuthNotice('Kaydın alındı. Bir yönetici hesabını onayladıktan sonra giriş yapabilirsin.')
      }
    } catch (e) {
      setAuthError(e.message)
    } finally {
      setBusy(false)
    }
  }

  return { user: auth, busy, authError, authNotice, login, register, logout }
}
