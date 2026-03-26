import api from './api';

export const adminService = {
  getDashboard: () => api.get('/api/admin/dashboard'),
  getUsers: (params) => api.get('/api/admin/users', { params }),
  banUser: (id) => api.put(`/api/admin/users/${id}/ban`),
  unbanUser: (id) => api.put(`/api/admin/users/${id}/unban`),
  updateTier: (id, tier) => api.put(`/api/admin/users/${id}/tier`, { tier }),
  getPets: (params) => api.get('/api/admin/pets', { params }),
  approvePet: (id) => api.put(`/api/admin/pets/${id}/approve`),
  rejectPet: (id) => api.put(`/api/admin/pets/${id}/reject`),
  getVets: (params) => api.get('/api/admin/vets', { params }),
  verifyVet: (id) => api.put(`/api/admin/vets/${id}/verify`),
  unverifyVet: (id) => api.put(`/api/admin/vets/${id}/unverify`),
  getTrainers: (params) => api.get('/api/admin/trainers', { params }),
  verifyTrainer: (id) => api.put(`/api/admin/trainers/${id}/verify`),
  unverifyTrainer: (id) => api.put(`/api/admin/trainers/${id}/unverify`),
  getOrders: (params) => api.get('/api/admin/orders', { params }),
  updateOrderStatus: (id, data) => api.put(`/api/admin/orders/${id}/status`, data),
  getReviews: (params) => api.get('/api/admin/reviews', { params }),
};
