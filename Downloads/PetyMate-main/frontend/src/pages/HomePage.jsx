import { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Search, ArrowRight, PawPrint, Heart, MessageCircle, Shield, Star, Stethoscope, GraduationCap, ShoppingBag, Package, ListPlus, Crown, ChevronRight } from 'lucide-react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Autoplay } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';
import PageTransition from '../components/common/PageTransition';

const heroWords = ['Match', 'Trainer', 'Vet', 'Home', 'Family'];

const stats = [
  { value: 50000, label: 'Happy Pets', suffix: '+' },
  { value: 10000, label: 'Successful Matches', suffix: '+' },
  { value: 500, label: 'Verified Vets', suffix: '+' },
  { value: 1000, label: 'Certified Trainers', suffix: '+' },
];

const steps = [
  { num: 1, icon: '🐾', title: 'Create Pet Profile', desc: 'Add photos, health info, and mating preferences for your pet' },
  { num: 2, icon: '🔍', title: 'Discover Matches', desc: 'Browse area-based matches filtered to your city and preferences' },
  { num: 3, icon: '💬', title: 'Connect & Meet', desc: 'Unlock contact details for just ₹99 and meet your pet\'s match' },
];

const services = [
  { icon: '🐾', title: 'Find Mating Partner', desc: 'Area-based matches with compatibility filters', to: '/match', color: 'bg-orange-50' },
  { icon: '🏠', title: 'Buy or Adopt a Pet', desc: 'Browse verified breeders and adoption listings', to: '/shop', color: 'bg-green-50' },
  { icon: '📦', title: 'Pet Products', desc: 'Food, toys, grooming — delivered to your door', to: '/products', color: 'bg-purple-50' },
  { icon: '🩺', title: 'Consult a Vet', desc: 'Online or clinic appointments with verified vets', to: '/vets', color: 'bg-teal-50' },
  { icon: '🎓', title: 'Find a Trainer', desc: 'Obedience, agility, behavior — expert trainers', to: '/trainers', color: 'bg-red-50' },
  { icon: '💊', title: 'Pet Health Tips', desc: 'Expert advice on nutrition, care, and wellness', to: '#', color: 'bg-indigo-50' },
  { icon: '📋', title: 'List Your Pet', desc: 'Reach thousands of pet owners in your area', to: '/pets/new', color: 'bg-pink-50' },
  { icon: '👑', title: 'Go Premium', desc: 'Unlimited matches, priority listing, exclusive badge', to: '/plans', color: 'bg-amber-50' },
];

