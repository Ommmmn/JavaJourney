import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

export function ProtectedRoute({ children }) {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <LoadingSpinner text="Checking authentication..." />;
  if (!isAuthenticated) return <Navigate to="/auth/login" replace />;
  return children;
}

export function AdminRoute({ children }) {
  const { user, isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <LoadingSpinner text="Checking permissions..." />;
  if (!isAuthenticated) return <Navigate to="/auth/login" replace />;
  if (user?.role !== 'ADMIN') return <Navigate to="/" replace />;
  return children;
}

export function GuestRoute({ children }) {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <LoadingSpinner text="Loading..." />;
  if (isAuthenticated) return <Navigate to="/" replace />;
  return children;
}
