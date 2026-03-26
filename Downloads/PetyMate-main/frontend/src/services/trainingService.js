import api from './api';

export const trainingService = {
  createSessionOrder: (data) => api.post('/api/training/sessions/create-order', data),
  verifySession: (data) => api.post('/api/training/sessions/verify', data),
  mySessions: (params) => api.get('/api/training/sessions/my', { params }),
  cancelSession: (id) => api.put(`/api/training/sessions/${id}/cancel`),
  buyPackageOrder: (packageId) => api.post(`/api/training/packages/${packageId}/buy-order`),
  verifyPackage: (packageId, data) => api.post(`/api/training/packages/${packageId}/buy-verify`, data),
  myPackages: () => api.get('/api/training/packages/my'),
};
