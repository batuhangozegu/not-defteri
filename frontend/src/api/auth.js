import { api } from './client'

export const authApi = {
  register: (email, password, displayName) => api.post('/api/auth/register', { email, password, displayName }),
  login: (email, password) => api.post('/api/auth/login', { email, password }),
  me: () => api.get('/api/auth/me'),
}
