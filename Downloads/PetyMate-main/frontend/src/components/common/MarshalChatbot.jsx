import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { MessageCircle, X, Send, Bot, User } from 'lucide-react';
import { chatbotService } from '../../services/chatbotService';
import { useAuth } from '../../context/AuthContext';

export default function MarshalChatbot() {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([
    { role: 'bot', text: 'Hi! I\'m Marshal 🐾 — your AI pet assistant. Ask me anything about pet care, nutrition, training, or health!' },
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const chatEndRef = useRef(null);
  const { isAuthenticated } = useAuth();

  useEffect(() => { chatEndRef.current?.scrollIntoView({ behavior: 'smooth' }); }, [messages]);

  const send = async () => {
    if (!input.trim() || loading) return;
    const userMsg = input.trim();
    setInput('');
    setMessages((m) => [...m, { role: 'user', text: userMsg }]);
    setLoading(true);
    try {
      const { data } = await chatbotService.sendMessage(userMsg);
      setMessages((m) => [...m, { role: 'bot', text: data.data?.reply || data.data || 'I couldn\'t understand that. Please try again!' }]);
    } catch {
      setMessages((m) => [...m, { role: 'bot', text: 'Sorry, I\'m having trouble connecting. Please try again later!' }]);
    }
    setLoading(false);
  };

  const handleKeyDown = (e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); send(); } };

  return (
    <>
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            className="fixed bottom-24 right-6 w-[360px] max-w-[calc(100vw-2rem)] h-[500px] bg-white rounded-2xl shadow-2xl border border-pawgray-border flex flex-col z-50 overflow-hidden"
          >
            <div className="bg-gradient-to-r from-primary to-accent px-5 py-4 flex items-center justify-between text-white rounded-t-2xl">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 bg-white/20 rounded-full flex items-center justify-center backdrop-blur-sm">
                  <Bot className="w-5 h-5" />
                </div>
                <div>
                  <p className="font-heading font-bold text-sm">Marshal AI 🐾</p>
                  <p className="text-[10px] text-white/70">Your pet care assistant</p>
                </div>
              </div>
              <button onClick={() => setOpen(false)} className="p-1 hover:bg-white/20 rounded-lg" aria-label="Close chat">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-pawgray/50">
              {messages.map((msg, i) => (
                <div key={i} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-[80%] rounded-2xl px-4 py-2.5 text-sm leading-relaxed ${
                    msg.role === 'user'
                      ? 'bg-primary text-white rounded-br-md'
                      : 'bg-white text-navy border border-pawgray-border rounded-bl-md'
                  }`}>
                    {msg.text}
                  </div>
                </div>
              ))}
              {loading && (
                <div className="flex justify-start">
                  <div className="bg-white text-navy border border-pawgray-border rounded-2xl rounded-bl-md px-4 py-3">
                    <div className="flex gap-1">
                      <span className="w-2 h-2 bg-navy/20 rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                      <span className="w-2 h-2 bg-navy/20 rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                      <span className="w-2 h-2 bg-navy/20 rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                    </div>
                  </div>
                </div>
              )}
              <div ref={chatEndRef} />
            </div>

            <div className="p-3 border-t border-pawgray-border bg-white">
              {!isAuthenticated ? (
                <p className="text-xs text-center text-navy/40 py-2">
                  <a href="/auth/login" className="text-primary font-semibold hover:underline">Login</a> to chat with Marshal
                </p>
              ) : (
                <div className="flex gap-2">
                  <input
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={handleKeyDown}
                    placeholder="Ask Marshal anything..."
                    className="flex-1 px-3 py-2.5 rounded-xl bg-pawgray text-sm text-navy placeholder-navy/30 border-none focus:outline-none focus:ring-2 focus:ring-primary/30"
                    disabled={loading}
                  />
                  <button onClick={send} disabled={loading || !input.trim()}
                    className="p-2.5 bg-primary text-white rounded-xl hover:bg-primary-dark disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                    aria-label="Send message">
                    <Send className="w-4 h-4" />
                  </button>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      <motion.button
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.9 }}
        onClick={() => setOpen(!open)}
        className="fixed bottom-6 right-6 w-14 h-14 bg-primary hover:bg-primary-dark text-white rounded-full shadow-xl flex items-center justify-center z-50 transition-colors"
        aria-label="Open Marshal AI chat"
      >
        {open ? <X className="w-6 h-6" /> : <MessageCircle className="w-6 h-6" />}
      </motion.button>
    </>
  );
}
