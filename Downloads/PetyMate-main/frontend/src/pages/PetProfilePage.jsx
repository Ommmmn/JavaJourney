import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { MapPin, Syringe, Award, Home, Lock, Unlock, Heart, Copy, Check, ChevronRight } from 'lucide-react';
import { petService } from '../services/petService';
import { matchService } from '../services/matchService';
import { useAuth } from '../context/AuthContext';
import PhotoGallery from '../components/common/PhotoGallery';
import RazorpayButton from '../components/common/RazorpayButton';
import PetCard from '../components/common/PetCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import PageTransition from '../components/common/PageTransition';
import { getPetImage } from '../utils/petImageMap';
import toast from 'react-hot-toast';

export default function PetProfilePage() {
  const { petId } = useParams();
  const { user, isAuthenticated } = useAuth();
  const [pet, setPet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [similarPets, setSimilarPets] = useState([]);
  const [matchModalOpen, setMatchModalOpen] = useState(false);
  const [myPets, setMyPets] = useState([]);
  const [selectedPetId, setSelectedPetId] = useState('');
  const [matchMessage, setMatchMessage] = useState('');
  const [matchLoading, setMatchLoading] = useState(false);
  const [unlocked, setUnlocked] = useState(false);
  const [copied, setCopied] = useState('');

  useEffect(() => {
    const fetchPet = async () => {
      setLoading(true);
      try {
        const { data } = await petService.getPetById(petId);
        setPet(data.data);
        setUnlocked(!!data.data.ownerPhone && data.data.ownerPhone.indexOf('*') === -1);
        const { data: similar } = await petService.getPets({ species: data.data.species, city: data.data.city, size: 4 });
        setSimilarPets((similar.data?.content || []).filter((p) => p.id !== data.data.id));
      } catch { toast.error('Pet not found'); }
      finally { setLoading(false); }
    };
    fetchPet();
  }, [petId]);

  useEffect(() => {
    if (isAuthenticated && matchModalOpen) {
      petService.myPets().then(({ data }) => setMyPets(data.data || [])).catch(() => {});
    }
  }, [isAuthenticated, matchModalOpen]);

  const handleSendMatch = async () => {
    if (!selectedPetId) { toast.error('Select one of your pets'); return; }
    setMatchLoading(true);
    try {
      await matchService.sendRequest({ requesterPetId: selectedPetId, receiverPetId: pet.id, message: matchMessage });
      toast.success('Match request sent! 💕');
      setMatchModalOpen(false);
    } catch (err) { toast.error(err.response?.data?.message || 'Failed to send request'); }
    finally { setMatchLoading(false); }
  };

  const handleCopy = (text, label) => {
    navigator.clipboard.writeText(text);
    setCopied(label);
    toast.success('Copied!');
    setTimeout(() => setCopied(''), 2000);
  };

  const ageDisplay = pet?.ageMonths >= 12 ? `${Math.floor(pet.ageMonths / 12)} years ${pet.ageMonths % 12} months` : `${pet?.ageMonths} months`;

  if (loading) return <LoadingSpinner text="Fetching pet details..." />;
  if (!pet) return <div className="text-center py-20 text-navy/50">Pet not found</div>;

  const photos = pet.photos?.length ? pet.photos : [{ photoUrl: getPetImage(pet.species), isPrimary: true }];

  return (
    <PageTransition>
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex items-center gap-2 text-sm text-navy/40 mb-6">
          <Link to="/match" className="hover:text-primary">Match</Link>
          <ChevronRight className="w-3 h-3" />
          <span className="text-navy">{pet.name}</span>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-5 gap-8">
          <div className="lg:col-span-3">
            <PhotoGallery photos={photos} alt={`${pet.name} - ${pet.breed}`} />

            <div className="mt-8 bg-white rounded-card border border-pawgray-border p-6">
              <h2 className="font-heading font-bold text-navy text-lg mb-3">About {pet.name}</h2>
              <p className="text-sm text-navy/60 leading-relaxed">{pet.bio || 'No bio provided.'}</p>
              {pet.healthStatus && (
                <div className="mt-4">
                  <h3 className="text-sm font-semibold text-navy mb-1">Health Notes</h3>
                  <p className="text-sm text-navy/60">{pet.healthStatus}</p>
                </div>
              )}
            </div>
          </div>

          <div className="lg:col-span-2">
            <div className="sticky top-20 space-y-5">
              <div className="bg-white rounded-card border border-pawgray-border p-6">
                <div className="flex items-center gap-3 mb-4">
                  <h1 className="font-heading font-extrabold text-2xl text-navy">{pet.name}</h1>
                  <span className={`text-xs font-bold px-2.5 py-1 rounded-badge ${pet.listingType === 'MATING' ? 'bg-pink-100 text-pink-700' : pet.listingType === 'SALE' ? 'bg-blue-100 text-blue-700' : 'bg-green-100 text-green-700'}`}>
                    {pet.listingType}
                  </span>
                </div>
                {pet.distanceKm && (
                  <div className="flex items-center gap-1 text-sm text-navy/50 mb-4">
                    <MapPin className="w-4 h-4" />
                    <span>{pet.distanceKm.toFixed(1)} km from you · {pet.city}, {pet.state}</span>
                  </div>
                )}

                <div className="grid grid-cols-2 gap-3 mb-4">
                  {[
                    { l: 'Age', v: ageDisplay },
                    { l: 'Gender', v: pet.gender },
                    { l: 'Breed', v: pet.breed },
                    { l: 'Weight', v: pet.weightKg ? `${pet.weightKg} kg` : 'N/A' },
                  ].map((s) => (
                    <div key={s.l} className="bg-pawgray rounded-xl p-3 text-center">
                      <p className="text-[11px] text-navy/40">{s.l}</p>
                      <p className="text-sm font-semibold text-navy">{s.v}</p>
                    </div>
                  ))}
                </div>

                <div className="flex flex-wrap gap-2 mb-5">
                  {pet.vaccinationStatus && <span className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-badge bg-green-50 text-green-700"><Syringe className="w-3 h-3" /> Vaccinated</span>}
                  {pet.pedigreeCertified && <span className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-badge bg-amber-50 text-amber-700"><Award className="w-3 h-3" /> Pedigree</span>}
                  {pet.hasOwnSpace && <span className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-badge bg-blue-50 text-blue-700"><Home className="w-3 h-3" /> Has Space</span>}
                  {pet.neutered && <span className="text-xs px-2.5 py-1 rounded-badge bg-purple-50 text-purple-700">Neutered</span>}
                </div>

                {pet.listingType === 'SALE' && pet.price && (
                  <div className="text-2xl font-extrabold text-primary mb-5">₹{parseFloat(pet.price).toLocaleString()}</div>
                )}
                {pet.listingType === 'ADOPTION' && (
                  <div className="text-lg font-bold text-secondary mb-5">🏡 Free Adoption</div>
                )}
              </div>

              <div className="bg-white rounded-card border border-pawgray-border p-6">
                <h3 className="font-heading font-bold text-navy mb-3">Pet Owner</h3>
                {unlocked ? (
                  <div className="space-y-3">
                    <div className="flex items-center justify-between bg-green-50 p-3 rounded-xl">
                      <div>
                        <p className="text-xs text-green-600 mb-0.5">Phone</p>
                        <p className="text-sm font-semibold text-navy">{pet.ownerPhone}</p>
                      </div>
                      <button onClick={() => handleCopy(pet.ownerPhone, 'phone')} className="p-1.5 text-green-600 hover:bg-green-100 rounded-lg" aria-label="Copy phone">
                        {copied === 'phone' ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                      </button>
                    </div>
                    <div className="flex items-center justify-between bg-green-50 p-3 rounded-xl">
                      <div>
                        <p className="text-xs text-green-600 mb-0.5">Email</p>
                        <p className="text-sm font-semibold text-navy">{pet.ownerEmail}</p>
                      </div>
                      <button onClick={() => handleCopy(pet.ownerEmail, 'email')} className="p-1.5 text-green-600 hover:bg-green-100 rounded-lg" aria-label="Copy email">
                        {copied === 'email' ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                      </button>
                    </div>
                    <div className="flex items-center gap-2 text-xs text-green-600"><Unlock className="w-3.5 h-3.5" /> Contact Unlocked ✓</div>
                  </div>
                ) : (
                  <div className="space-y-3">
                    <div className="bg-pawgray rounded-xl p-4 text-center">
                      <div className="w-12 h-12 rounded-full bg-navy/5 flex items-center justify-center mx-auto mb-2 blur-sm">
                        <span className="text-2xl">👤</span>
                      </div>
                      <p className="text-sm text-navy/40 blur-sm select-none">📞 ***** *****</p>
                      <p className="text-sm text-navy/40 blur-sm select-none">📧 ***@***.com</p>
                    </div>
                    {pet.listingType === 'MATING' ? (
                      <div className="bg-blue-50 text-blue-800 p-3 rounded-xl text-xs flex items-start gap-2">
                        <Lock className="w-4 h-4 shrink-0 mt-0.5" />
                        <p>Send a match request first. Once accepted by the owner, you can securely unlock their contact details from your <strong>Matches</strong> dashboard.</p>
                      </div>
                    ) : (
                      <div className="flex items-center gap-2 text-xs text-navy/50">
                        <Lock className="w-3.5 h-3.5" />
                        <span>Login to view contact details</span>
                      </div>
                    )}
                  </div>
                )}
              </div>

              {pet.listingType === 'MATING' && isAuthenticated && (
                <button onClick={() => setMatchModalOpen(true)}
                  className="w-full py-3.5 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-colors flex items-center justify-center gap-2">
                  <Heart className="w-5 h-5" /> Send Match Request
                </button>
              )}
            </div>
          </div>
        </div>

        {similarPets.length > 0 && (
          <div className="mt-16">
            <h2 className="font-heading font-bold text-navy text-xl mb-6">You Might Also Like</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
              {similarPets.map((p) => <PetCard key={p.id} pet={p} />)}
            </div>
          </div>
        )}
      </div>

      {matchModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" onClick={() => setMatchModalOpen(false)}>
          <div className="bg-white rounded-card p-6 w-full max-w-md shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h3 className="font-heading font-bold text-navy text-lg mb-4">Send Match Request to {pet.name}</h3>
            <div className="mb-4">
              <label className="text-sm font-medium text-navy mb-1 block">Select Your Pet</label>
              <select value={selectedPetId} onChange={(e) => setSelectedPetId(e.target.value)}
                className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30">
                <option value="">Choose a pet...</option>
                {myPets.filter((p) => p.species === pet.species && p.gender !== pet.gender).map((p) => (
                  <option key={p.id} value={p.id}>{p.name} ({p.breed}, {p.gender})</option>
                ))}
              </select>
            </div>
            <div className="mb-5">
              <label className="text-sm font-medium text-navy mb-1 block">Message (optional)</label>
              <textarea value={matchMessage} onChange={(e) => setMatchMessage(e.target.value)} placeholder="Hello! I'd love for our pets to meet..."
                className="w-full px-3 py-2.5 rounded-xl border border-pawgray-border text-sm resize-none h-20 focus:outline-none focus:ring-2 focus:ring-primary/30" />
            </div>
            <div className="flex gap-3">
              <button onClick={() => setMatchModalOpen(false)} className="flex-1 py-2.5 border border-pawgray-border text-navy/60 rounded-xl hover:bg-pawgray">Cancel</button>
              <button onClick={handleSendMatch} disabled={matchLoading}
                className="flex-1 py-2.5 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark disabled:opacity-60 flex items-center justify-center gap-2">
                {matchLoading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : <Heart className="w-4 h-4" />}
                {matchLoading ? 'Sending...' : 'Send'}
              </button>
            </div>
          </div>
        </div>
      )}
    </PageTransition>
  );
}
