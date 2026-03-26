import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { SlidersHorizontal, Grid3x3, List, X } from 'lucide-react';
import { petService } from '../services/petService';
import PetCard from '../components/common/PetCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';

const speciesList = ['DOG','CAT','RABBIT','BIRD','FISH','HAMSTER','REPTILE','OTHER'];
const speciesEmoji = { DOG:'🐕', CAT:'🐈', RABBIT:'🐇', BIRD:'🐦', FISH:'🐟', HAMSTER:'🐹', REPTILE:'🦎', OTHER:'🐾' };

export default function MatchPage() {
  const [searchParams] = useSearchParams();
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [showFilters, setShowFilters] = useState(false);

  const [filters, setFilters] = useState({
    species: searchParams.get('species') || '',
    breed: '', gender: '', listingType: 'MATING',
    city: searchParams.get('city') || '',
    radiusKm: searchParams.get('radiusKm') || 50,
    ageMin: '', ageMax: '',
    vaccinationStatus: false, pedigreeOnly: false, hasOwnSpace: false,
    sortBy: 'NEWEST',
  });

  const fetchPets = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12 };
      Object.entries(filters).forEach(([k, v]) => { if (v !== '' && v !== false) params[k] = v; });
      const { data } = await petService.getPets(params);
      const result = data.data;
      if (p === 0) setPets(result.content || []);
      else setPets((prev) => [...prev, ...(result.content || [])]);
      setTotalElements(result.totalElements || 0);
      setPage(p);
    } catch { setPets([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchPets(0); }, []);

  const applyFilters = () => { fetchPets(0); setShowFilters(false); };
  const resetFilters = () => {
    setFilters({ species: '', breed: '', gender: '', listingType: 'MATING', city: '', radiusKm: 50, ageMin: '', ageMax: '', vaccinationStatus: false, pedigreeOnly: false, hasOwnSpace: false, sortBy: 'NEWEST' });
  };

  const activeFilterCount = Object.entries(filters).filter(([k, v]) => v && v !== 'MATING' && v !== 'NEWEST' && v !== 50 && v !== false && v !== '').length;

  return (
    <PageTransition>
      <div className="max-w-7xl mx-auto px-4 py-8 flex gap-6">
        {/* Desktop Sidebar */}
        <aside className="hidden lg:block w-72 flex-shrink-0">
          <div className="sticky top-20 bg-white rounded-card border border-pawgray-border p-5 max-h-[calc(100vh-6rem)] overflow-y-auto">
            <div className="flex items-center justify-between mb-5">
              <h3 className="font-heading font-bold text-navy text-lg">Filters</h3>
              <button onClick={resetFilters} className="text-xs text-primary hover:underline">Reset All</button>
            </div>

            <div className="space-y-5">
              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Looking For</label>
                <div className="flex gap-2">
                  {['MATING','SALE','ADOPTION'].map((t) => (
                    <button key={t} onClick={() => setFilters((f) => ({ ...f, listingType: t }))}
                      className={`flex-1 py-2 text-xs font-semibold rounded-lg transition-all ${filters.listingType === t ? 'bg-primary text-white' : 'bg-pawgray text-navy/60 hover:bg-primary/10'}`}>
                      {t === 'MATING' ? '💕' : t === 'SALE' ? '💰' : '🏡'} {t.charAt(0) + t.slice(1).toLowerCase()}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Species</label>
                <div className="flex flex-wrap gap-1.5">
                  {speciesList.map((s) => (
                    <button key={s} onClick={() => setFilters((f) => ({ ...f, species: f.species === s ? '' : s }))}
                      className={`px-2.5 py-1.5 rounded-lg text-xs font-medium transition-all ${filters.species === s ? 'bg-primary text-white' : 'bg-pawgray text-navy/60 hover:bg-primary/10'}`}>
                      {speciesEmoji[s]} {s}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Gender</label>
                <div className="flex gap-2">
                  {[{v:'',l:'Any'},{v:'MALE',l:'♂️ Male'},{v:'FEMALE',l:'♀️ Female'}].map((g) => (
                    <button key={g.v} onClick={() => setFilters((f) => ({ ...f, gender: g.v }))}
                      className={`flex-1 py-2 text-xs font-medium rounded-lg transition-all ${filters.gender === g.v ? 'bg-primary text-white' : 'bg-pawgray text-navy/60'}`}>
                      {g.l}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">City</label>
                <input value={filters.city} onChange={(e) => setFilters((f) => ({ ...f, city: e.target.value }))}
                  placeholder="Enter city" className="w-full px-3 py-2 rounded-lg border border-pawgray-border text-sm focus:outline-none focus:ring-1 focus:ring-primary" />
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Distance: {filters.radiusKm} km</label>
                <input type="range" min="10" max="500" step="10" value={filters.radiusKm}
                  onChange={(e) => setFilters((f) => ({ ...f, radiusKm: parseInt(e.target.value) }))}
                  className="w-full accent-primary" />
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Breed</label>
                <input value={filters.breed} onChange={(e) => setFilters((f) => ({ ...f, breed: e.target.value }))}
                  placeholder="Search breed" className="w-full px-3 py-2 rounded-lg border border-pawgray-border text-sm focus:outline-none focus:ring-1 focus:ring-primary" />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium text-navy block">Health</label>
                {[
                  { key: 'vaccinationStatus', label: '💉 Vaccinated Only' },
                  { key: 'pedigreeOnly', label: '🏆 Pedigree Certified' },
                  { key: 'hasOwnSpace', label: '🏠 Has Own Space' },
                ].map((f) => (
                  <label key={f.key} className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" checked={filters[f.key]} onChange={() => setFilters((prev) => ({ ...prev, [f.key]: !prev[f.key] }))} className="accent-primary" />
                    <span className="text-sm text-navy/60">{f.label}</span>
                  </label>
                ))}
              </div>

              <div>
                <label className="text-sm font-medium text-navy mb-2 block">Sort By</label>
                <select value={filters.sortBy} onChange={(e) => setFilters((f) => ({ ...f, sortBy: e.target.value }))}
                  className="w-full px-3 py-2 rounded-lg border border-pawgray-border text-sm focus:outline-none focus:ring-1 focus:ring-primary">
                  <option value="NEWEST">Newest First</option>
                  <option value="NEAREST">Nearest First</option>
                  <option value="AGE_ASC">Youngest First</option>
                  <option value="AGE_DESC">Oldest First</option>
                  <option value="PRICE_ASC">Price: Low → High</option>
                  <option value="PRICE_DESC">Price: High → Low</option>
                </select>
              </div>

              <button onClick={applyFilters} className="w-full py-3 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-colors">
                Apply Filters
              </button>
            </div>
          </div>
        </aside>

        {/* Main */}
        <main className="flex-1 min-w-0">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="font-heading font-extrabold text-2xl text-navy">{totalElements} pets found {filters.city && `in ${filters.city}`}</h1>
            </div>
            <div className="flex items-center gap-2">
              <button onClick={() => setShowFilters(true)} className="lg:hidden p-2.5 rounded-xl bg-white border border-pawgray-border text-navy/60 hover:text-primary relative" aria-label="Open filters">
                <SlidersHorizontal className="w-5 h-5" />
                {activeFilterCount > 0 && <span className="absolute -top-1 -right-1 w-4 h-4 bg-primary text-white text-[10px] rounded-full flex items-center justify-center">{activeFilterCount}</span>}
              </button>
            </div>
          </div>

          {loading && page === 0 ? (
            <LoadingSpinner text="Fetching adorable pets..." />
          ) : pets.length === 0 ? (
            <EmptyState title="No pets found in your area" description="Try expanding your search radius or changing filters" actionLabel="Reset Filters" onAction={() => { resetFilters(); fetchPets(0); }} />
          ) : (
            <>
              <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5">
                {pets.map((pet) => <PetCard key={pet.id} pet={pet} />)}
              </div>
              {pets.length < totalElements && (
                <div className="text-center mt-8">
                  <button onClick={() => fetchPets(page + 1)} disabled={loading}
                    className="px-8 py-3 bg-white border border-pawgray-border text-navy font-medium rounded-xl hover:bg-pawgray transition-all disabled:opacity-50">
                    {loading ? 'Loading...' : 'Load More'}
                  </button>
                </div>
              )}
            </>
          )}
        </main>
      </div>

      {/* Mobile Filter Drawer */}
      {showFilters && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <div className="absolute inset-0 bg-black/40" onClick={() => setShowFilters(false)} />
          <div className="absolute right-0 top-0 bottom-0 w-80 bg-white shadow-xl overflow-y-auto p-5">
            <div className="flex items-center justify-between mb-5">
              <h3 className="font-heading font-bold text-navy text-lg">Filters</h3>
              <button onClick={() => setShowFilters(false)} className="p-1 text-navy/40 hover:text-navy" aria-label="Close"><X className="w-5 h-5" /></button>
            </div>
            <p className="text-sm text-navy/40 mb-4">Use the desktop sidebar filters on larger screens or apply directly here.</p>
            <button onClick={applyFilters} className="w-full py-3 bg-primary text-white font-semibold rounded-xl">Apply & Close</button>
          </div>
        </div>
      )}
    </PageTransition>
  );
}
