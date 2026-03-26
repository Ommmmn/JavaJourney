import api from './api';

export const shopService = {
  getShopPets: (params) => api.get('/api/shop/pets', { params }),
  getShopPetById: (id) => api.get(`/api/shop/pets/${id}`),
};
