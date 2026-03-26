import api from './api';

export const subscriptionService = {
  getPlans: () => api.get('/api/subscriptions/plans'),
  createOrder: (plan) => api.post('/api/subscriptions/create-order', { plan }),
  verify: (data) => api.post('/api/subscriptions/verify', data),
  mySubscription: () => api.get('/api/subscriptions/my'),
};
