import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { AnimatePresence } from 'framer-motion';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { NotificationProvider } from './context/NotificationContext';

import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import MarshalChatbot from './components/common/MarshalChatbot';
import { ProtectedRoute, AdminRoute, GuestRoute } from './components/common/RouteGuards';

import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MatchPage from './pages/MatchPage';
import PetProfilePage from './pages/PetProfilePage';
import ShopPage from './pages/ShopPage';
import ShopPetDetailPage from './pages/ShopPetDetailPage';
import ProductsPage from './pages/ProductsPage';
import VetsPage from './pages/VetsPage';
import VetProfilePage from './pages/VetProfilePage';
import TrainersPage from './pages/TrainersPage';
import TrainerProfilePage from './pages/TrainerProfilePage';
import AddPetPage from './pages/AddPetPage';
import UserDashboardPage from './pages/UserDashboardPage';
import PlansPage from './pages/PlansPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import NotFoundPage from './pages/NotFoundPage';

function AppContent() {
  const location = useLocation();
  const isAuthPage = location.pathname.startsWith('/auth');
  const isAdminPage = location.pathname.startsWith('/admin');

  return (
    <div className="flex flex-col min-h-screen">
      {!isAuthPage && <Navbar />}
      <main className="flex-1">
        <AnimatePresence mode="wait">
          <Routes location={location} key={location.pathname}>
            <Route path="/" element={<HomePage />} />

            <Route path="/auth/login" element={<GuestRoute><LoginPage /></GuestRoute>} />
            <Route path="/auth/register" element={<GuestRoute><RegisterPage /></GuestRoute>} />

            <Route path="/match" element={<MatchPage />} />
            <Route path="/match/:petId" element={<PetProfilePage />} />

            <Route path="/shop" element={<ShopPage />} />
            <Route path="/shop/:petId" element={<ShopPetDetailPage />} />

            <Route path="/products" element={<ProductsPage />} />

            <Route path="/vets" element={<VetsPage />} />
            <Route path="/vets/:vetId" element={<VetProfilePage />} />

            <Route path="/trainers" element={<TrainersPage />} />
            <Route path="/trainers/:trainerId" element={<TrainerProfilePage />} />

            <Route path="/plans" element={<PlansPage />} />

            <Route path="/pets/new" element={<ProtectedRoute><AddPetPage /></ProtectedRoute>} />

            <Route path="/profile" element={<ProtectedRoute><UserDashboardPage /></ProtectedRoute>} />

            <Route path="/admin" element={<AdminRoute><AdminDashboardPage /></AdminRoute>} />

            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </AnimatePresence>
      </main>
      {!isAuthPage && !isAdminPage && <Footer />}
      <MarshalChatbot />
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <CartProvider>
          <NotificationProvider>
            <AppContent />
            <Toaster
              position="top-right"
              toastOptions={{
                duration: 3000,
                style: {
                  background: '#1A1F36',
                  color: '#fff',
                  borderRadius: '12px',
                  fontSize: '14px',
                  fontFamily: 'Inter, sans-serif',
                },
                success: { iconTheme: { primary: '#FF6B35', secondary: '#fff' } },
                error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
              }}
            />
          </NotificationProvider>
        </CartProvider>
      </AuthProvider>
    </Router>
  );
}
