import api from './api';

export const productService = {
  getProducts: (params) => api.get('/api/products', { params }),
  getProductById: (id) => api.get(`/api/products/${id}`),
  addReview: (id, data) => api.post(`/api/products/${id}/review`, data),
};
