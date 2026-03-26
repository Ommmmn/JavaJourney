import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { petService } from '../services/petService';
import { matchService } from '../services/matchService';
import { orderService } from '../services/orderService';
import { appointmentService } from '../services/appointmentService';
import { trainingService } from '../services/trainingService';
import { subscriptionService } from '../services/subscriptionService';
import { authService } from '../services/authService';
import PetCard from '../components/common/PetCard';
import SubscriptionBadge from '../components/common/SubscriptionBadge';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';
import { User, PawPrint, Heart, ShoppingBag, Calendar, GraduationCap, Crown, Star, Plus, Check, X as XIcon, Upload } from 'lucide-react';
import toast from 'react-hot-toast';

const tabs = [
  { key: 'profile', label: 'My Profile', icon: User },
  { key: 'pets', label: 'My Pets', icon: PawPrint },
  { key: 'matches', label: 'Matches', icon: Heart },
  { key: 'orders', label: 'Orders', icon: ShoppingBag },
  { key: 'appointments', label: 'Appointments', icon: Calendar },
  { key: 'training', label: 'Training', icon: GraduationCap },
  { key: 'subscription', label: 'Subscription', icon: Crown },
  { key: 'reviews', label: 'Reviews', icon: Star },
];

export default function UserDashboardPage() {
  const { user, updateProfile, setUser } = useAuth();
  const [activeTab, setActiveTab] = useState('profile');
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState(null);
  const [profileForm, setProfileForm] = useState({
    name: user?.name || '', phone: user?.phone || '', city: user?.city || '', state: user?.state || '', pincode: user?.pincode || '',
  });
  const [matchSubTab, setMatchSubTab] = useState('received');

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        let result = null;
        switch (activeTab) {
          case 'pets': result = (await petService.myPets()).data.data; break;
          case 'matches':
            result = matchSubTab === 'received'
              ? (await matchService.getReceived({ page: 0, size: 20 })).data.data
              : (await matchService.getSent({ page: 0, size: 20 })).data.data;
            break;
          case 'orders': result = (await orderService.myOrders({ page: 0, size: 20 })).data.data; break;
          case 'appointments': result = (await appointmentService.myAppointments({ page: 0, size: 20 })).data.data; break;
          case 'training': result = (await trainingService.mySessions({ page: 0, size: 20 })).data.data; break;
          case 'subscription': result = (await subscriptionService.mySubscription()).data.data; break;
          default: break;
        }
        setData(result);
      } catch { setData(null); }
      finally { setLoading(false); }
    };
    if (activeTab !== 'profile' && activeTab !== 'reviews') fetchData();
  }, [activeTab, matchSubTab]);

  const handleProfileSave = async () => {
    try {
      await updateProfile(profileForm);
    } catch (err) { toast.error('Failed to update profile'); }
  };

  const handlePhotoUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    try {
      const { data: res } = await authService.uploadPhoto(file);
      setUser((prev) => ({ ...prev, profilePhotoUrl: res.data?.profilePhotoUrl }));
      toast.success('Photo updated!');
    } catch { toast.error('Failed to upload photo'); }
  };

  const handleMatchAction = async (id, action) => {
    try {
      if (action === 'accept') await matchService.accept(id);
      else await matchService.reject(id);
      toast.success(action === 'accept' ? 'Request accepted! 💕' : 'Request rejected');
      setData((prev) => ({
        ...prev, content: prev.content?.map((m) => m.id === id ? { ...m, status: action === 'accept' ? 'ACCEPTED' : 'REJECTED' } : m)
      }));
    } catch (err) { toast.error('Action failed'); }
  };

  const statusColors = { PENDING: 'bg-amber-100 text-amber-700', ACCEPTED: 'bg-green-100 text-green-700', REJECTED: 'bg-red-100 text-red-700',
    PAID: 'bg-blue-100 text-blue-700', PROCESSING: 'bg-indigo-100 text-indigo-700', SHIPPED: 'bg-purple-100 text-purple-700', DELIVERED: 'bg-green-100 text-green-700', CANCELLED: 'bg-red-100 text-red-700', CONFIRMED: 'bg-green-100 text-green-700', COMPLETED: 'bg-teal-100 text-teal-700' };

  return (
    <PageTransition>
      <div className="bg-gradient-to-br from-primary/5 to-accent/5 py-8">
        <div className="max-w-5xl mx-auto px-4 flex items-center gap-5">
          <label className="cursor-pointer group relative">
            <div className="w-20 h-20 rounded-2xl overflow-hidden border-3 border-white shadow-lg bg-pawgray">
              {user?.profilePhotoUrl ? <img src={user.profilePhotoUrl} alt={user.name} className="w-full h-full object-cover" /> : <User className="w-10 h-10 text-navy/20 m-auto mt-5" />}
            </div>
            <div className="absolute inset-0 rounded-2xl bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
              <Upload className="w-5 h-5 text-white" />
            </div>
            <input type="file" accept="image/*" className="hidden" onChange={handlePhotoUpload} />
          </label>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="font-heading font-extrabold text-2xl text-navy">{user?.name}</h1>
              <SubscriptionBadge tier={user?.subscriptionTier} />
            </div>
            <p className="text-sm text-navy/50">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex gap-2 overflow-x-auto pb-4 mb-6 border-b border-pawgray-border">
          {tabs.map((t) => (
            <button key={t.key} onClick={() => setActiveTab(t.key)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-medium whitespace-nowrap transition-all ${activeTab === t.key ? 'bg-primary text-white' : 'text-navy/50 hover:bg-primary/5 hover:text-primary'}`}>
              <t.icon className="w-4 h-4" /> {t.label}
            </button>
          ))}
        </div>

        {/* PROFILE */}
        {activeTab === 'profile' && (
          <div className="max-w-lg space-y-4">
            {['name','phone','city','state','pincode'].map((f) => (
              <div key={f}>
                <label className="text-sm font-medium text-navy mb-1 block capitalize">{f}</label>
                <input value={profileForm[f]} onChange={(e) => setProfileForm((p) => ({ ...p, [f]: e.target.value }))}
                  className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
              </div>
            ))}
            <button onClick={handleProfileSave} className="px-6 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark transition-colors">Save Changes</button>
          </div>
        )}

        {/* PETS */}
        {activeTab === 'pets' && (
          <div>
            <Link to="/pets/new" className="inline-flex items-center gap-2 px-5 py-2.5 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark mb-6">
              <Plus className="w-4 h-4" /> Add New Pet
            </Link>
            {loading ? <LoadingSpinner /> : !data?.length ? (
              <EmptyState icon={PawPrint} title="No pets yet" description="Add your first pet to get started" actionLabel="Add Pet" onAction={() => window.location.href='/pets/new'} />
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">{data.map((p) => <PetCard key={p.id} pet={p} />)}</div>
            )}
          </div>
        )}

        {/* MATCHES */}
        {activeTab === 'matches' && (
          <div>
            <div className="flex gap-2 mb-6">
              {['received','sent'].map((t) => (
                <button key={t} onClick={() => setMatchSubTab(t)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium ${matchSubTab === t ? 'bg-primary text-white' : 'bg-pawgray text-navy/60'}`}>
                  {t.charAt(0).toUpperCase() + t.slice(1)}
                </button>
              ))}
            </div>
            {loading ? <LoadingSpinner /> : !data?.content?.length ? (
              <EmptyState icon={Heart} title="No match requests" description={matchSubTab === 'received' ? 'No one has sent you a request yet' : 'You haven\'t sent any requests yet'} />
            ) : (
              <div className="space-y-3">
                {data.content.map((m) => (
                  <div key={m.id} className="flex items-center justify-between bg-white rounded-card border border-pawgray-border p-4">
                    <div>
                      <p className="font-heading font-bold text-navy text-sm">{matchSubTab === 'received' ? m.requesterPetName || 'Pet' : m.receiverPetName || 'Pet'}</p>
                      <p className="text-xs text-navy/40">{m.message || 'No message'}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-badge ${statusColors[m.status] || 'bg-gray-100 text-gray-600'}`}>{m.status}</span>
                      {matchSubTab === 'received' && m.status === 'PENDING' && (
                        <>
                          <button onClick={() => handleMatchAction(m.id, 'accept')} className="p-1.5 bg-green-100 text-green-600 rounded-lg hover:bg-green-200"><Check className="w-4 h-4" /></button>
                          <button onClick={() => handleMatchAction(m.id, 'reject')} className="p-1.5 bg-red-100 text-red-600 rounded-lg hover:bg-red-200"><XIcon className="w-4 h-4" /></button>
                        </>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* ORDERS */}
        {activeTab === 'orders' && (
          <div>
            {loading ? <LoadingSpinner /> : !data?.content?.length ? (
              <EmptyState icon={ShoppingBag} title="No orders yet" actionLabel="Shop Now" onAction={() => window.location.href='/products'} />
            ) : (
              <div className="space-y-3">
                {data.content.map((o) => (
                  <div key={o.id} className="bg-white rounded-card border border-pawgray-border p-4">
                    <div className="flex items-center justify-between">
                      <p className="font-heading font-bold text-navy text-sm">Order #{o.id}</p>
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-badge ${statusColors[o.status] || 'bg-gray-100 text-gray-600'}`}>{o.status}</span>
                    </div>
                    <p className="text-xs text-navy/40 mt-1">₹{o.totalAmount} · {new Date(o.createdAt).toLocaleDateString()}</p>
                    {o.trackingNumber && <p className="text-xs text-navy/50 mt-1">📦 Tracking: {o.trackingNumber}</p>}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* APPOINTMENTS */}
        {activeTab === 'appointments' && (
          <div>
            {loading ? <LoadingSpinner /> : !data?.content?.length ? (
              <EmptyState icon={Calendar} title="No appointments" actionLabel="Find a Vet" onAction={() => window.location.href='/vets'} />
            ) : (
              <div className="space-y-3">
                {data.content.map((a) => (
                  <div key={a.id} className="bg-white rounded-card border border-pawgray-border p-4 flex items-center justify-between">
                    <div>
                      <p className="font-heading font-bold text-navy text-sm">{a.vetName || 'Vet Appointment'}</p>
                      <p className="text-xs text-navy/40">{a.appointmentDate} at {a.appointmentTime} · {a.mode}</p>
                    </div>
                    <span className={`text-xs font-bold px-2.5 py-1 rounded-badge ${statusColors[a.status] || 'bg-gray-100 text-gray-600'}`}>{a.status}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TRAINING */}
        {activeTab === 'training' && (
          <div>
            {loading ? <LoadingSpinner /> : !data?.content?.length ? (
              <EmptyState icon={GraduationCap} title="No training sessions" actionLabel="Find a Trainer" onAction={() => window.location.href='/trainers'} />
            ) : (
              <div className="space-y-3">
                {data.content.map((s) => (
                  <div key={s.id} className="bg-white rounded-card border border-pawgray-border p-4 flex items-center justify-between">
                    <div>
                      <p className="font-heading font-bold text-navy text-sm">{s.trainerName || 'Training Session'}</p>
                      <p className="text-xs text-navy/40">{s.sessionDate} at {s.sessionTime} · {s.mode?.replace(/_/g, ' ')} · {s.durationHours}h</p>
                    </div>
                    <span className={`text-xs font-bold px-2.5 py-1 rounded-badge ${statusColors[s.status] || 'bg-gray-100 text-gray-600'}`}>{s.status}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* SUBSCRIPTION */}
        {activeTab === 'subscription' && (
          <div className="max-w-md">
            <div className="bg-white rounded-card border border-pawgray-border p-6 text-center">
              <SubscriptionBadge tier={user?.subscriptionTier} />
              <h3 className="font-heading font-bold text-navy text-xl mt-3 mb-1">
                {user?.subscriptionTier === 'PREMIUM' ? 'Premium Plan 👑' : user?.subscriptionTier === 'BASIC' ? 'Basic Plan' : 'Free Plan'}
              </h3>
              {user?.subscriptionExpiry && <p className="text-sm text-navy/40 mb-4">Expires: {new Date(user.subscriptionExpiry).toLocaleDateString()}</p>}
              {user?.subscriptionTier === 'FREE' && (
                <Link to="/plans" className="inline-block px-6 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark mt-4">Upgrade Plan</Link>
              )}
            </div>
          </div>
        )}

        {/* REVIEWS */}
        {activeTab === 'reviews' && (
          <EmptyState icon={Star} title="Your Reviews" description="Reviews you've given will appear here" />
        )}
      </div>
    </PageTransition>
  );
}
