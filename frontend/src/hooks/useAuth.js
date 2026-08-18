import { useCallback, useEffect, useState } from 'react'
import { authApi } from '../api/auth'
import { setAuthToken, setUnauthorizedHandler } from '../api/client'

const STORAGE_KEY = 'not-defteri-auth'

function loadStored() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function useAuth() {
  const [auth, setAuth] = useState(loadStored)
  const [authError, setAuthError] = useState(null)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    setAuthToken(auth?.token ?? null)
    if (auth) localStorage.setItem(STORAGE_KEY, JSON.stringify(auth))
    else localStorage.removeItem(STORAGE_KEY)
  }, [auth])

  const logout = useCallback(() => {
    setAuth(null)
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => logout())
  }, [logout])

  async function login(email, password) {
    setBusy(true)
    setAuthError(null)
    try {
      const res = await authApi.login(email, password)
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
    try {
      const res = await authApi.register(email, password, displayName)
      setAuth(res)
    } catch (e) {
      setAuthError(e.message)
    } finally {
      setBusy(false)
    }
  }

  return { user: auth, busy, authError, login, register, logout }
}
