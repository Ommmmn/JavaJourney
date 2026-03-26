import api from './api';

export const appointmentService = {
  createOrder: (data) => api.post('/api/appointments/create-order', data),
  verify: (data) => api.post('/api/appointments/verify', data),
  myAppointments: (params) => api.get('/api/appointments/my', { params }),
  cancel: (id) => api.put(`/api/appointments/${id}/cancel`),
};
