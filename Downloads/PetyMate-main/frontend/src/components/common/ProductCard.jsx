import { Star, ShoppingCart } from 'lucide-react';
import { useCart } from '../../context/CartContext';

export default function ProductCard({ product, onClick }) {
  const { addItem } = useCart();
  const discount = product.originalPrice && product.originalPrice > product.price
    ? Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100)
    : 0;

  const stockLabel = product.stockQty <= 0
    ? { text: 'Out of Stock', cls: 'bg-red-100 text-red-700' }
    : product.stockQty <= 5
    ? { text: 'Low Stock', cls: 'bg-amber-100 text-amber-700' }
    : { text: 'In Stock', cls: 'bg-green-100 text-green-700' };

  return (
    <div onClick={onClick}
      className="group bg-white rounded-card overflow-hidden shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 border border-pawgray-border cursor-pointer">
      <div className="relative aspect-square overflow-hidden bg-gray-50 p-4">
        <img src={product.photoUrl || 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=300'}
          alt={product.name} className="w-full h-full object-contain group-hover:scale-105 transition-transform duration-500" />
        {discount > 0 && (
          <span className="absolute top-3 left-3 text-xs font-bold px-2 py-1 rounded-badge bg-red-500 text-white">
            -{discount}%
          </span>
        )}
        {product.isFeatured && (
          <span className="absolute top-3 right-3 text-xs font-bold px-2 py-1 rounded-badge bg-amber-400 text-navy">
            ⭐ Featured
          </span>
        )}
      </div>
      <div className="p-4">
        <p className="text-[11px] text-navy/40 uppercase tracking-wider font-medium">{product.brand}</p>
        <h3 className="font-heading font-bold text-navy text-sm mt-1 line-clamp-2 min-h-[40px] group-hover:text-primary transition-colors">
          {product.name}
        </h3>
        {product.speciesTags && (
          <div className="flex flex-wrap gap-1 mt-1.5">
            {product.speciesTags.split(',').slice(0, 3).map((t) => (
              <span key={t} className="text-[10px] px-1.5 py-0.5 rounded bg-pawgray text-navy/50">{t.trim()}</span>
            ))}
          </div>
        )}
        <div className="flex items-center gap-1 mt-2">
          <Star className="w-3.5 h-3.5 text-amber-400 fill-current" />
          <span className="text-xs font-medium text-navy/60">{product.rating || '0.0'}</span>
          <span className="text-xs text-navy/30">({product.reviewCount || 0})</span>
        </div>
        <div className="flex items-baseline gap-2 mt-2">
          <span className="text-lg font-extrabold text-primary">₹{product.price}</span>
          {discount > 0 && (
            <span className="text-sm text-navy/30 line-through">₹{product.originalPrice}</span>
          )}
        </div>
        <div className="flex items-center justify-between mt-3">
          <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-badge ${stockLabel.cls}`}>{stockLabel.text}</span>
          <button
            onClick={(e) => { e.stopPropagation(); e.preventDefault(); if (product.stockQty > 0) addItem(product, 1); }}
            disabled={product.stockQty <= 0}
            className="p-2 rounded-xl bg-primary text-white hover:bg-primary-dark transition-colors disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed"
            aria-label="Add to cart"
          >
            <ShoppingCart className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
