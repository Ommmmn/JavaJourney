export default function SubscriptionBadge({ tier }) {
  if (!tier || tier === 'FREE') {
    return <span className="text-[10px] font-bold px-2 py-0.5 rounded-badge bg-gray-100 text-gray-500">FREE</span>;
  }
  if (tier === 'BASIC') {
    return <span className="text-[10px] font-bold px-2 py-0.5 rounded-badge bg-blue-100 text-blue-700">BASIC</span>;
  }
  return (
    <span className="text-[10px] font-bold px-2 py-0.5 rounded-badge bg-gradient-to-r from-amber-400 to-orange-500 text-white">
      ✨ PREMIUM
    </span>
  );
}
