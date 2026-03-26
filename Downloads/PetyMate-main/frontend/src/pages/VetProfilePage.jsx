import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { vetService } from '../services/vetService';
import { appointmentService } from '../services/appointmentService';
import { petService } from '../services/petService';
import { useAuth } from '../context/AuthContext';
import StarRating from '../components/common/StarRating';
import RazorpayButton from '../components/common/RazorpayButton';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageTransition from '../components/common/PageTransition';
import { MapPin, Star, BadgeCheck, Clock, Calendar, Phone, Mail, Award } from 'lucide-react';
import toast from 'react-hot-toast';

export default function VetProfilePage() {
  const { vetId } = useParams();
  const { isAuthenticated } = useAuth();
  const [vet, setVet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [myPets, setMyPets] = useState([]);
  const [bookingForm, setBookingForm] = useState({ petId: '', date: '', time: '', mode: 'ONLINE', notes: '' });
  const [bookingCreated, setBookingCreated] = useState(null);

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      try {
        const { data } = await vetService.getVetById(vetId);
        setVet(data.data);
        if (isAuthenticated) {
          const { data: pets } = await petService.myPets();
          setMyPets(pets.data || []);
        }
      } catch { toast.error('Vet not found'); }
      finally { setLoading(false); }
    };
    fetch();
  }, [vetId, isAuthenticated]);

  const handleBooking = async () => {
    if (!bookingForm.petId || !bookingForm.date || !bookingForm.time) {
      toast.error('Please fill all required fields');
      return;
    }
    setBookingCreated(bookingForm);
  };

  const timeSlots = [];
  for (let h = 6; h <= 20; h++) {
    timeSlots.push(`${h.toString().padStart(2, '0')}:00`);
  }

  if (loading) return <LoadingSpinner text="Loading vet profile..." />;
  if (!vet) return <div className="text-center py-20 text-navy/50">Vet not found</div>;

  return (
    <PageTransition>
      <div className="bg-gradient-to-br from-secondary/10 to-teal-50 h-48 relative" />
      <div className="max-w-5xl mx-auto px-4 -mt-16 pb-16">
        <div className="flex items-end gap-5 mb-8">
          <div className="relative">
            <div className="w-28 h-28 rounded-2xl border-4 border-white shadow-lg overflow-hidden bg-white">
              <img src={vet.photoUrl || 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200'} alt={vet.name} className="w-full h-full object-cover" />
            </div>
            {vet.isVerified && (
              <div className="absolute -bottom-1 -right-1 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center border-2 border-white">
                <BadgeCheck className="w-5 h-5 text-white" />
              </div>
            )}
          </div>
          <div className="pb-2">
            <h1 className="font-heading font-extrabold text-2xl text-navy">Dr. {vet.name}</h1>
            <p className="text-navy/60">{vet.specialization}</p>
            <p className="text-sm text-navy/40">{vet.qualification}</p>
          </div>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {[
            { l: 'Rating', v: <div className="flex items-center gap-1"><Star className="w-4 h-4 text-amber-400 fill-current" />{vet.rating || '0.0'}</div> },
            { l: 'Experience', v: `${vet.experienceYears} years` },
            { l: 'Appointments', v: vet.totalAppointments || 0 },
            { l: 'Fee', v: `₹${vet.consultationFee}` },
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
              <p className="text-sm text-navy/60 leading-relaxed">{vet.bio || 'Experienced veterinarian dedicated to pet health and wellness.'}</p>
            </div>
            <div className="bg-white rounded-card border border-pawgray-border p-6">
              <h2 className="font-heading font-bold text-navy text-lg mb-3">Details</h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div className="flex items-center gap-2 text-navy/60"><MapPin className="w-4 h-4 text-secondary" /> {vet.city}, {vet.state}</div>
                <div className="flex items-center gap-2 text-navy/60"><Clock className="w-4 h-4 text-secondary" /> {vet.availableHours || '9 AM - 6 PM'}</div>
                <div className="flex items-center gap-2 text-navy/60"><Calendar className="w-4 h-4 text-secondary" /> {vet.availableDays || 'Mon - Sat'}</div>
                <div className="flex items-center gap-2 text-navy/60"><Award className="w-4 h-4 text-secondary" /> {vet.reviewCount || 0} reviews</div>
              </div>
            </div>
          </div>

          <div>
            <div className="sticky top-20 bg-white rounded-card border border-pawgray-border p-6">
              <h3 className="font-heading font-bold text-navy text-lg mb-4">Book Appointment</h3>
              {isAuthenticated ? (
                <div className="space-y-4">
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Your Pet</label>
                    <select value={bookingForm.petId} onChange={(e) => setBookingForm((f) => ({ ...f, petId: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      <option value="">Select pet...</option>
                      {myPets.map((p) => <option key={p.id} value={p.id}>{p.name} ({p.species})</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Date</label>
                    <input type="date" value={bookingForm.date} min={new Date(Date.now() + 86400000).toISOString().split('T')[0]}
                      onChange={(e) => setBookingForm((f) => ({ ...f, date: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30" />
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Time</label>
                    <select value={bookingForm.time} onChange={(e) => setBookingForm((f) => ({ ...f, time: e.target.value }))}
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
                      <option value="">Select time...</option>
                      {timeSlots.map((t) => <option key={t} value={t}>{t}</option>)}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Mode</label>
                    <div className="flex gap-2">
                      {['ONLINE', 'CLINIC'].map((m) => (
                        <button key={m} type="button" onClick={() => setBookingForm((f) => ({ ...f, mode: m }))}
                          className={`flex-1 py-2 text-xs font-semibold rounded-lg transition-all ${bookingForm.mode === m ? 'bg-secondary text-white' : 'bg-pawgray text-navy/60'}`}>
                          {m === 'ONLINE' ? '💻 Online' : '🏥 Clinic'}
                        </button>
                      ))}
                    </div>
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Notes</label>
                    <textarea value={bookingForm.notes} onChange={(e) => setBookingForm((f) => ({ ...f, notes: e.target.value }))}
                      placeholder="Describe symptoms or concerns..."
                      className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm h-20 resize-none focus:outline-none focus:ring-2 focus:ring-secondary/30" />
                  </div>
                  <div className="bg-secondary/5 rounded-xl p-3 text-center">
                    <p className="text-sm text-navy/50">Consultation Fee</p>
                    <p className="text-xl font-bold text-secondary">₹{vet.consultationFee}</p>
                  </div>
                  <RazorpayButton
                    label={`Book & Pay ₹${vet.consultationFee}`}
                    description="Vet Appointment - PetyMate"
                    createOrderFn={() => appointmentService.createOrder(bookingForm)}
                    verifyFn={(data) => appointmentService.verify(data)}
                    onSuccess={() => toast.success('Appointment booked! 🩺')}
                    className="w-full bg-secondary hover:bg-secondary-dark"
                  />
                </div>
              ) : (
                <div className="text-center py-6">
                  <p className="text-sm text-navy/50 mb-3">Login to book an appointment</p>
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
