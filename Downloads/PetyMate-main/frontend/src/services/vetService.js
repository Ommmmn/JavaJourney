import api from './api';

export const vetService = {
  getVets: (params) => api.get('/api/vets', { params }),
  getVetById: (id) => api.get(`/api/vets/${id}`),
  addReview: (id, data) => api.post(`/api/vets/${id}/review`, data),
};
