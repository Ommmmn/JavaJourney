import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { trainerService } from '../services/trainerService';
import { trainingService } from '../services/trainingService';
import { petService } from '../services/petService';
import { useAuth } from '../context/AuthContext';
import StarRating from '../components/common/StarRating';
import RazorpayButton from '../components/common/RazorpayButton';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageTransition from '../components/common/PageTransition';
import { MapPin, Star, BadgeCheck, Clock, Home, Monitor, Users, Building2, Award, Package } from 'lucide-react';
import toast from 'react-hot-toast';

const modeIcons = { HOME_VISIT: Home, ONLINE: Monitor, GROUP_CLASS: Users, TRAINING_CENTER: Building2 };

export default function TrainerProfilePage() {
  const { trainerId } = useParams();
  const { isAuthenticated } = useAuth();
  const [trainer, setTrainer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [myPets, setMyPets] = useState([]);
  const [form, setForm] = useState({ petId: '', date: '', time: '', durationHours: 1, mode: 'HOME_VISIT', focusArea: 'Obedience', petCurrentIssues: '' });

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      try {
        const { data } = await trainerService.getTrainerById(trainerId);
        setTrainer(data.data);
        if (isAuthenticated) {
          const { data: pets } = await petService.myPets();
          setMyPets(pets.data || []);
        }
      } catch { toast.error('Trainer not found'); }
      finally { setLoading(false); }
    };
    fetch();
  }, [trainerId, isAuthenticated]);

  const timeSlots = [];
  for (let h = 6; h <= 20; h++) timeSlots.push(`${h.toString().padStart(2, '0')}:00`);
  const totalFee = form.durationHours * (trainer?.sessionFeePerHour || 0);
  const modes = trainer?.sessionModes?.split(',').map((s) => s.trim()) || [];

  if (loading) return <LoadingSpinner text="Loading trainer profile..." />;
  if (!trainer) return <div className="text-center py-20 text-navy/50">Trainer not found</div>;

  return (
    <PageTransition>
      <div className="bg-gradient-to-br from-secondary/10 to-emerald-50 h-48 relative" />
      <div className="max-w-5xl mx-auto px-4 -mt-16 pb-16">
        <div className="flex items-end gap-5 mb-8">
          <div className="relative">
            <div className="w-28 h-28 rounded-full border-4 border-white shadow-lg overflow-hidden bg-white">
              <img src={trainer.photoUrl || 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=200'} alt={trainer.name} className="w-full h-full object-cover" />
            </div>
            {trainer.isVerified && (
              <div className="absolute bottom-0 right-0 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center border-2 border-white">
                <BadgeCheck className="w-5 h-5 text-white" />
              </div>
            )}
          </div>
          <div className="pb-2">
            <h1 className="font-heading font-extrabold text-2xl text-navy">{trainer.name}</h1>
            <p className="text-navy/60">{trainer.specialization?.replace(/_/g, ' ')}</p>
            <div className="flex items-center gap-1 text-sm text-navy/40 mt-1"><MapPin className="w-3.5 h-3.5" /> {trainer.city}, {trainer.state}</div>
          </div>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {[
            { l: 'Total Sessions', v: trainer.totalSessions || 0 },
            { l: 'Rating', v: <div className="flex items-center justify-center gap-1"><Star className="w-4 h-4 text-amber-400 fill-current" />{trainer.rating || '0.0'}</div> },
            { l: 'Experience', v: `${trainer.experienceYears} years` },
            { l: 'Fee/Hour', v: `₹${trainer.sessionFeePerHour}` },
          ].map((s) => (
            <div key={s.l} className="bg-white rounded-card p-4 text-center border border-pawgray-border">
              <p className="text-xs text-navy/40 mb-1">{s.l}</p>
              <p className="text-lg font-bold text-navy">{s.v}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-card border border-pawgray-border p-6">
              <h2 className="font-heading font-bold text-navy text-lg mb-3">About</h2>
              <p className="text-sm text-navy/60 leading-relaxed">{trainer.bio || 'Experienced pet trainer dedicated to helping pets reach their best potential.'}</p>
              {trainer.certification && <p className="text-sm text-navy/40 mt-3">📜 Certifications: {trainer.certification}</p>}
            </div>

            <div className="bg-white rounded-card border border-pawgray-border p-6">
              <h2 className="font-heading font-bold text-navy text-lg mb-3">Session Modes</h2>
              <div className="grid grid-cols-2 gap-3">
                {modes.map((m) => {
                  const Icon = modeIcons[m] || Monitor;
                  return (
                    <div key={m} className="flex items-center gap-3 bg-secondary/5 rounded-xl p-3">
                      <Icon className="w-5 h-5 text-secondary" />
                      <span className="text-sm font-medium text-navy">{m.replace(/_/g, ' ')}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>

          <div>
            <div className="sticky top-20 bg-white rounded-card border border-pawgray-border p-6">
              <h3 className="font-heading font-bold text-navy text-lg mb-4">Book Session</h3>
              {isAuthenticated ? (
                <div className="space-y-4">
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Your Pet</label>
                    <select value={form.petId} onChange={(e) => setForm((f) => ({ ...f, petId: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      <option value="">Select pet...</option>
                      {myPets.map((p) => <option key={p.id} value={p.id}>{p.name} ({p.species})</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Date</label>
                    <input type="date" value={form.date} min={new Date(Date.now() + 86400000).toISOString().split('T')[0]}
                      onChange={(e) => setForm((f) => ({ ...f, date: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30" />
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Time</label>
                    <select value={form.time} onChange={(e) => setForm((f) => ({ ...f, time: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      <option value="">Select time...</option>
                      {timeSlots.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Duration</label>
                    <div className="flex gap-2">
                      {[1, 2, 3].map((h) => (
                        <button key={h} type="button" onClick={() => setForm((f) => ({ ...f, durationHours: h }))}
                          className={`flex-1 py-2 text-sm font-semibold rounded-lg transition-all ${form.durationHours === h ? 'bg-secondary text-white' : 'bg-pawgray text-navy/60'}`}>
                          {h}h
                        </button>
                      ))}
                    </div>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Mode</label>
                    <select value={form.mode} onChange={(e) => setForm((f) => ({ ...f, mode: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      {modes.map((m) => <option key={m} value={m}>{m.replace(/_/g, ' ')}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Focus Area</label>
                    <select value={form.focusArea} onChange={(e) => setForm((f) => ({ ...f, focusArea: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      {['Obedience','Potty Training','Aggression','Leash Walking','Socialization','Commands','Tricks','Other'].map((a) => (
                        <option key={a} value={a}>{a}</option>
                      ))}
                    </select>
                  </div>
                  <div className="bg-secondary/5 rounded-xl p-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-navy/50">{form.durationHours}h × ₹{trainer.sessionFeePerHour}/hr</span>
                      <span className="font-bold text-secondary text-lg">₹{totalFee}</span>
                    </div>
                  </div>
                  <RazorpayButton
                    label={`Book & Pay ₹${totalFee}`}
                    description="Training Session - PetyMate"
                    createOrderFn={() => trainingService.createSessionOrder({ ...form, trainerId: trainer.id })}
                    verifyFn={(data) => trainingService.verifySession(data)}
                    onSuccess={() => toast.success('Session booked! 🎓')}
                    className="w-full bg-secondary hover:bg-secondary-dark"
                  />
                </div>
              ) : (
                <div className="text-center py-6">
                  <p className="text-sm text-navy/50 mb-3">Login to book a session</p>
                  <a href="/auth/login" className="px-6 py-2.5 bg-secondary text-white font-semibold rounded-xl inline-block hover:bg-secondary-dark">Login</a>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </PageTransition>
  );
}
