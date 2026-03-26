import api from './api';

export const trainerService = {
  getTrainers: (params) => api.get('/api/trainers', { params }),
  getTrainerById: (id) => api.get(`/api/trainers/${id}`),
  addReview: (id, data) => api.post(`/api/trainers/${id}/review`, data),
};
