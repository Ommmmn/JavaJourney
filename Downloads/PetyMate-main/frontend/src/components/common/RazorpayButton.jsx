import { useState } from 'react';
import { Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';

export default function RazorpayButton({
  label, createOrderFn, verifyFn, onSuccess, onError,
  className = '', disabled = false, description = 'PetyMate Payment',
}) {
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  const handleClick = async () => {
    setLoading(true);
    try {
      const { data: orderRes } = await createOrderFn();
      const orderData = orderRes.data;

      const options = {
        key: orderData.keyId || 'rzp_test_your_key_id_here',
        amount: orderData.amount,
        currency: orderData.currency || 'INR',
        name: 'PetyMate',
        description,
        order_id: orderData.orderId || orderData.razorpayOrderId,
        prefill: {
          name: user?.name || '',
          email: user?.email || '',
          contact: user?.phone || '',
        },
        theme: { color: '#FF6B35' },
        handler: async (response) => {
          try {
            const verifyPayload = {
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            };
            const { data: verifyRes } = await verifyFn(verifyPayload);
            toast.success('Payment successful! 🎉');
            onSuccess?.(verifyRes.data);
          } catch (err) {
            toast.error('Payment verification failed');
            onError?.(err);
          }
        },
        modal: { ondismiss: () => setLoading(false) },
      };

      const rzp = new window.Razorpay(options);
      rzp.on('payment.failed', (resp) => {
        toast.error('Payment failed. Please try again.');
        onError?.(resp.error);
        setLoading(false);
      });
      rzp.open();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Could not create order');
      onError?.(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <button onClick={handleClick} disabled={loading || disabled}
      className={`inline-flex items-center justify-center gap-2 px-6 py-3 text-sm font-semibold text-white bg-primary hover:bg-primary-dark rounded-xl transition-all disabled:opacity-60 disabled:cursor-not-allowed ${className}`}>
      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
      {loading ? 'Processing...' : label}
    </button>
  );
}
