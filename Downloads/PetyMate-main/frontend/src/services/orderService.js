import api from './api';

export const orderService = {
  createCartOrder: (data) => api.post('/api/orders/cart/create-order', data),
  verifyCartOrder: (data) => api.post('/api/orders/cart/verify', data),
  myOrders: (params) => api.get('/api/orders/my', { params }),
  getOrder: (id) => api.get(`/api/orders/${id}`),
};
