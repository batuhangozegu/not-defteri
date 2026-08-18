import { useCallback, useEffect, useState } from 'react'

const STORAGE_KEY = 'not-defteri-theme'

export function useTheme() {
  const [dark, setDark] = useState(() => {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) return stored === 'dark'
    return window.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, dark ? 'dark' : 'light')
  }, [dark])

  const toggle = useCallback(() => setDark((d) => !d), [])

  return { theme: dark ? 'dark' : 'light', dark, toggle }
}
