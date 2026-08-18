import { api } from './client'

export const aiApi = {
  summarize: (text) => api.post('/api/ai/summarize', { text }),
  expand: (text) => api.post('/api/ai/expand', { text }),
  fix: (text) => api.post('/api/ai/fix', { text }),
  ask: (question) => api.post('/api/ai/ask', { question }),
}
