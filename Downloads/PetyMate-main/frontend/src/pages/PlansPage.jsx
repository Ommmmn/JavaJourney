import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { subscriptionService } from '../services/subscriptionService';
import RazorpayButton from '../components/common/RazorpayButton';
import PageTransition from '../components/common/PageTransition';
import { useAuth } from '../context/AuthContext';
import { Check, X, Shield, Zap, Crown, ChevronDown } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';

const plans = [
  {
    name: 'Free', price: 0, badge: 'Free Forever', badgeCls: 'bg-gray-100 text-gray-600',
    cardCls: 'bg-white border-gray-200', isPremium: false,
    features: [
      { t: 'Browse all pet listings', ok: true }, { t: '2 match requests/day', ok: true }, { t: 'Basic pet profile', ok: true },
      { t: 'Contact unlock', ok: false }, { t: 'Priority listing', ok: false }, { t: 'Trainer/vet discounts', ok: false }, { t: 'Unlimited unlocks', ok: false }, { t: 'Dedicated support', ok: false },
    ],
  },
  {
    name: 'Basic', tier: 'BASIC', price: 199, badge: 'Most Popular', badgeCls: 'bg-primary text-white',
    cardCls: 'bg-white border-primary ring-2 ring-primary/20', isPremium: false,
    features: [
      { t: 'Everything in Free', ok: true }, { t: '10 match requests/day', ok: true }, { t: '5 contact unlocks/month', ok: true },
      { t: 'View pet seller contacts', ok: true }, { t: 'Email match alerts', ok: true }, { t: 'Unlimited unlocks', ok: false }, { t: 'Priority listing', ok: false }, { t: 'Dedicated support', ok: false },
    ],
  },
  {
    name: 'Premium', tier: 'PREMIUM', price: 499, badge: 'Premium ✨', badgeCls: 'bg-white/20 text-white',
    cardCls: 'bg-gradient-to-br from-primary to-accent text-white border-transparent', isPremium: true,
    features: [
      { t: 'Everything in Basic', ok: true }, { t: 'Unlimited requests + unlocks', ok: true }, { t: 'Priority listing badge', ok: true },
      { t: 'Early access to new pets', ok: true }, { t: 'Trainer & vet discounts (10%)', ok: true }, { t: 'Dedicated support', ok: true },
    ],
  },
];

const faqs = [
  { q: 'Can I cancel my subscription anytime?', a: 'Yes, you can cancel anytime. Your benefits continue until the end of the billing period.' },
  { q: 'What payment methods do you accept?', a: 'We accept all major credit/debit cards, UPI, net banking, and wallets via Razorpay.' },
  { q: 'Is there a free trial?', a: 'Our Free plan is permanently free. Upgrade to Basic or Premium anytime for enhanced features.' },
  { q: 'How does contact unlock work?', a: 'For ₹99 per unlock, you get the pet owner\'s phone and email. Basic plan includes 5/month free.' },
  { q: 'What does Priority Listing mean?', a: 'Premium pets appear first in search results, increasing visibility by up to 5x.' },
  { q: 'Can I upgrade from Basic to Premium?', a: 'Yes! Pay only the difference. Your unlock balance carries over.' },
  { q: 'Are payments secure?', a: 'All payments are processed through Razorpay, India\'s leading payment gateway with bank-grade security.' },
  { q: 'What about refunds?', a: 'We offer full refunds within 7 days of purchase if you\'re not satisfied.' },
];

export default function PlansPage() {
  const { user, fetchUser } = useAuth();
  const [openFaq, setOpenFaq] = useState(null);

  const handleSuccess = () => {
    toast.success('Subscription activated! 🎉');
    fetchUser();
  };

  return (
    <PageTransition>
      <section className="bg-gradient-to-br from-orange-50 to-purple-50 py-16 md:py-24">
        <div className="max-w-5xl mx-auto px-4">
          <div className="text-center mb-14">
            <h1 className="font-heading font-extrabold text-4xl md:text-5xl text-navy mb-4">Choose Your Plan</h1>
            <p className="text-navy/50 max-w-xl mx-auto">Unlock the full potential of PetyMate with a plan that fits your needs</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16">
            {plans.map((p) => (
              <motion.div key={p.name} whileHover={{ y: -6 }}
                className={`relative rounded-card p-7 border-2 ${p.cardCls} shadow-sm hover:shadow-xl transition-all`}>
                {p.name === 'Basic' && <div className="absolute -top-3 left-1/2 -translate-x-1/2 px-3 py-0.5 bg-primary text-white text-xs font-bold rounded-pill">POPULAR</div>}
                <span className={`inline-block text-xs font-bold px-3 py-1 rounded-pill ${p.badgeCls} mb-4`}>{p.badge}</span>
                <div className="mb-5">
                  <span className={`text-4xl font-heading font-extrabold ${p.isPremium ? 'text-white' : 'text-navy'}`}>₹{p.price}</span>
                  {p.price > 0 && <span className={`text-sm ${p.isPremium ? 'text-white/80' : 'text-navy/50'}`}>/month</span>}
                </div>
                <ul className="space-y-3 mb-7">
                  {p.features.map((f) => (
                    <li key={f.t} className={`flex items-center gap-2 text-sm ${p.isPremium ? (f.ok ? 'text-white' : 'text-white/40') : (f.ok ? 'text-navy/70' : 'text-navy/30')}`}>
                      {f.ok ? <Check className="w-4 h-4 text-green-500 flex-shrink-0" /> : <X className="w-4 h-4 flex-shrink-0 opacity-40" />}
                      {f.t}
                    </li>
                  ))}
                </ul>
                {p.price === 0 ? (
                  <Link to="/auth/register" className="block w-full text-center py-3 rounded-xl text-sm font-semibold bg-navy text-white hover:bg-navy-muted transition-all">Get Started</Link>
                ) : (
                  <RazorpayButton
                    label={p.isPremium ? 'Go Premium 👑' : 'Subscribe Now'}
                    description={`${p.name} Plan - PetyMate`}
                    createOrderFn={() => subscriptionService.createOrder(p.tier)}
                    verifyFn={(data) => subscriptionService.verify({ ...data, plan: p.tier })}
                    onSuccess={handleSuccess}
                    className={`w-full ${p.isPremium ? 'bg-white text-primary hover:bg-white/90' : 'bg-primary text-white hover:bg-primary-dark'}`}
                  />
                )}
              </motion.div>
            ))}
          </div>

          <div className="max-w-2xl mx-auto">
            <h2 className="font-heading font-extrabold text-2xl text-navy text-center mb-8">Frequently Asked Questions</h2>
            <div className="space-y-3">
              {faqs.map((f, i) => (
                <div key={i} className="bg-white rounded-card border border-pawgray-border overflow-hidden">
                  <button onClick={() => setOpenFaq(openFaq === i ? null : i)}
                    className="w-full flex items-center justify-between p-4 text-left">
                    <span className="font-medium text-navy text-sm">{f.q}</span>
                    <ChevronDown className={`w-4 h-4 text-navy/40 transition-transform ${openFaq === i ? 'rotate-180' : ''}`} />
                  </button>
                  {openFaq === i && (
                    <div className="px-4 pb-4 text-sm text-navy/60">{f.a}</div>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="text-center mt-12">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-white rounded-pill border border-pawgray-border text-sm text-navy/50">
              <Shield className="w-4 h-4 text-green-500" />
              Secured by Razorpay — bank-grade encryption
            </div>
          </div>
        </div>
      </section>
    </PageTransition>
  );
}
