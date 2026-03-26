import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { motion } from 'framer-motion';
import { Mail, Lock, Eye, EyeOff, PawPrint } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import PageTransition from '../components/common/PageTransition';

const schema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(1, 'Password is required'),
});

export default function LoginPage() {
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(schema) });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      await login(data.email, data.password);
      navigate('/profile');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageTransition>
      <div className="min-h-screen flex">
        <div className="hidden md:flex md:w-1/2 gradient-hero relative items-center justify-center p-12">
          <div className="text-center text-white relative z-10">
            <div className="w-20 h-20 bg-white/20 rounded-2xl flex items-center justify-center mx-auto mb-6 backdrop-blur-sm">
              <PawPrint className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-4xl font-heading font-extrabold mb-3">PetyMate</h2>
            <p className="text-lg text-white/80 mb-10">Your Pet's World, Connected</p>
            <div className="flex justify-center gap-6">
              {['50K+ Pets', 'Verified Vets', 'Secure Payments'].map((t) => (
                <div key={t} className="px-4 py-2 rounded-pill bg-white/15 backdrop-blur-sm text-sm font-medium">{t}</div>
              ))}
            </div>
            <div className="mt-12 flex items-center justify-center gap-4">
              <svg className="w-16 h-16 text-white/30" viewBox="0 0 100 80" fill="currentColor">
                <ellipse cx="30" cy="40" rx="20" ry="25"/><circle cx="20" cy="15" r="8"/><circle cx="40" cy="15" r="8"/>
              </svg>
              <span className="text-4xl">❤️</span>
              <svg className="w-16 h-16 text-white/30 -scale-x-100" viewBox="0 0 100 80" fill="currentColor">
                <ellipse cx="30" cy="40" rx="20" ry="25"/><circle cx="20" cy="15" r="8"/><circle cx="40" cy="15" r="8"/>
              </svg>
            </div>
          </div>
        </div>

        <div className="flex-1 flex items-center justify-center p-6 md:p-12 bg-white">
          <div className="w-full max-w-md">
            <div className="md:hidden flex items-center gap-2 mb-8">
              <div className="w-9 h-9 bg-primary rounded-xl flex items-center justify-center">
                <PawPrint className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-heading font-bold text-navy">Pety<span className="text-primary">Mate</span></span>
            </div>

            <h1 className="text-2xl font-heading font-extrabold text-navy mb-2">Welcome Back 🐾</h1>
            <p className="text-sm text-navy/50 mb-8">Sign in to find your pet's perfect match</p>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              <div>
                <label className="block text-sm font-medium text-navy mb-1.5">Email</label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                  <input {...register('email')} type="email" placeholder="you@email.com"
                    className="w-full pl-10 pr-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all" />
                </div>
                {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium text-navy mb-1.5">Password</label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                  <input {...register('password')} type={showPass ? 'text' : 'password'} placeholder="Enter your password"
                    className="w-full pl-10 pr-12 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all" />
                  <button type="button" onClick={() => setShowPass(!showPass)} className="absolute right-3 top-1/2 -translate-y-1/2 text-navy/30 hover:text-navy/60" aria-label="Toggle password">
                    {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
                {errors.password && <p className="text-xs text-red-500 mt-1">{errors.password.message}</p>}
              </div>

              <button type="submit" disabled={loading}
                className="w-full py-3 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-all disabled:opacity-60 flex items-center justify-center gap-2">
                {loading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : null}
                {loading ? 'Signing In...' : 'Sign In'}
              </button>
            </form>

            <div className="mt-6 flex items-center gap-3">
              <div className="flex-1 h-px bg-pawgray-border" />
              <span className="text-xs text-navy/30">or</span>
              <div className="flex-1 h-px bg-pawgray-border" />
            </div>

            <button disabled className="mt-4 w-full py-3 border border-pawgray-border rounded-xl text-sm text-navy/40 font-medium cursor-not-allowed">
              Continue with Google — Coming Soon
            </button>

            <p className="mt-8 text-sm text-center text-navy/50">
              New to PetyMate?{' '}
              <Link to="/auth/register" className="text-primary font-semibold hover:underline">Create account</Link>
            </p>
          </div>
        </div>
      </div>
    </PageTransition>
  );
}
