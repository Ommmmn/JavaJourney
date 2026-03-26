import api from './api';

export const chatbotService = {
  sendMessage: (sessionToken, message, userId) =>
    api.post('/api/chatbot/message', { sessionToken, message, userId }),
};
