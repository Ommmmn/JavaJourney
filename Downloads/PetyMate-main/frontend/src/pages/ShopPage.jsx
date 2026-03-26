import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { shopService } from '../services/shopService';
import PetCard from '../components/common/PetCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';
import { Heart } from 'lucide-react';

const speciesTabs = [
  { label: 'All', value: '' },
  { label: '🐕 Dogs', value: 'DOG' },
  { label: '🐈 Cats', value: 'CAT' },
  { label: '🐇 Rabbits', value: 'RABBIT' },
  { label: '🐦 Birds', value: 'BIRD' },
  { label: '🐟 Fish', value: 'FISH' },
  { label: '🐾 Others', value: 'OTHER' },
];

export default function ShopPage() {
  const [searchParams] = useSearchParams();
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [species, setSpecies] = useState(searchParams.get('species') || '');
  const [listingType, setListingType] = useState('');

  const fetchPets = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12 };
      if (species) params.species = species;
      if (listingType) params.listingType = listingType;
      const { data } = await shopService.getShopPets(params);
      const result = data.data;
      if (p === 0) setPets(result.content || []);
      else setPets((prev) => [...prev, ...(result.content || [])]);
      setTotalElements(result.totalElements || 0);
      setPage(p);
    } catch { setPets([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchPets(0); }, [species, listingType]);

  return (
    <PageTransition>
      <section className="bg-gradient-to-br from-primary/5 to-accent/5 py-16 text-center">
        <div className="max-w-3xl mx-auto px-4">
          <h1 className="font-heading font-extrabold text-4xl text-navy mb-3">Find Your Perfect Pet Companion 🐾</h1>
          <p className="text-navy/50">Browse verified breeders and heartwarming adoption listings</p>
        </div>
      </section>

      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex flex-wrap gap-2 mb-4">
          {speciesTabs.map((t) => (
            <button key={t.value} onClick={() => setSpecies(t.value)}
              className={`px-4 py-2 rounded-pill text-sm font-medium transition-all ${species === t.value ? 'bg-primary text-white' : 'bg-white text-navy/60 border border-pawgray-border hover:border-primary/30'}`}>
              {t.label}
            </button>
          ))}
        </div>
        <div className="flex gap-2 mb-8">
          {[{ l: 'All', v: '' }, { l: '💰 For Sale', v: 'SALE' }, { l: '🏡 For Adoption', v: 'ADOPTION' }].map((t) => (
            <button key={t.v} onClick={() => setListingType(t.v)}
              className={`px-4 py-2 rounded-pill text-sm font-medium transition-all ${listingType === t.v ? 'bg-secondary text-white' : 'bg-white text-navy/60 border border-pawgray-border hover:border-secondary/30'}`}>
              {t.l}
            </button>
          ))}
        </div>

        {loading && page === 0 ? (
          <LoadingSpinner text="Loading shop..." />
        ) : pets.length === 0 ? (
          <EmptyState icon={Heart} title="No pets available" description="Check back soon for new listings" />
        ) : (
          <>
            <p className="text-sm text-navy/40 mb-4">{totalElements} pets found</p>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {pets.map((pet) => <PetCard key={pet.id} pet={pet} />)}
            </div>
            {pets.length < totalElements && (
              <div className="text-center mt-8">
                <button onClick={() => fetchPets(page + 1)} disabled={loading}
                  className="px-8 py-3 bg-white border border-pawgray-border text-navy font-medium rounded-xl hover:bg-pawgray transition-all">
                  {loading ? 'Loading...' : 'Load More'}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </PageTransition>
  );
}
