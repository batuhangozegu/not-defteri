import { api } from './client'

export const adminApi = {
  listUsers: () => api.get('/api/admin/users'),
  approve: (id) => api.post(`/api/admin/users/${id}/approve`, {}),
  reject: (id) => api.del(`/api/admin/users/${id}`),
}
