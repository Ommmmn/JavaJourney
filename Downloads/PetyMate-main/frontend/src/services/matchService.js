import api from './api';

export const matchService = {
  sendRequest: (data) => api.post('/api/matches/request', data),
  getReceived: (params) => api.get('/api/matches/received', { params }),
  getSent: (params) => api.get('/api/matches/sent', { params }),
  accept: (id) => api.put(`/api/matches/${id}/accept`),
  reject: (id) => api.put(`/api/matches/${id}/reject`),
  createUnlockOrder: (id) => api.post(`/api/matches/${id}/create-unlock-order`),
  unlock: (id, data) => api.post(`/api/matches/${id}/unlock`, data),
};