const speciesCards = [
  { name: 'Dogs', species: 'DOG', img: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=300' },
  { name: 'Cats', species: 'CAT', img: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=300' },
  { name: 'Rabbits', species: 'RABBIT', img: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=300' },
  { name: 'Birds', species: 'BIRD', img: 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=300' },
  { name: 'Fish', species: 'FISH', img: 'https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=300' },
  { name: 'Hamsters', species: 'HAMSTER', img: 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=300' },
];

const testimonials = [
  { quote: 'PetyMate helped us find the perfect mate for our Labrador. The process was so smooth and the area-based matching is brilliant!', name: 'Priya Sharma', city: 'Mumbai', pet: 'Bruno (Labrador)', img: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=100' },
  { quote: 'I adopted my Persian cat through PetyMate. The verified listings gave me confidence. Best decision ever!', name: 'Rahul Gupta', city: 'Delhi', pet: 'Misha (Persian Cat)', img: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=100' },
  { quote: 'The trainers on PetyMate are exceptional. Our GSD\'s behavior improved dramatically in just 4 sessions!', name: 'Ananya Reddy', city: 'Hyderabad', pet: 'Rex (German Shepherd)', img: 'https://images.unsplash.com/photo-1550697851-920b181d8ca8?w=100' },
  { quote: 'Convenient vet consultations online. Saved us a trip and got great advice for our Beagle\'s diet.', name: 'Vikram Singh', city: 'Jaipur', pet: 'Coco (Beagle)', img: 'https://images.unsplash.com/photo-1534361960057-19f4e2c040da?w=100' },
  { quote: 'Premium plan is totally worth it. Unlimited match requests and priority listing helped us find partners quickly.', name: 'Sneha Patel', city: 'Ahmedabad', pet: 'Goldie (Golden Retriever)', img: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=100' },
  { quote: 'The product store has everything! Quality pet food delivered right to our doorstep. My parrot loves the new treats!', name: 'Arjun Nair', city: 'Kochi', pet: 'Kiwi (Indian Ringneck)', img: 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=100' },
];

const plans = [
  {
    name: 'Free', price: 0, badge: 'Free Forever', badgeCls: 'bg-gray-100 text-gray-600',
    cardCls: 'bg-white border-gray-200', btnCls: 'bg-navy text-white hover:bg-navy-muted', btnLabel: 'Get Started', to: '/auth/register',
    features: [
      { text: 'Browse all pet listings', ok: true },
      { text: '2 match requests/day', ok: true },
      { text: 'Basic pet profile', ok: true },
      { text: 'Contact unlock', ok: false },
      { text: 'Priority listing', ok: false },
      { text: 'Trainer/vet discounts', ok: false },
    ],
  },
  {
    name: 'Basic', price: 199, badge: 'Most Popular', badgeCls: 'bg-primary text-white',
    cardCls: 'bg-white border-primary ring-2 ring-primary/20', btnCls: 'bg-primary text-white hover:bg-primary-dark', btnLabel: 'Subscribe Now', to: '/plans',
    features: [
      { text: 'Everything in Free', ok: true },
      { text: '10 match requests/day', ok: true },
      { text: '5 contact unlocks/month', ok: true },
      { text: 'View pet seller contacts', ok: true },
      { text: 'Email match alerts', ok: true },
      { text: 'Unlimited unlocks', ok: false },
      { text: 'Priority listing', ok: false },
    ],
  },
  {
    name: 'Premium', price: 499, badge: 'Premium ✨', badgeCls: 'bg-white/20 text-white',
    cardCls: 'bg-gradient-to-br from-primary to-accent text-white border-transparent', btnCls: 'bg-white text-primary hover:bg-white/90', btnLabel: 'Go Premium 👑', to: '/plans',
    features: [
      { text: 'Everything in Basic', ok: true },
      { text: 'Unlimited requests + unlocks', ok: true },
      { text: 'Priority listing badge', ok: true },
      { text: 'Early access to new pets', ok: true },
      { text: 'Trainer & vet discounts (10%)', ok: true },
      { text: 'Dedicated support', ok: true },
    ],
  },
];

function useCountUp(target, duration, isVisible) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (!isVisible) return;
    let start = 0;
    const step = target / (duration / 16);
    const timer = setInterval(() => {
      start += step;
      if (start >= target) { setCount(target); clearInterval(timer); }
      else setCount(Math.floor(start));
    }, 16);
    return () => clearInterval(timer);
  }, [target, duration, isVisible]);
  return count;
}

function StatItem({ value, label, suffix }) {
  const ref = useRef(null);
  const [visible, setVisible] = useState(false);
  useEffect(() => {
    const obs = new IntersectionObserver(([e]) => { if (e.isIntersecting) setVisible(true); }, { threshold: 0.3 });
    if (ref.current) obs.observe(ref.current);
    return () => obs.disconnect();
  }, []);
  const count = useCountUp(value, 2000, visible);
  return (
    <div ref={ref} className="text-center">
      <p className="text-3xl md:text-4xl font-heading font-extrabold text-navy">{count.toLocaleString()}{suffix}</p>
      <p className="text-sm text-navy/50 mt-1">{label}</p>
    </div>
  );
}

export default function HomePage() {
  const [wordIndex, setWordIndex] = useState(0);
  const [species, setSpecies] = useState('');
  const [city, setCity] = useState('');
  const [radius, setRadius] = useState(50);
  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => setWordIndex((i) => (i + 1) % heroWords.length), 2000);
    return () => clearInterval(interval);
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (species) params.set('species', species);
    if (city) params.set('city', city);
    params.set('radiusKm', radius);
    navigate(`/match?${params.toString()}`);
  };

  return (
    <PageTransition>
      {/* HERO */}
      <section className="relative min-h-screen flex items-center justify-center gradient-hero overflow-hidden">
        <svg className="absolute bottom-10 left-10 w-32 h-32 text-white/10 animate-bob1" viewBox="0 0 100 100" fill="currentColor"><path d="M50 20c-5 0-10-5-15-5s-10 5-15 15c-5 10-5 20 0 30s15 20 30 20 25-10 30-20 5-20 0-30c-5-10-10-15-15-15s-10 5-15 5z"/></svg>
        <svg className="absolute top-20 right-16 w-24 h-24 text-white/10 animate-bob2" viewBox="0 0 100 100" fill="currentColor"><ellipse cx="50" cy="45" rx="25" ry="30"/><ellipse cx="30" cy="15" rx="10" ry="15" transform="rotate(-20,30,15)"/><ellipse cx="70" cy="15" rx="10" ry="15" transform="rotate(20,70,15)"/></svg>
        <svg className="absolute top-1/3 left-8 w-16 h-16 text-white/10 animate-bob3" viewBox="0 0 100 100" fill="currentColor"><circle cx="35" cy="35" r="12"/><circle cx="65" cy="35" r="12"/><circle cx="35" cy="65" r="12"/><circle cx="65" cy="65" r="12"/><circle cx="50" cy="50" r="18"/></svg>
        <svg className="absolute top-16 left-1/4 w-20 h-20 text-white/10 animate-bob4" viewBox="0 0 100 100" fill="currentColor"><path d="M50 10L90 90H10z"/></svg>
        <svg className="absolute bottom-20 right-10 w-20 h-20 text-white/10 animate-bob5" viewBox="0 0 100 100" fill="currentColor"><circle cx="35" cy="35" r="12"/><circle cx="65" cy="35" r="12"/><circle cx="35" cy="65" r="12"/><circle cx="65" cy="65" r="12"/><circle cx="50" cy="50" r="18"/></svg>

        <div className="relative z-10 text-center text-white px-4 max-w-4xl mx-auto">
          <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }}
            className="inline-block px-4 py-1.5 rounded-pill bg-white/15 backdrop-blur-sm text-sm font-medium mb-6">
            🐾 India's #1 Pet Platform
          </motion.div>

          <motion.h1 initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.15 }}
            className="font-heading font-extrabold text-4xl sm:text-5xl md:text-6xl lg:text-7xl leading-tight mb-6">
            Find Your Pet's<br />
            Perfect{' '}
            <span key={wordIndex} className="inline-block text-yellow-300 transition-opacity duration-300">
              {heroWords[wordIndex]}
            </span>
          </motion.h1>

          <motion.p initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.3 }}
            className="text-lg md:text-xl text-white/80 max-w-2xl mx-auto mb-10">
            Area-based pet matchmaking · Trusted trainers · Verified vets · ₹99 to connect
          </motion.p>

          <motion.form onSubmit={handleSearch} initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6, delay: 0.45 }}
            className="glassmorphism p-4 md:p-5 max-w-2xl mx-auto">
            <div className="flex flex-col md:flex-row gap-3">
              <select value={species} onChange={(e) => setSpecies(e.target.value)}
                className="flex-1 px-4 py-3 rounded-xl bg-white/90 text-navy text-sm font-medium focus:outline-none focus:ring-2 focus:ring-primary" aria-label="Select species">
                <option value="">All Species</option>
                <option value="DOG">🐕 Dog</option>
                <option value="CAT">🐈 Cat</option>
                <option value="RABBIT">🐇 Rabbit</option>
                <option value="BIRD">🐦 Bird</option>
                <option value="FISH">🐟 Fish</option>
                <option value="HAMSTER">🐹 Hamster</option>
                <option value="OTHER">🐾 Other</option>
              </select>
              <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="Enter your city..."
                className="flex-1 px-4 py-3 rounded-xl bg-white/90 text-navy text-sm placeholder-navy/40 focus:outline-none focus:ring-2 focus:ring-primary" />
              <button type="submit" className="px-6 py-3 bg-primary hover:bg-primary-dark text-white font-semibold rounded-xl transition-colors flex items-center justify-center gap-2 whitespace-nowrap">
                <Search className="w-4 h-4" /> Find Match 🐾
              </button>
            </div>
            <div className="mt-3 flex items-center gap-3 text-white/70 text-sm">
              <span>Radius: {radius} km</span>
              <input type="range" min="10" max="500" step="10" value={radius} onChange={(e) => setRadius(e.target.value)}
                className="flex-1 accent-primary" aria-label="Search radius" />
            </div>
          </motion.form>

          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.6 }}
            className="flex flex-wrap items-center justify-center gap-3 mt-8">
            <Link to="/pets/new" className="px-5 py-2.5 border-2 border-white/50 text-white hover:bg-white/10 rounded-pill text-sm font-semibold transition-all">
              List Your Pet
            </Link>
            <Link to="/trainers" className="px-5 py-2.5 border-2 border-white/50 text-white hover:bg-white/10 rounded-pill text-sm font-semibold transition-all">
              Browse Trainers 🎓
            </Link>
          </motion.div>
        </div>
      </section>

      {/* STATS */}
      <section className="bg-warm py-12 md:py-16">
        <div className="max-w-5xl mx-auto px-4 grid grid-cols-2 md:grid-cols-4 gap-8">
          {stats.map((s) => <StatItem key={s.label} {...s} />)}
        </div>
      </section>

      {/* HOW IT WORKS */}
      <section className="bg-white py-16 md:py-24">
        <div className="max-w-5xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-heading font-extrabold text-center text-navy mb-14">How PetyMate Works</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
            <div className="hidden md:block absolute top-16 left-1/4 right-1/4 border-t-2 border-dashed border-primary/20" />
            {steps.map((s) => (
              <motion.div key={s.num} whileHover={{ y: -8 }}
                className="relative bg-white rounded-card p-8 text-center shadow-sm border border-pawgray-border hover:shadow-lg transition-all">
                <div className="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center text-lg font-bold mx-auto mb-4">
                  {s.num}
                </div>
                <span className="text-3xl mb-3 block">{s.icon}</span>
                <h3 className="font-heading font-bold text-navy text-lg mb-2">{s.title}</h3>
                <p className="text-sm text-navy/50">{s.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* SERVICES */}
      <section className="bg-white py-16 md:py-24">
        <div className="max-w-6xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-heading font-extrabold text-center text-navy mb-4">Everything Your Pet Needs, In One Place</h2>
          <p className="text-center text-navy/50 mb-14 max-w-xl mx-auto">From matchmaking to healthcare — we've got every pet covered</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
            {services.map((s) => (
              <Link key={s.title} to={s.to}>
                <motion.div whileHover={{ scale: 1.03 }}
                  className="bg-white rounded-card p-6 shadow-sm border border-pawgray-border hover:shadow-lg transition-all h-full">
                  <div className={`w-12 h-12 rounded-xl ${s.color} flex items-center justify-center text-2xl mb-4`}>
                    {s.icon}
                  </div>
                  <h3 className="font-heading font-bold text-navy mb-2">{s.title}</h3>
                  <p className="text-sm text-navy/50">{s.desc}</p>
                </motion.div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* PLANS */}
      <section className="bg-gradient-to-br from-orange-50 to-purple-50 py-16 md:py-24">
        <div className="max-w-5xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-heading font-extrabold text-center text-navy mb-14">Choose Your Plan</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {plans.map((p) => (
              <motion.div key={p.name} whileHover={{ y: -6 }}
                className={`relative rounded-card p-7 border-2 ${p.cardCls} shadow-sm hover:shadow-xl transition-all`}>
                <span className={`inline-block text-xs font-bold px-3 py-1 rounded-pill ${p.badgeCls} mb-4`}>{p.badge}</span>
                <div className="mb-5">
                  <span className={`text-4xl font-heading font-extrabold ${p.name === 'Premium' ? 'text-white' : 'text-navy'}`}>₹{p.price}</span>
                  {p.price > 0 && <span className={`text-sm ${p.name === 'Premium' ? 'text-white/80' : 'text-navy/50'}`}>/month</span>}
                </div>
                <ul className="space-y-3 mb-7">
                  {p.features.map((f) => (
                    <li key={f.text} className={`flex items-center gap-2 text-sm ${p.name === 'Premium' ? (f.ok ? 'text-white' : 'text-white/40') : (f.ok ? 'text-navy/70' : 'text-navy/30')}`}>
                      <span className={`text-base ${f.ok ? '✓' : '✗'}`}>{f.ok ? '✓' : '✗'}</span>
                      {f.text}
                    </li>
                  ))}
                </ul>
                <Link to={p.to} className={`block w-full text-center py-3 rounded-xl text-sm font-semibold transition-all ${p.btnCls}`}>
                  {p.btnLabel}
                </Link>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* SPECIES */}
      <section className="bg-navy py-16 text-white">
        <div className="max-w-6xl mx-auto px-4">
          <h2 className="text-3xl font-heading font-extrabold text-center mb-10">We Speak Every Pet's Language</h2>
          <div className="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
            {speciesCards.map((s) => (
              <Link key={s.species} to={`/match?species=${s.species}`}
                className="flex-shrink-0 w-40 rounded-2xl overflow-hidden group relative">
                <img src={s.img} alt={s.name} className="w-full h-48 object-cover group-hover:scale-110 transition-transform duration-500" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent" />
                <div className="absolute bottom-3 left-3 right-3">
                  <p className="font-heading font-bold">{s.name}</p>
                  <p className="text-xs text-white/70 flex items-center gap-1">Explore <ChevronRight className="w-3 h-3" /></p>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* TESTIMONIALS */}
      <section className="bg-gradient-to-b from-warm to-white py-16 md:py-24">
        <div className="max-w-4xl mx-auto px-4">
          <h2 className="text-3xl md:text-4xl font-heading font-extrabold text-center text-navy mb-14">Happy Pet Families</h2>
          <Swiper modules={[Navigation, Autoplay]} navigation autoplay={{ delay: 3000 }} loop spaceBetween={24} slidesPerView={1}
            breakpoints={{ 768: { slidesPerView: 2 } }} className="pb-10">
            {testimonials.map((t, i) => (
              <SwiperSlide key={i}>
                <div className="bg-white rounded-card p-6 shadow-sm border border-pawgray-border h-full">
                  <p className="text-sm text-navy/70 italic mb-4">"{t.quote}"</p>
                  <div className="flex items-center gap-3">
                    <img src={t.img} alt={t.name} className="w-10 h-10 rounded-full object-cover border-2 border-primary/20" />
                    <div>
                      <p className="font-heading font-bold text-navy text-sm">{t.name}</p>
                      <p className="text-xs text-navy/40">{t.city} · {t.pet}</p>
                    </div>
                  </div>
                </div>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      </section>

      {/* APP BANNER */}
      <section className="bg-primary py-16 text-white">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <h2 className="text-3xl font-heading font-extrabold mb-3">PetyMate App Coming Soon 📱</h2>
          <p className="text-white/80 mb-8">Get notified when we launch on iOS and Android</p>
          <form onSubmit={(e) => { e.preventDefault(); import('react-hot-toast').then(m => m.default.success('We\'ll notify you! 📱')); }}
            className="flex gap-3 max-w-md mx-auto">
            <input type="email" placeholder="Enter your email..." required
              className="flex-1 px-4 py-3 rounded-xl bg-white/20 text-white placeholder-white/50 border border-white/20 focus:outline-none focus:ring-2 focus:ring-white/50" />
            <button type="submit" className="px-6 py-3 bg-white text-primary font-semibold rounded-xl hover:bg-white/90 transition-colors">
              Notify Me
            </button>
          </form>
        </div>
      </section>
    </PageTransition>
  );
}
