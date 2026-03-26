import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { matchService } from '../services/matchService';
import { useAuth } from './AuthContext';

const NotificationContext = createContext(null);

export const useNotification = () => {
  const ctx = useContext(NotificationContext);
  if (!ctx) throw new Error('useNotification must be used within NotificationProvider');
  return ctx;
};

export function NotificationProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [matchRequestCount, setMatchRequestCount] = useState(0);
  const [totalUnread, setTotalUnread] = useState(0);

  const fetchNotifications = useCallback(async () => {
    if (!isAuthenticated) return;
    try {
      const { data } = await matchService.getReceived({ status: 'PENDING', page: 0, size: 1 });
      const count = data.data?.totalElements || 0;
      setMatchRequestCount(count);
      setTotalUnread(count);
    } catch { /* silent */ }
  }, [isAuthenticated]);

  useEffect(() => {
    fetchNotifications();
    if (!isAuthenticated) return;
    const interval = setInterval(fetchNotifications, 60000);
    return () => clearInterval(interval);
  }, [fetchNotifications, isAuthenticated]);

  return (
    <NotificationContext.Provider value={{ matchRequestCount, totalUnread, fetchNotifications }}>
      {children}
    </NotificationContext.Provider>
  );
}
