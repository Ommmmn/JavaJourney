import { useState, useEffect } from 'react';
import { vetService } from '../services/vetService';
import VetCard from '../components/common/VetCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';
import { Stethoscope, Search } from 'lucide-react';

export default function VetsPage() {
  const [vets, setVets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [city, setCity] = useState('');
  const [specialization, setSpecialization] = useState('');
  const [sortBy, setSortBy] = useState('RATING');

  const fetchVets = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12, sortBy };
      if (city) params.city = city;
      if (specialization) params.specialization = specialization;
      const { data } = await vetService.getVets(params);
      const result = data.data;
      if (p === 0) setVets(result.content || []);
      else setVets((prev) => [...prev, ...(result.content || [])]);
      setTotalElements(result.totalElements || 0);
      setPage(p);
    } catch { setVets([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchVets(0); }, [sortBy]);

  const handleSearch = (e) => { e.preventDefault(); fetchVets(0); };

  return (
    <PageTransition>
      <section className="bg-gradient-to-br from-secondary/10 to-teal-50 py-16 text-center">
        <div className="max-w-3xl mx-auto px-4">
          <h1 className="font-heading font-extrabold text-4xl text-navy mb-3">Consult Certified Veterinarians 🩺</h1>
          <p className="text-navy/50">Online or clinic appointments with verified professionals</p>
        </div>
      </section>

      <div className="max-w-6xl mx-auto px-4 py-8">
        <form onSubmit={handleSearch} className="flex flex-wrap gap-3 mb-8">
          <div className="relative flex-1 min-w-[200px]">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
            <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="Search by city..."
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30" />
          </div>
          <input value={specialization} onChange={(e) => setSpecialization(e.target.value)} placeholder="Specialization..."
            className="px-4 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30 min-w-[180px]" />
          <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-secondary/30">
            <option value="RATING">Top Rated</option>
            <option value="FEE_ASC">Fee: Low → High</option>
            <option value="FEE_DESC">Fee: High → Low</option>
            <option value="EXPERIENCE">Most Experienced</option>
          </select>
          <button type="submit" className="px-6 py-2.5 bg-secondary text-white rounded-xl text-sm font-medium hover:bg-secondary-dark transition-colors">Search</button>
        </form>

        {loading && page === 0 ? (
          <LoadingSpinner text="Finding vets..." />
        ) : vets.length === 0 ? (
          <EmptyState icon={Stethoscope} title="No vets found" description="Try a different city or specialization" />
        ) : (
          <>
            <p className="text-sm text-navy/40 mb-4">{totalElements} veterinarians found</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {vets.map((vet) => <VetCard key={vet.id} vet={vet} />)}
            </div>
            {vets.length < totalElements && (
              <div className="text-center mt-8">
                <button onClick={() => fetchVets(page + 1)} disabled={loading}
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
