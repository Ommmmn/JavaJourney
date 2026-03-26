import { Star } from 'lucide-react';

export default function StarRating({ value = 0, onChange, size = 'md', showValue = true }) {
  const sizes = { sm: 'w-3.5 h-3.5', md: 'w-5 h-5', lg: 'w-6 h-6' };
  const iconSize = sizes[size] || sizes.md;
  const interactive = typeof onChange === 'function';

  return (
    <div className="flex items-center gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <button key={star} type="button" disabled={!interactive}
          onClick={() => interactive && onChange(star)}
          className={`${interactive ? 'cursor-pointer hover:scale-110' : 'cursor-default'} transition-transform`}
          aria-label={`${star} star`}>
          <Star className={`${iconSize} ${star <= Math.round(value) ? 'text-amber-400 fill-amber-400' : 'text-gray-200'} transition-colors`} />
        </button>
      ))}
      {showValue && <span className="text-sm font-medium text-navy/60 ml-1">{Number(value).toFixed(1)}</span>}
    </div>
  );
}
