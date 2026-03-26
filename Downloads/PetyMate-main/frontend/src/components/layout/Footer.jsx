import { Link } from 'react-router-dom';
import { PawPrint, Instagram, Facebook, Twitter, Youtube, Mail } from 'lucide-react';
import toast from 'react-hot-toast';
import { useState } from 'react';

const quickLinks = [
  { to: '/', label: 'Home' },
  { to: '/match', label: 'Find Match' },
  { to: '/shop', label: 'Shop' },
  { to: '/products', label: 'Products' },
  { to: '/vets', label: 'Vets' },
  { to: '/trainers', label: 'Trainers' },
  { to: '/plans', label: 'Plans' },
];

const supportLinks = [
  { to: '#', label: 'FAQ' },
  { to: '#', label: 'Contact Us' },
  { to: '#', label: 'Report Issue' },
  { to: '#', label: 'Privacy Policy' },
  { to: '#', label: 'Terms of Service' },
];

export default function Footer() {
  const [email, setEmail] = useState('');

  const handleNotify = (e) => {
    e.preventDefault();
    if (!email) return;
    toast.success('Thanks! We\'ll notify you when the app launches 📱');
    setEmail('');
  };

  return (
    <footer className="bg-navy text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-10">
          <div>
            <Link to="/" className="flex items-center gap-2 mb-4">
              <div className="w-9 h-9 bg-primary rounded-xl flex items-center justify-center">
                <PawPrint className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-heading font-bold">
                Pety<span className="text-primary">Mate</span>
              </span>
            </Link>
            <p className="text-white/60 text-sm leading-relaxed mb-5">
              India's #1 pet platform. Find mating partners, buy or adopt pets, shop products, consult vets, and hire trainers — all in one place.
            </p>
            <div className="flex items-center gap-3">
              {[Instagram, Facebook, Twitter, Youtube].map((Icon, i) => (
                <a key={i} href="#" className="w-9 h-9 rounded-lg bg-white/10 hover:bg-primary flex items-center justify-center transition-all hover:scale-110" aria-label="Social link">
                  <Icon className="w-4 h-4" />
                </a>
              ))}
            </div>
          </div>

          <div>
            <h4 className="font-heading font-bold text-base mb-4">Quick Links</h4>
            <ul className="space-y-2.5">
              {quickLinks.map((l) => (
                <li key={l.to + l.label}>
                  <Link to={l.to} className="text-sm text-white/60 hover:text-primary transition-colors">{l.label}</Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-heading font-bold text-base mb-4">Support</h4>
            <ul className="space-y-2.5">
              {supportLinks.map((l) => (
                <li key={l.label}>
                  <Link to={l.to} className="text-sm text-white/60 hover:text-primary transition-colors">{l.label}</Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-heading font-bold text-base mb-4">Get the App</h4>
            <p className="text-sm text-white/60 mb-4">Coming soon on iOS & Android</p>
            <div className="space-y-2 mb-6">
              <div className="px-4 py-2.5 rounded-lg bg-white/10 text-sm text-white/50 text-center cursor-not-allowed">
                🍎 App Store — Coming Soon
              </div>
              <div className="px-4 py-2.5 rounded-lg bg-white/10 text-sm text-white/50 text-center cursor-not-allowed">
                🤖 Play Store — Coming Soon
              </div>
            </div>
            <form onSubmit={handleNotify} className="flex gap-2">
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
                placeholder="Get notified"
                className="flex-1 px-3 py-2 rounded-lg bg-white/10 text-sm text-white placeholder-white/40 border border-white/10 focus:border-primary focus:outline-none" />
              <button type="submit" className="p-2 bg-primary rounded-lg hover:bg-primary-dark transition-colors" aria-label="Subscribe">
                <Mail className="w-4 h-4" />
              </button>
            </form>
          </div>
        </div>
      </div>

      <div className="border-t border-white/10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5 flex flex-col sm:flex-row items-center justify-between gap-3">
          <p className="text-xs text-white/40">Made with 🐾 in India · © 2024 PetyMate Pvt Ltd</p>
          <p className="text-xs text-white/40">All rights reserved</p>
        </div>
      </div>
    </footer>
  );
}
