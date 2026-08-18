const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const AUTH_STORAGE_KEY = 'not-defteri-auth'

// Modül yüklenir yüklenmez (React render'a başlamadan önce) localStorage'dan okunur.
// Bu sayede sayfa yenilendiğinde/girişten hemen sonra ilk render'da mount olan
// bileşenlerin ilk API isteği token'sız gitmiyor — token'ın set edilmesini bir
// useEffect'e bırakmak, o effect henüz çalışmadan çocuk bileşenlerin istek atmasına
// (ve 401 alıp anında çıkışa) yol açan bir yarış durumuydu.
let authToken = readStoredToken()
let onUnauthorized = null

function readStoredToken() {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw)?.token ?? null : null
  } catch {
    return null
  }
}

export function setAuthToken(token) {
  authToken = token
}

/** 401 (geçersiz/süresi dolmuş token) alındığında çağrılacak fonksiyonu kaydeder. */
export function setUnauthorizedHandler(fn) {
  onUnauthorized = fn
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers }
  if (authToken) headers.Authorization = `Bearer ${authToken}`

  const res = await fetch(`${BASE_URL}${path}`, { headers, ...options })

  if (res.status === 401) {
    onUnauthorized?.()
  }

  if (!res.ok) {
    let message = `İstek başarısız: ${res.status}`
    try {
      const body = await res.json()
      if (body?.message) message = body.message
    } catch {
      // gövde JSON değilse orijinal mesaj kalır
    }
    throw new Error(message)
  }
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body: JSON.stringify(body) }),
  put: (path, body) => request(path, { method: 'PUT', body: JSON.stringify(body) }),
  del: (path) => request(path, { method: 'DELETE' }),
}
