import { useState, useEffect } from 'react';
import { adminService } from '../services/adminService';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageTransition from '../components/common/PageTransition';
import { Users, PawPrint, ShoppingBag, Stethoscope, GraduationCap, Star, BarChart3, TrendingUp, BadgeCheck, Ban, Undo2, Eye } from 'lucide-react';
import toast from 'react-hot-toast';

const sidebarItems = [
  { key: 'dashboard', label: 'Dashboard', icon: BarChart3 },
  { key: 'users', label: 'Users', icon: Users },
  { key: 'pets', label: 'Pets', icon: PawPrint },
  { key: 'orders', label: 'Orders', icon: ShoppingBag },
  { key: 'vets', label: 'Vets', icon: Stethoscope },
  { key: 'trainers', label: 'Trainers', icon: GraduationCap },
  { key: 'reviews', label: 'Reviews', icon: Star },
];

export default function AdminDashboardPage() {
  const [activeSection, setActiveSection] = useState('dashboard');
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      let result = null;
      switch (activeSection) {
        case 'dashboard': result = (await adminService.getDashboard()).data.data; break;
        case 'users': result = (await adminService.getUsers({ page: 0, size: 50 })).data.data; break;
        case 'pets': result = (await adminService.getPets({ page: 0, size: 50 })).data.data; break;
        case 'orders': result = (await adminService.getOrders({ page: 0, size: 50 })).data.data; break;
        case 'vets': result = (await adminService.getVets({ page: 0, size: 50 })).data.data; break;
        case 'trainers': result = (await adminService.getTrainers({ page: 0, size: 50 })).data.data; break;
        case 'reviews': result = (await adminService.getReviews({ page: 0, size: 50 })).data.data; break;
        default: break;
      }
      setData(result);
    } catch { setData(null); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchData(); }, [activeSection]);

  const handleAction = async (action, id) => {
    try {
      switch (action) {
        case 'ban': await adminService.banUser(id); break;
        case 'unban': await adminService.unbanUser(id); break;
        case 'verifyVet': await adminService.verifyVet(id); break;
        case 'unverifyVet': await adminService.unverifyVet(id); break;
        case 'verifyTrainer': await adminService.verifyTrainer(id); break;
        case 'unverifyTrainer': await adminService.unverifyTrainer(id); break;
        default: break;
      }
      toast.success('Action completed');
      fetchData();
    } catch (err) { toast.error('Action failed'); }
  };

  return (
    <PageTransition>
      <div className="flex min-h-[calc(100vh-4rem)]">
        <aside className="w-56 bg-navy flex-shrink-0 hidden md:block">
          <div className="p-5">
            <h3 className="font-heading font-bold text-white text-lg mb-6">Admin Panel</h3>
            <nav className="space-y-1">
              {sidebarItems.map((s) => (
                <button key={s.key} onClick={() => setActiveSection(s.key)}
                  className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${activeSection === s.key ? 'bg-primary text-white' : 'text-white/60 hover:bg-white/10 hover:text-white'}`}>
                  <s.icon className="w-4 h-4" /> {s.label}
                </button>
              ))}
            </nav>
          </div>
        </aside>

        <main className="flex-1 bg-pawgray p-6 overflow-y-auto">
          <div className="md:hidden flex gap-2 overflow-x-auto pb-4 mb-6">
            {sidebarItems.map((s) => (
              <button key={s.key} onClick={() => setActiveSection(s.key)}
                className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-xs font-medium whitespace-nowrap ${activeSection === s.key ? 'bg-primary text-white' : 'bg-white text-navy/60'}`}>
                <s.icon className="w-3.5 h-3.5" /> {s.label}
              </button>
            ))}
          </div>

          {loading ? <LoadingSpinner text="Loading admin data..." /> : (
            <>
              {activeSection === 'dashboard' && data && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">Dashboard Overview</h2>
                  <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                    {[
                      { l: 'Total Users', v: data.totalUsers || 0, c: 'bg-blue-50 text-blue-700', icon: Users },
                      { l: 'Active Listings', v: data.activePetListings || 0, c: 'bg-green-50 text-green-700', icon: PawPrint },
                      { l: 'Total Orders', v: data.totalOrders || 0, c: 'bg-purple-50 text-purple-700', icon: ShoppingBag },
                      { l: 'Monthly Revenue', v: `₹${(data.monthlyRevenue || 0).toLocaleString()}`, c: 'bg-amber-50 text-amber-700', icon: TrendingUp },
                    ].map((kpi) => (
                      <div key={kpi.l} className={`rounded-card p-5 ${kpi.c}`}>
                        <kpi.icon className="w-5 h-5 mb-2" />
                        <p className="text-2xl font-heading font-extrabold">{kpi.v}</p>
                        <p className="text-xs mt-1 opacity-70">{kpi.l}</p>
                      </div>
                    ))}
                  </div>
                  <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                    {[
                      { l: 'Today Registrations', v: data.todayRegistrations || 0 },
                      { l: 'Total Matches', v: data.totalMatches || 0 },
                      { l: 'Pending Vet Verifications', v: data.pendingVetVerifications || 0 },
                      { l: 'Pending Trainer Verifications', v: data.pendingTrainerVerifications || 0 },
                    ].map((s) => (
                      <div key={s.l} className="bg-white rounded-card border border-pawgray-border p-4 text-center">
                        <p className="text-xl font-bold text-navy">{s.v}</p>
                        <p className="text-xs text-navy/40 mt-1">{s.l}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeSection === 'users' && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">User Management</h2>
                  <div className="bg-white rounded-card border border-pawgray-border overflow-hidden">
                    <div className="overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead><tr className="bg-pawgray text-left">
                          <th className="px-4 py-3 font-medium text-navy/50">Name</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Email</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Role</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Tier</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Status</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Actions</th>
                        </tr></thead>
                        <tbody>
                          {(data?.content || []).map((u) => (
                            <tr key={u.id} className="border-t border-pawgray-border hover:bg-pawgray/50">
                              <td className="px-4 py-3 font-medium text-navy">{u.name}</td>
                              <td className="px-4 py-3 text-navy/60">{u.email}</td>
                              <td className="px-4 py-3"><span className="text-xs px-2 py-0.5 rounded-badge bg-blue-100 text-blue-700">{u.role}</span></td>
                              <td className="px-4 py-3"><span className="text-xs px-2 py-0.5 rounded-badge bg-amber-100 text-amber-700">{u.subscriptionTier}</span></td>
                              <td className="px-4 py-3"><span className={`text-xs px-2 py-0.5 rounded-badge ${u.isBanned ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>{u.isBanned ? 'Banned' : 'Active'}</span></td>
                              <td className="px-4 py-3">
                                {u.isBanned ? (
                                  <button onClick={() => handleAction('unban', u.id)} className="text-xs px-2 py-1 bg-green-100 text-green-700 rounded-lg hover:bg-green-200">Unban</button>
                                ) : (
                                  <button onClick={() => handleAction('ban', u.id)} className="text-xs px-2 py-1 bg-red-100 text-red-700 rounded-lg hover:bg-red-200">Ban</button>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {(activeSection === 'vets' || activeSection === 'trainers') && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">{activeSection === 'vets' ? 'Vet' : 'Trainer'} Management</h2>
                  <div className="bg-white rounded-card border border-pawgray-border overflow-hidden">
                    <div className="overflow-x-auto">
                      <table className="w-full text-sm">
                        <thead><tr className="bg-pawgray text-left">
                          <th className="px-4 py-3 font-medium text-navy/50">Name</th>
                          <th className="px-4 py-3 font-medium text-navy/50">City</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Specialization</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Verified</th>
                          <th className="px-4 py-3 font-medium text-navy/50">Actions</th>
                        </tr></thead>
                        <tbody>
                          {(data?.content || []).map((item) => (
                            <tr key={item.id} className="border-t border-pawgray-border hover:bg-pawgray/50">
                              <td className="px-4 py-3 font-medium text-navy">{item.name}</td>
                              <td className="px-4 py-3 text-navy/60">{item.city}</td>
                              <td className="px-4 py-3 text-navy/60">{item.specialization}</td>
                              <td className="px-4 py-3">
                                <span className={`text-xs px-2 py-0.5 rounded-badge ${item.isVerified ? 'bg-green-100 text-green-700' : 'bg-amber-100 text-amber-700'}`}>
                                  {item.isVerified ? 'Verified' : 'Pending'}
                                </span>
                              </td>
                              <td className="px-4 py-3">
                                {item.isVerified ? (
                                  <button onClick={() => handleAction(activeSection === 'vets' ? 'unverifyVet' : 'unverifyTrainer', item.id)}
                                    className="text-xs px-2 py-1 bg-red-100 text-red-700 rounded-lg hover:bg-red-200">Unverify</button>
                                ) : (
                                  <button onClick={() => handleAction(activeSection === 'vets' ? 'verifyVet' : 'verifyTrainer', item.id)}
                                    className="text-xs px-2 py-1 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 flex items-center gap-1"><BadgeCheck className="w-3 h-3" /> Verify</button>
                                )}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {activeSection === 'pets' && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">Pet Listings</h2>
                  <p className="text-sm text-navy/40 mb-4">{data?.content?.length || 0} listings</p>
                  <div className="space-y-3">
                    {(data?.content || []).map((p) => (
                      <div key={p.id} className="bg-white rounded-card border border-pawgray-border p-4 flex items-center justify-between">
                        <div><p className="font-medium text-navy">{p.name} ({p.species} · {p.breed})</p><p className="text-xs text-navy/40">{p.city} · {p.listingType} · {p.status}</p></div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeSection === 'orders' && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">Orders</h2>
                  <div className="space-y-3">
                    {(data?.content || []).map((o) => (
                      <div key={o.id} className="bg-white rounded-card border border-pawgray-border p-4 flex items-center justify-between">
                        <div><p className="font-medium text-navy">Order #{o.id}</p><p className="text-xs text-navy/40">₹{o.totalAmount} · {o.status}</p></div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeSection === 'reviews' && (
                <div>
                  <h2 className="font-heading font-bold text-navy text-xl mb-6">Reviews</h2>
                  <p className="text-sm text-navy/40">Review management coming soon.</p>
                </div>
              )}
            </>
          )}
        </main>
      </div>
    </PageTransition>
  );
}
