import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Mail, Lock, User, Phone, MapPin, Eye, EyeOff, PawPrint, Upload, ArrowLeft, ArrowRight } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import toast from 'react-hot-toast';
import PageTransition from '../components/common/PageTransition';

const indianStates = ['Andhra Pradesh','Arunachal Pradesh','Assam','Bihar','Chhattisgarh','Delhi','Goa','Gujarat','Haryana','Himachal Pradesh','Jharkhand','Karnataka','Kerala','Madhya Pradesh','Maharashtra','Manipur','Meghalaya','Mizoram','Nagaland','Odisha','Punjab','Rajasthan','Sikkim','Tamil Nadu','Telangana','Tripura','Uttar Pradesh','Uttarakhand','West Bengal'];

const step1Schema = z.object({
  name: z.string().min(1, 'Name is required').max(100),
  email: z.string().email('Invalid email'),
  phone: z.string().regex(/^[0-9]{10}$/, 'Phone must be 10 digits'),
  password: z.string().min(8, 'Min 8 characters'),
  confirmPassword: z.string(),
}).refine((d) => d.password === d.confirmPassword, { message: 'Passwords must match', path: ['confirmPassword'] });

const step2Schema = z.object({
  city: z.string().min(1, 'City is required'),
  state: z.string().min(1, 'State is required'),
  pincode: z.string().regex(/^[0-9]{6}$/, 'Must be 6 digits'),
  agreeTerms: z.literal(true, { errorMap: () => ({ message: 'You must agree to terms' }) }),
});

