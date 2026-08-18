import { api } from './client'

export const pagesApi = {
  tree: () => api.get('/api/pages'),
  search: (query) => api.get(`/api/pages/search?q=${encodeURIComponent(query)}`),
  get: (id) => api.get(`/api/pages/${id}`),
  create: (data) => api.post('/api/pages', data),
  update: (id, data) => api.put(`/api/pages/${id}`, data),
  remove: (id) => api.del(`/api/pages/${id}`),

  blocks: (pageId) => api.get(`/api/pages/${pageId}/blocks`),
  replaceBlocks: (pageId, blocks) => api.put(`/api/pages/${pageId}/blocks`, { blocks }),
  deleteBlock: (pageId, blockId) => api.del(`/api/pages/${pageId}/blocks/${blockId}`),
}
