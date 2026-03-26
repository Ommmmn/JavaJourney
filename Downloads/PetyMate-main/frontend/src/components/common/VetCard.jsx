import { Link } from 'react-router-dom';
import { MapPin, Star, Clock, BadgeCheck, Calendar } from 'lucide-react';

export default function VetCard({ vet }) {
  return (
    <Link to={`/vets/${vet.id}`}
      className="group bg-white rounded-card overflow-hidden shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 border border-pawgray-border p-5">
      <div className="flex items-start gap-4">
        <div className="relative w-20 h-20 rounded-2xl overflow-hidden flex-shrink-0 border-2 border-pawgray-border">
          <img src={vet.photoUrl || 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200'}
            alt={vet.name} className="w-full h-full object-cover" />
          {vet.isVerified && (
            <div className="absolute -bottom-1 -right-1 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center border-2 border-white">
              <BadgeCheck className="w-3.5 h-3.5 text-white" />
            </div>
          )}
        </div>
        <div className="flex-1 min-w-0">
          <h3 className="font-heading font-bold text-navy text-base truncate group-hover:text-primary transition-colors">
            Dr. {vet.name}
          </h3>
          <p className="text-sm text-navy/60 mt-0.5 truncate">{vet.specialization}</p>
          <p className="text-xs text-navy/40 mt-0.5 truncate">{vet.qualification}</p>
        </div>
      </div>

      <div className="mt-4 grid grid-cols-3 gap-2 text-center">
        <div className="bg-pawgray rounded-xl py-2">
          <div className="flex items-center justify-center gap-1 text-amber-500">
            <Star className="w-3.5 h-3.5 fill-current" />
            <span className="text-sm font-bold">{vet.rating || '0.0'}</span>
          </div>
          <p className="text-[10px] text-navy/40 mt-0.5">{vet.reviewCount} reviews</p>
        </div>
        <div className="bg-pawgray rounded-xl py-2">
          <p className="text-sm font-bold text-navy">{vet.experienceYears}y</p>
          <p className="text-[10px] text-navy/40 mt-0.5">Experience</p>
        </div>
        <div className="bg-pawgray rounded-xl py-2">
          <p className="text-sm font-bold text-secondary">₹{vet.consultationFee}</p>
          <p className="text-[10px] text-navy/40 mt-0.5">Fee</p>
        </div>
      </div>

      <div className="mt-3 flex items-center gap-1 text-xs text-navy/50">
        <MapPin className="w-3 h-3" />
        <span>{vet.city}, {vet.state}</span>
      </div>

      <button className="mt-4 w-full py-2.5 text-sm font-semibold text-white bg-secondary hover:bg-secondary-dark rounded-xl transition-colors flex items-center justify-center gap-2">
        <Calendar className="w-4 h-4" /> Book Appointment
      </button>
    </Link>
  );
}
