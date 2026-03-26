import { useState, useEffect } from 'react';
import { trainerService } from '../services/trainerService';
import TrainerCard from '../components/common/TrainerCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';
import { GraduationCap, Search } from 'lucide-react';

export default function TrainersPage() {
  const [trainers, setTrainers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [city, setCity] = useState('');
  const [specialization, setSpecialization] = useState('');
  const [sortBy, setSortBy] = useState('RATING');

  const fetchTrainers = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12, sortBy };
      if (city) params.city = city;
      if (specialization) params.specialization = specialization;
      const { data } = await trainerService.getTrainers(params);
      const result = data.data;
      if (p === 0) setTrainers(result.content || []);
      else setTrainers((prev) => [...prev, ...(result.content || [])]);
      setTotalElements(result.totalElements || 0);
      setPage(p);
    } catch { setTrainers([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchTrainers(0); }, [sortBy]);

  const handleSearch = (e) => { e.preventDefault(); fetchTrainers(0); };

  return (
    <PageTransition>
      <section className="bg-gradient-to-br from-secondary/10 to-emerald-50 py-16 text-center">
        <div className="max-w-3xl mx-auto px-4">
          <h1 className="font-heading font-extrabold text-4xl text-navy mb-3">Expert Pet Trainers Near You 🎓</h1>
          <p className="text-navy/50">From basic obedience to advanced agility — certified professionals</p>
        </div>
      </section>

      <div className="max-w-6xl mx-auto px-4 py-8">
        <form onSubmit={handleSearch} className="flex flex-wrap gap-3 mb-8">
          <div className="relative flex-1 min-w-[200px]">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
            <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="Search by city..."
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30" />
          </div>
          <select value={specialization} onChange={(e) => setSpecialization(e.target.value)}
            className="px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none min-w-[180px]">
            <option value="">All Specializations</option>
            {['OBEDIENCE','AGILITY','BEHAVIOR_CORRECTION','PUPPY_TRAINING','TRICK_TRAINING','THERAPY_DOG','PROTECTION','MULTI'].map((s) => (
              <option key={s} value={s}>{s.replace(/_/g, ' ')}</option>
            ))}
          </select>
          <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none">
            <option value="RATING">Top Rated</option>
            <option value="FEE_ASC">Fee: Low → High</option>
            <option value="FEE_DESC">Fee: High → Low</option>
            <option value="EXPERIENCE">Most Experienced</option>
            <option value="TOTAL_SESSIONS">Most Sessions</option>
          </select>
          <button type="submit" className="px-6 py-2.5 bg-secondary text-white rounded-xl text-sm font-medium hover:bg-secondary-dark transition-colors">Search</button>
        </form>

        {loading && page === 0 ? (
          <LoadingSpinner text="Finding trainers..." />
        ) : trainers.length === 0 ? (
          <EmptyState icon={GraduationCap} title="No trainers found" description="Try a different city or specialization" />
        ) : (
          <>
            <p className="text-sm text-navy/40 mb-4">{totalElements} trainers found</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {trainers.map((t) => <TrainerCard key={t.id} trainer={t} />)}
            </div>
            {trainers.length < totalElements && (
              <div className="text-center mt-8">
                <button onClick={() => fetchTrainers(page + 1)} disabled={loading}
                  className="px-8 py-3 bg-white border border-pawgray-border text-navy font-medium rounded-xl hover:bg-pawgray transition-all">
                  Load More
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </PageTransition>
  );
}
