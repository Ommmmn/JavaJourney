import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productService } from '../services/productService';
import { useCart } from '../context/CartContext';
import { orderService } from '../services/orderService';
import ProductCard from '../components/common/ProductCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import PageTransition from '../components/common/PageTransition';
import RazorpayButton from '../components/common/RazorpayButton';
import { ShoppingCart, X, Minus, Plus, Trash2, Search, Package } from 'lucide-react';
import toast from 'react-hot-toast';

const categories = [
  { label: 'All', value: '', icon: '🛒' },
  { label: 'Food', value: 'FOOD', icon: '🥘' },
  { label: 'Toys', value: 'TOYS', icon: '🎾' },
  { label: 'Grooming', value: 'GROOMING', icon: '✂️' },
  { label: 'Medicine', value: 'MEDICINE', icon: '💊' },
  { label: 'Accessories', value: 'ACCESSORIES', icon: '🎀' },
  { label: 'Housing', value: 'HOUSING', icon: '🏠' },
];

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState('');
  const [search, setSearch] = useState('');
  const [sortBy, setSortBy] = useState('NEWEST');
  const [cartOpen, setCartOpen] = useState(false);
  const { items, total, itemCount, removeItem, updateQty, clearCart } = useCart();
  const [shippingInfo, setShippingInfo] = useState({ shippingName: '', shippingPhone: '', shippingAddress: '', shippingCity: '', shippingState: '', shippingPincode: '' });

  const fetchProducts = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12, sortBy };
      if (category) params.category = category;
      if (search) params.search = search;
      const { data } = await productService.getProducts(params);
      const result = data.data;
      if (p === 0) setProducts(result.content || []);
      else setProducts((prev) => [...prev, ...(result.content || [])]);
      setTotalElements(result.totalElements || 0);
      setPage(p);
    } catch { setProducts([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchProducts(0); }, [category, sortBy]);

  const handleSearch = (e) => { e.preventDefault(); fetchProducts(0); };
  const shippingCost = total >= 499 ? 0 : 49;
  const grandTotal = total + shippingCost;

  return (
    <PageTransition>
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="font-heading font-extrabold text-2xl text-navy">Pet Products</h1>
          <button onClick={() => setCartOpen(true)} className="relative p-3 bg-white rounded-xl border border-pawgray-border hover:border-primary/30 transition-all" aria-label="Open cart">
            <ShoppingCart className="w-5 h-5 text-navy/60" />
            {itemCount > 0 && <span className="absolute -top-1 -right-1 w-5 h-5 bg-primary text-white text-[10px] font-bold rounded-full flex items-center justify-center">{itemCount}</span>}
          </button>
        </div>

        <div className="flex flex-wrap gap-2 mb-4 overflow-x-auto pb-2">
          {categories.map((c) => (
            <button key={c.value} onClick={() => setCategory(c.value)}
              className={`px-4 py-2 rounded-pill text-sm font-medium whitespace-nowrap transition-all ${category === c.value ? 'bg-primary text-white' : 'bg-white text-navy/60 border border-pawgray-border hover:border-primary/30'}`}>
              {c.icon} {c.label}
            </button>
          ))}
        </div>

        <div className="flex gap-3 mb-6">
          <form onSubmit={handleSearch} className="flex-1 flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-navy/30" />
              <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search products..."
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
            </div>
            <button type="submit" className="px-4 py-2.5 bg-primary text-white rounded-xl text-sm font-medium hover:bg-primary-dark">Search</button>
          </form>
          <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-2.5 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30">
            <option value="NEWEST">Newest</option>
            <option value="PRICE_ASC">Price: Low → High</option>
            <option value="PRICE_DESC">Price: High → Low</option>
            <option value="RATING">Top Rated</option>
            <option value="POPULAR">Popular</option>
          </select>
        </div>

        {loading && page === 0 ? (
          <LoadingSpinner text="Loading products..." />
        ) : products.length === 0 ? (
          <EmptyState icon={Package} title="No products found" description="Try different search or category" />
        ) : (
          <>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
              {products.map((p) => <ProductCard key={p.id} product={p} />)}
            </div>
            {products.length < totalElements && (
              <div className="text-center mt-8">
                <button onClick={() => fetchProducts(page + 1)} disabled={loading}
                  className="px-8 py-3 bg-white border border-pawgray-border text-navy font-medium rounded-xl hover:bg-pawgray transition-all">
                  Load More
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Cart Drawer */}
      {cartOpen && (
        <div className="fixed inset-0 z-50">
          <div className="absolute inset-0 bg-black/40" onClick={() => setCartOpen(false)} />
          <div className="absolute right-0 top-0 bottom-0 w-full max-w-md bg-white shadow-2xl flex flex-col">
            <div className="flex items-center justify-between p-5 border-b border-pawgray-border">
              <h3 className="font-heading font-bold text-navy text-lg">Cart ({itemCount})</h3>
              <button onClick={() => setCartOpen(false)} className="p-1 text-navy/40 hover:text-navy" aria-label="Close cart"><X className="w-5 h-5" /></button>
            </div>
            <div className="flex-1 overflow-y-auto p-5">
              {items.length === 0 ? (
                <div className="text-center py-16">
                  <ShoppingCart className="w-12 h-12 text-navy/10 mx-auto mb-3" />
                  <p className="text-sm text-navy/40">Your cart is empty</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {items.map((item) => (
                    <div key={item.product.id} className="flex gap-3 bg-pawgray rounded-xl p-3">
                      <img src={item.product.photoUrl || 'https://via.placeholder.com/80'} alt={item.product.name} className="w-16 h-16 rounded-lg object-contain bg-white" />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-navy truncate">{item.product.name}</p>
                        <p className="text-sm font-bold text-primary mt-1">₹{item.price}</p>
                        <div className="flex items-center gap-2 mt-1.5">
                          <button onClick={() => updateQty(item.product.id, item.qty - 1)} className="w-7 h-7 rounded-lg bg-white flex items-center justify-center hover:bg-primary/5" aria-label="Decrease">
                            <Minus className="w-3 h-3" />
                          </button>
                          <span className="text-sm font-medium w-6 text-center">{item.qty}</span>
                          <button onClick={() => updateQty(item.product.id, item.qty + 1)} className="w-7 h-7 rounded-lg bg-white flex items-center justify-center hover:bg-primary/5" aria-label="Increase">
                            <Plus className="w-3 h-3" />
                          </button>
                          <button onClick={() => removeItem(item.product.id)} className="ml-auto p-1.5 text-red-400 hover:text-red-600" aria-label="Remove">
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              {items.length > 0 && (
                <div className="mt-6 space-y-3">
                  <h4 className="font-heading font-bold text-navy text-sm">Shipping Address</h4>
                  {['shippingName','shippingPhone','shippingAddress','shippingCity','shippingState','shippingPincode'].map((f) => (
                    <input key={f} value={shippingInfo[f]} onChange={(e) => setShippingInfo((p) => ({ ...p, [f]: e.target.value }))}
                      placeholder={f.replace('shipping', '').replace(/([A-Z])/g, ' $1').trim()}
                      className="w-full px-3 py-2 rounded-lg border border-pawgray-border text-sm focus:outline-none focus:ring-1 focus:ring-primary" />
                  ))}
                </div>
              )}
            </div>
            {items.length > 0 && (
              <div className="border-t border-pawgray-border p-5">
                <div className="space-y-2 mb-4 text-sm">
                  <div className="flex justify-between"><span className="text-navy/50">Subtotal</span><span className="font-medium text-navy">₹{total.toFixed(0)}</span></div>
                  <div className="flex justify-between"><span className="text-navy/50">Shipping</span><span className="font-medium text-navy">{shippingCost === 0 ? 'Free' : `₹${shippingCost}`}</span></div>
                  <div className="flex justify-between border-t border-pawgray-border pt-2"><span className="font-semibold text-navy">Total</span><span className="font-bold text-primary text-lg">₹{grandTotal.toFixed(0)}</span></div>
                  {total < 499 && <p className="text-[11px] text-navy/40">Add ₹{(499 - total).toFixed(0)} more for free shipping</p>}
                </div>
                <RazorpayButton
                  label={`Place Order · ₹${grandTotal.toFixed(0)}`}
                  description="Product Order - PetyMate"
                  createOrderFn={() => orderService.createCartOrder({
                    items: items.map((i) => ({ productId: i.product.id, qty: i.qty })),
                    shippingAddress: shippingInfo,
                  })}
                  verifyFn={(data) => orderService.verifyCartOrder(data)}
                  onSuccess={() => { clearCart(); setCartOpen(false); toast.success('Order placed! 🎉'); }}
                  className="w-full"
                />
              </div>
            )}
          </div>
        </div>
      )}
    </PageTransition>
  );
}
