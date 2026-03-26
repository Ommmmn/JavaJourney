import { Link } from 'react-router-dom';
import { MapPin, Star, BadgeCheck, Home, Monitor, Users, Building2, Clock } from 'lucide-react';

const modeIcons = { HOME_VISIT: Home, ONLINE: Monitor, GROUP_CLASS: Users, TRAINING_CENTER: Building2 };

const specColors = {
  OBEDIENCE: 'bg-blue-100 text-blue-700',
  AGILITY: 'bg-green-100 text-green-700',
  BEHAVIOR_CORRECTION: 'bg-red-100 text-red-700',
  PUPPY_TRAINING: 'bg-amber-100 text-amber-700',
  TRICK_TRAINING: 'bg-purple-100 text-purple-700',
  THERAPY_DOG: 'bg-teal-100 text-teal-700',
  PROTECTION: 'bg-orange-100 text-orange-700',
  MULTI: 'bg-indigo-100 text-indigo-700',
};

export default function TrainerCard({ trainer }) {
  const modes = trainer.sessionModes?.split(',').map((s) => s.trim()) || [];

  return (
    <Link to={`/trainers/${trainer.id}`}
      className="group bg-white rounded-card overflow-hidden shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 border border-pawgray-border">
      <div className="relative pt-8 pb-4 px-5 text-center bg-gradient-to-br from-secondary/5 to-teal-50">
        <div className="relative inline-block">
          <div className="w-24 h-24 rounded-full border-4 border-white shadow-lg overflow-hidden mx-auto">
            <img src={trainer.photoUrl || 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=200'}
              alt={trainer.name} className="w-full h-full object-cover" />
          </div>
          {trainer.isVerified && (
            <div className="absolute bottom-0 right-0 w-7 h-7 bg-blue-500 rounded-full flex items-center justify-center border-2 border-white">
              <BadgeCheck className="w-4 h-4 text-white" />
            </div>
          )}
        </div>
        <h3 className="mt-3 font-heading font-bold text-navy text-lg group-hover:text-secondary transition-colors">{trainer.name}</h3>
        <span className={`inline-block mt-1.5 text-xs font-semibold px-3 py-1 rounded-badge ${specColors[trainer.specialization] || 'bg-gray-100 text-gray-700'}`}>
          {trainer.specialization?.replace(/_/g, ' ')}
        </span>
      </div>

      <div className="px-5 pb-5">
        <div className="flex items-center justify-center gap-3 py-3 mt-1">
          {modes.map((m) => {
            const Icon = modeIcons[m] || Monitor;
            return (
              <div key={m} className="w-8 h-8 rounded-lg bg-secondary/10 flex items-center justify-center" title={m.replace(/_/g, ' ')}>
                <Icon className="w-4 h-4 text-secondary" />
              </div>
            );
          })}
        </div>

        <div className="grid grid-cols-3 gap-2 text-center mt-2">
          <div>
            <p className="text-sm font-bold text-navy">{trainer.experienceYears}y</p>
            <p className="text-[10px] text-navy/40">Experience</p>
          </div>
          <div>
            <div className="flex items-center justify-center gap-0.5 text-amber-500">
              <Star className="w-3 h-3 fill-current" />
              <span className="text-sm font-bold">{trainer.rating || '0.0'}</span>
            </div>
            <p className="text-[10px] text-navy/40">{trainer.totalSessions} sessions</p>
          </div>
          <div>
            <p className="text-sm font-bold text-secondary">₹{trainer.sessionFeePerHour}</p>
            <p className="text-[10px] text-navy/40">per hour</p>
          </div>
        </div>

        <div className="flex items-center justify-center gap-1 text-xs text-navy/50 mt-3">
          <MapPin className="w-3 h-3" />
          <span>{trainer.city}, {trainer.state}</span>
        </div>

        <div className="mt-4 flex gap-2">
          <button className="flex-1 py-2.5 text-sm font-medium text-secondary border border-secondary rounded-xl hover:bg-secondary/5 transition-colors text-center">
            View Profile
          </button>
          <button className="flex-1 py-2.5 text-sm font-semibold text-white bg-secondary hover:bg-secondary-dark rounded-xl transition-colors flex items-center justify-center gap-1.5">
            <Clock className="w-3.5 h-3.5" /> Book
          </button>
        </div>
      </div>
    </Link>
  );
}
