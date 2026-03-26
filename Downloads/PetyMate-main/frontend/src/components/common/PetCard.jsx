import { Link } from 'react-router-dom';
import { MapPin, Syringe, Award, Home, Heart } from 'lucide-react';
import { getPetImage } from '../../utils/petImageMap';

const listingColors = {
  MATING: 'bg-pink-100 text-pink-700',
  SALE: 'bg-blue-100 text-blue-700',
  ADOPTION: 'bg-green-100 text-green-700',
};

const speciesEmoji = { DOG: '🐕', CAT: '🐈', RABBIT: '🐇', BIRD: '🐦', FISH: '🐟', HAMSTER: '🐹', REPTILE: '🦎', OTHER: '🐾' };

export default function PetCard({ pet }) {
  const primaryPhoto = pet.photos?.find((p) => p.isPrimary)?.photoUrl
    || pet.photos?.[0]?.photoUrl
    || getPetImage(pet.species);

  const ageDisplay = pet.ageMonths >= 12
    ? `${Math.floor(pet.ageMonths / 12)}y ${pet.ageMonths % 12}m`
    : `${pet.ageMonths}m`;

  return (
    <Link to={pet.listingType === 'MATING' ? `/match/${pet.id}` : `/shop/${pet.id}`}
      className="group bg-white rounded-card overflow-hidden shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 border border-pawgray-border">
      <div className="relative aspect-[4/3] overflow-hidden">
        <img src={primaryPhoto} alt={`${pet.name} - ${pet.breed} ${pet.species}`}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
        <span className="absolute top-3 left-3 text-xs font-bold px-2.5 py-1 rounded-badge bg-white/90 backdrop-blur-sm shadow-sm">
          {speciesEmoji[pet.species] || '🐾'} {pet.species}
        </span>
        <span className={`absolute top-3 right-3 text-xs font-bold px-2.5 py-1 rounded-badge ${listingColors[pet.listingType]}`}>
          {pet.listingType === 'MATING' ? '💕 Mating' : pet.listingType === 'SALE' ? `₹${pet.price?.toLocaleString()}` : '🏡 Adopt'}
        </span>
      </div>
      <div className="p-4">
        <div className="flex items-center justify-between mb-1">
          <h3 className="font-heading font-bold text-navy text-lg truncate">{pet.name}</h3>
          <span className="text-xs px-2 py-0.5 rounded-badge bg-pawgray font-medium">{pet.gender === 'MALE' ? '♂️' : '♀️'}</span>
        </div>
        <p className="text-sm text-navy/60 mb-2">{pet.breed} · {ageDisplay}</p>
        <div className="flex items-center gap-1 text-xs text-navy/50 mb-3">
          <MapPin className="w-3 h-3" />
          <span>{pet.city}{pet.distanceKm ? ` · ${pet.distanceKm.toFixed(1)} km` : ''}</span>
        </div>
        <div className="flex flex-wrap gap-1.5 mb-3">
          {pet.vaccinationStatus && (
            <span className="flex items-center gap-1 text-[11px] px-2 py-0.5 rounded-badge bg-green-50 text-green-700">
              <Syringe className="w-3 h-3" /> Vaccinated
            </span>
          )}
          {pet.pedigreeCertified && (
            <span className="flex items-center gap-1 text-[11px] px-2 py-0.5 rounded-badge bg-amber-50 text-amber-700">
              <Award className="w-3 h-3" /> Pedigree
            </span>
          )}
          {pet.hasOwnSpace && (
            <span className="flex items-center gap-1 text-[11px] px-2 py-0.5 rounded-badge bg-blue-50 text-blue-700">
              <Home className="w-3 h-3" /> Has Space
            </span>
          )}
        </div>
        <button className="w-full py-2.5 text-sm font-semibold text-white bg-primary hover:bg-primary-dark rounded-xl transition-colors flex items-center justify-center gap-2">
          View Profile <Heart className="w-3.5 h-3.5" />
        </button>
      </div>
    </Link>
  );
}
