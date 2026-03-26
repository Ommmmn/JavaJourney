import api from './api';

export const petService = {
  createPet: (formData) => api.post('/api/pets', formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
  getPets: (params) => api.get('/api/pets', { params }),
  getPetById: (id) => api.get(`/api/pets/${id}`),
  updatePet: (id, data) => api.put(`/api/pets/${id}`, data),
  deletePet: (id) => api.delete(`/api/pets/${id}`),
  myPets: () => api.get('/api/pets/my'),
  updateStatus: (id, status) => api.put(`/api/pets/${id}/status`, { status }),
  addPhotos: (id, files) => {
    const fd = new FormData();
    files.forEach((f) => fd.append('files', f));
    return api.post(`/api/pets/${id}/photos`, fd, { headers: { 'Content-Type': 'multipart/form-data' } });
  },
  deletePhoto: (petId, photoId) => api.delete(`/api/pets/${petId}/photos/${photoId}`),
};
