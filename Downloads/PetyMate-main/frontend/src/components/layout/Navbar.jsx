import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Menu, X, Bell, ShoppingCart, User, LogOut, PawPrint, ChevronDown, Shield } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import { useNotification } from '../../context/NotificationContext';

const navLinks = [
  { to: '/match', label: 'Find Match' },
  { to: '/shop', label: 'Shop' },
  { to: '/products', label: 'Products' },
  { to: '/vets', label: 'Vets' },
  { to: '/trainers', label: 'Trainers' },
  { to: '/plans', label: 'Plans' },
];

export default function Navbar() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const { user, isAuthenticated, logout } = useAuth();
  const { itemCount } = useCart();
  const { totalUnread } = useNotification();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/');
    setDropdownOpen(false);
  };

  return (
    <nav className="sticky top-0 z-50 bg-white/95 backdrop-blur-md shadow-sm border-b border-pawgray-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2 group">
            <div className="w-9 h-9 bg-primary rounded-xl flex items-center justify-center group-hover:scale-110 transition-transform">
              <PawPrint className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-heading font-bold text-navy">
              Pety<span className="text-primary">Mate</span>
            </span>
          </Link>

          <div className="hidden md:flex items-center gap-1">
            {navLinks.map((l) => (
              <Link key={l.to} to={l.to}
                className="px-3 py-2 text-sm font-medium text-navy/70 hover:text-primary rounded-lg hover:bg-primary/5 transition-all">
                {l.label}
              </Link>
            ))}
          </div>

          <div className="flex items-center gap-3">
            {isAuthenticated && (
              <>
                <button onClick={() => navigate('/profile')} className="relative p-2 text-navy/60 hover:text-primary hover:bg-primary/5 rounded-lg transition-all" aria-label="Notifications">
                  <Bell className="w-5 h-5" />
                  {totalUnread > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                      {totalUnread > 9 ? '9+' : totalUnread}
                    </span>
                  )}
                </button>
                <button onClick={() => navigate('/products')} className="relative p-2 text-navy/60 hover:text-primary hover:bg-primary/5 rounded-lg transition-all" aria-label="Cart">
                  <ShoppingCart className="w-5 h-5" />
                  {itemCount > 0 && (
                    <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-primary text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                      {itemCount > 9 ? '9+' : itemCount}
                    </span>
                  )}
                </button>
              </>
            )}

            {isAuthenticated ? (
              <div className="relative">
                <button onClick={() => setDropdownOpen(!dropdownOpen)}
                  className="flex items-center gap-2 px-3 py-1.5 rounded-pill border border-pawgray-border hover:border-primary/30 hover:bg-primary/5 transition-all"
                  aria-label="User menu">
                  <div className="w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center overflow-hidden">
                    {user?.profilePhotoUrl ? (
                      <img src={user.profilePhotoUrl} alt={user.name} className="w-full h-full object-cover" />
                    ) : (
                      <User className="w-4 h-4 text-primary" />
                    )}
                  </div>
                  <span className="hidden sm:block text-sm font-medium text-navy max-w-[100px] truncate">{user?.name}</span>
                  <ChevronDown className="w-3.5 h-3.5 text-navy/50" />
                </button>
                <AnimatePresence>
                  {dropdownOpen && (
                    <motion.div
                      initial={{ opacity: 0, y: 8, scale: 0.95 }}
                      animate={{ opacity: 1, y: 0, scale: 1 }}
                      exit={{ opacity: 0, y: 8, scale: 0.95 }}
                      transition={{ duration: 0.15 }}
                      className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl border border-pawgray-border py-2 z-50"
                      onMouseLeave={() => setDropdownOpen(false)}
                    >
                      <div className="px-4 py-2 border-b border-pawgray-border">
                        <p className="text-sm font-semibold text-navy truncate">{user?.name}</p>
                        <p className="text-xs text-navy/50 truncate">{user?.email}</p>
                        {user?.subscriptionTier && user.subscriptionTier !== 'FREE' && (
                          <span className={`inline-block mt-1 text-[10px] font-bold px-2 py-0.5 rounded-badge ${user.subscriptionTier === 'PREMIUM' ? 'bg-gradient-to-r from-amber-400 to-orange-500 text-white' : 'bg-blue-100 text-blue-700'}`}>
                            {user.subscriptionTier}
                          </span>
                        )}
                      </div>
                      <Link to="/profile" onClick={() => setDropdownOpen(false)} className="flex items-center gap-3 px-4 py-2.5 text-sm text-navy/70 hover:bg-primary/5 hover:text-primary transition-all">
                        <User className="w-4 h-4" /> My Profile
                      </Link>
                      <Link to="/pets/new" onClick={() => setDropdownOpen(false)} className="flex items-center gap-3 px-4 py-2.5 text-sm text-navy/70 hover:bg-primary/5 hover:text-primary transition-all">
                        <PawPrint className="w-4 h-4" /> Add Pet
                      </Link>
                      {user?.role === 'ADMIN' && (
                        <Link to="/admin" onClick={() => setDropdownOpen(false)} className="flex items-center gap-3 px-4 py-2.5 text-sm text-navy/70 hover:bg-primary/5 hover:text-primary transition-all">
                          <Shield className="w-4 h-4" /> Admin Panel
                        </Link>
                      )}
                      <div className="border-t border-pawgray-border mt-1 pt-1">
                        <button onClick={handleLogout} className="flex items-center gap-3 px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 transition-all w-full">
                          <LogOut className="w-4 h-4" /> Logout
                        </button>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            ) : (
              <div className="hidden md:flex items-center gap-2">
                <Link to="/auth/login" className="px-4 py-2 text-sm font-medium text-navy/70 hover:text-primary transition-colors">Login</Link>
                <Link to="/auth/register" className="px-5 py-2 text-sm font-semibold text-white bg-primary hover:bg-primary-dark rounded-pill shadow-md hover:shadow-lg transition-all">Sign Up</Link>
              </div>
            )}

            <button onClick={() => setMobileOpen(!mobileOpen)} className="md:hidden p-2 text-navy/60 hover:text-primary rounded-lg" aria-label="Toggle menu">
              {mobileOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="md:hidden overflow-hidden bg-white border-t border-pawgray-border"
          >
            <div className="px-4 py-4 space-y-1">
              {navLinks.map((l) => (
                <Link key={l.to} to={l.to} onClick={() => setMobileOpen(false)}
                  className="block px-4 py-3 text-sm font-medium text-navy/70 hover:text-primary hover:bg-primary/5 rounded-lg transition-all">
                  {l.label}
                </Link>
              ))}
              {!isAuthenticated && (
                <div className="pt-3 border-t border-pawgray-border mt-3 space-y-2">
                  <Link to="/auth/login" onClick={() => setMobileOpen(false)} className="block w-full text-center px-4 py-2.5 text-sm font-medium text-primary border border-primary rounded-pill hover:bg-primary/5 transition-all">Login</Link>
                  <Link to="/auth/register" onClick={() => setMobileOpen(false)} className="block w-full text-center px-4 py-2.5 text-sm font-semibold text-white bg-primary rounded-pill hover:bg-primary-dark transition-all">Sign Up</Link>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  );
}
