import api from './api';

export const authService = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
  refresh: (refreshToken) => api.post('/api/auth/refresh', { refreshToken }),
  logout: () => api.post('/api/auth/logout'),
  getMe: () => api.get('/api/auth/me'),
  updateMe: (data) => api.put('/api/auth/me', data),
  uploadPhoto: (file) => {
    const fd = new FormData();
    fd.append('file', file);
    return api.post('/api/auth/me/photo', fd, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
};