export default function RegisterPage() {
  const [step, setStep] = useState(1);
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading] = useState(false);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [photoFile, setPhotoFile] = useState(null);
  const [step1Data, setStep1Data] = useState(null);
  const { register: authRegister } = useAuth();
  const navigate = useNavigate();

  const form1 = useForm({ resolver: zodResolver(step1Schema) });
  const form2 = useForm({ resolver: zodResolver(step2Schema), defaultValues: { agreeTerms: false } });

  const onStep1 = (data) => { setStep1Data(data); setStep(2); };

  const onStep2 = async (data) => {
    setLoading(true);
    try {
      const payload = { ...step1Data, ...data };
      delete payload.confirmPassword;
      delete payload.agreeTerms;
      await authRegister(payload);
      if (photoFile) {
        try { await authService.uploadPhoto(photoFile); } catch { /* silent */ }
      }
      navigate('/profile');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handlePhotoChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setPhotoFile(file);
    const reader = new FileReader();
    reader.onload = (ev) => setPhotoPreview(ev.target.result);
    reader.readAsDataURL(file);
  };

  return (
    <PageTransition>
      <div className="min-h-screen flex">
        <div className="hidden md:flex md:w-1/2 gradient-hero relative items-center justify-center p-12">
          <div className="text-center text-white relative z-10">
            <div className="w-20 h-20 bg-white/20 rounded-2xl flex items-center justify-center mx-auto mb-6 backdrop-blur-sm">
              <PawPrint className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-4xl font-heading font-extrabold mb-3">Join PetyMate</h2>
            <p className="text-lg text-white/80 mb-10">Connect with pet lovers across India</p>
            <div className="flex justify-center gap-6">
              {['50K+ Pets', 'Verified Vets', 'Secure Payments'].map((t) => (
                <div key={t} className="px-4 py-2 rounded-pill bg-white/15 backdrop-blur-sm text-sm font-medium">{t}</div>
              ))}
            </div>
          </div>
        </div>

        <div className="flex-1 flex items-center justify-center p-6 md:p-12 bg-white overflow-y-auto">
          <div className="w-full max-w-md">
            <div className="md:hidden flex items-center gap-2 mb-6">
              <div className="w-9 h-9 bg-primary rounded-xl flex items-center justify-center">
                <PawPrint className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-heading font-bold text-navy">Pety<span className="text-primary">Mate</span></span>
            </div>

            <h1 className="text-2xl font-heading font-extrabold text-navy mb-2">Create Account 🐾</h1>
            <p className="text-sm text-navy/50 mb-6">Join thousands of pet owners</p>

            <div className="flex items-center gap-3 mb-8">
              {[1, 2].map((s) => (
                <div key={s} className="flex items-center gap-2 flex-1">
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step >= s ? 'bg-primary text-white' : 'bg-pawgray text-navy/30'}`}>{s}</div>
                  <span className={`text-xs font-medium ${step >= s ? 'text-navy' : 'text-navy/30'}`}>{s === 1 ? 'Account' : 'Location'}</span>
                  {s < 2 && <div className={`flex-1 h-0.5 ${step > s ? 'bg-primary' : 'bg-pawgray-border'}`} />}
                </div>
              ))}
            </div>

            {step === 1 ? (
              <form onSubmit={form1.handleSubmit(onStep1)} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Full Name</label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                    <input {...form1.register('name')} placeholder="Your full name"
                      className="w-full pl-10 pr-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  </div>
                  {form1.formState.errors.name && <p className="text-xs text-red-500 mt-1">{form1.formState.errors.name.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Email</label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                    <input {...form1.register('email')} type="email" placeholder="you@email.com"
                      className="w-full pl-10 pr-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  </div>
                  {form1.formState.errors.email && <p className="text-xs text-red-500 mt-1">{form1.formState.errors.email.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Phone</label>
                  <div className="relative">
                    <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                    <input {...form1.register('phone')} placeholder="10-digit number"
                      className="w-full pl-10 pr-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  </div>
                  {form1.formState.errors.phone && <p className="text-xs text-red-500 mt-1">{form1.formState.errors.phone.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Password</label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                    <input {...form1.register('password')} type={showPass ? 'text' : 'password'} placeholder="Min 8 characters"
                      className="w-full pl-10 pr-12 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                    <button type="button" onClick={() => setShowPass(!showPass)} className="absolute right-3 top-1/2 -translate-y-1/2 text-navy/30 hover:text-navy/60" aria-label="Toggle password">
                      {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {form1.formState.errors.password && <p className="text-xs text-red-500 mt-1">{form1.formState.errors.password.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Confirm Password</label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
                    <input {...form1.register('confirmPassword')} type="password" placeholder="Confirm password"
                      className="w-full pl-10 pr-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  </div>
                  {form1.formState.errors.confirmPassword && <p className="text-xs text-red-500 mt-1">{form1.formState.errors.confirmPassword.message}</p>}
                </div>
                <button type="submit" className="w-full py-3 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-all flex items-center justify-center gap-2">
                  Next <ArrowRight className="w-4 h-4" />
                </button>
              </form>
            ) : (
              <form onSubmit={form2.handleSubmit(onStep2)} className="space-y-4">
                <div className="flex justify-center">
                  <label className="cursor-pointer group">
                    <div className="w-24 h-24 rounded-full bg-pawgray border-2 border-dashed border-pawgray-border overflow-hidden flex items-center justify-center group-hover:border-primary transition-colors">
                      {photoPreview ? (
                        <img src={photoPreview} alt="Profile preview" className="w-full h-full object-cover" />
                      ) : (
                        <Upload className="w-8 h-8 text-navy/20" />
                      )}
                    </div>
                    <input type="file" accept="image/*" className="hidden" onChange={handlePhotoChange} />
                    <p className="text-xs text-center text-navy/40 mt-2">Upload photo</p>
                  </label>
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">City</label>
                  <input {...form2.register('city')} placeholder="Your city"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  {form2.formState.errors.city && <p className="text-xs text-red-500 mt-1">{form2.formState.errors.city.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">State</label>
                  <select {...form2.register('state')} className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary">
                    <option value="">Select state</option>
                    {indianStates.map((s) => <option key={s} value={s}>{s}</option>)}
                  </select>
                  {form2.formState.errors.state && <p className="text-xs text-red-500 mt-1">{form2.formState.errors.state.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-navy mb-1">Pincode</label>
                  <input {...form2.register('pincode')} placeholder="6-digit pincode"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  {form2.formState.errors.pincode && <p className="text-xs text-red-500 mt-1">{form2.formState.errors.pincode.message}</p>}
                </div>
                <label className="flex items-start gap-2 cursor-pointer">
                  <input type="checkbox" {...form2.register('agreeTerms')} className="mt-1 accent-primary" />
                  <span className="text-xs text-navy/60">I agree to PetyMate's <Link to="#" className="text-primary underline">Terms</Link> & <Link to="#" className="text-primary underline">Privacy Policy</Link></span>
                </label>
                {form2.formState.errors.agreeTerms && <p className="text-xs text-red-500">{form2.formState.errors.agreeTerms.message}</p>}

                <div className="flex gap-3">
                  <button type="button" onClick={() => setStep(1)}
                    className="flex-1 py-3 border border-pawgray-border text-navy/60 font-medium rounded-xl hover:bg-pawgray transition-all flex items-center justify-center gap-2">
                    <ArrowLeft className="w-4 h-4" /> Back
                  </button>
                  <button type="submit" disabled={loading}
                    className="flex-1 py-3 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-all disabled:opacity-60 flex items-center justify-center gap-2">
                    {loading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : null}
                    {loading ? 'Creating...' : 'Create Account'}
                  </button>
                </div>
              </form>
            )}

            <p className="mt-6 text-sm text-center text-navy/50">
              Already have an account?{' '}
              <Link to="/auth/login" className="text-primary font-semibold hover:underline">Sign in</Link>
            </p>
          </div>
        </div>
      </div>
    </PageTransition>
  );
}
