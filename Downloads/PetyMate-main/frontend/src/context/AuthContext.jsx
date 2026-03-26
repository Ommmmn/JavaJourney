import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authService } from '../services/authService';
import toast from 'react-hot-toast';

const AuthContext = createContext(null);

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [accessToken, setAccessToken] = useState(localStorage.getItem('petymate_access_token'));
  const [isLoading, setIsLoading] = useState(true);
  const isAuthenticated = !!user;

  const fetchUser = useCallback(async () => {
    try {
      const token = localStorage.getItem('petymate_access_token');
      if (!token) { setIsLoading(false); return; }
      const { data } = await authService.getMe();
      setUser(data.data);
    } catch {
      localStorage.removeItem('petymate_access_token');
      localStorage.removeItem('petymate_refresh_token');
      setUser(null);
      setAccessToken(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => { fetchUser(); }, [fetchUser]);

  const login = async (email, password) => {
    const { data } = await authService.login({ email, password });
    const res = data.data;
    localStorage.setItem('petymate_access_token', res.accessToken);
    localStorage.setItem('petymate_refresh_token', res.refreshToken);
    setAccessToken(res.accessToken);
    setUser(res.user);
    toast.success('Welcome back! 🐾');
    return res;
  };

  const register = async (formData) => {
    const { data } = await authService.register(formData);
    const res = data.data;
    localStorage.setItem('petymate_access_token', res.accessToken);
    localStorage.setItem('petymate_refresh_token', res.refreshToken);
    setAccessToken(res.accessToken);
    setUser(res.user);
    toast.success('Account created! 🎉');
    return res;
  };

  const logout = async () => {
    try { await authService.logout(); } catch { /* silent */ }
    localStorage.removeItem('petymate_access_token');
    localStorage.removeItem('petymate_refresh_token');
    setUser(null);
    setAccessToken(null);
    toast.success('Logged out successfully');
  };

  const updateProfile = async (profileData) => {
    const { data } = await authService.updateMe(profileData);
    setUser(data.data);
    toast.success('Profile updated!');
    return data.data;
  };

  const refreshAccessToken = async () => {
    const rt = localStorage.getItem('petymate_refresh_token');
    if (!rt) throw new Error('No refresh token');
    const { data } = await authService.refresh(rt);
    const newToken = data.data.accessToken;
    localStorage.setItem('petymate_access_token', newToken);
    setAccessToken(newToken);
    return newToken;
  };

  return (
    <AuthContext.Provider value={{
      user, accessToken, isLoading, isAuthenticated,
      login, register, logout, updateProfile, refreshAccessToken, fetchUser, setUser,
    }}>
      {children}
    </AuthContext.Provider>
  );
}
