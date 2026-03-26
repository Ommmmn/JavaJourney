import { Link } from 'react-router-dom';
import { PawPrint, Home, ArrowLeft } from 'lucide-react';
import PageTransition from '../components/common/PageTransition';

export default function NotFoundPage() {
  return (
    <PageTransition>
      <div className="min-h-[80vh] flex items-center justify-center px-4">
        <div className="text-center max-w-md">
          <div className="relative inline-block mb-6">
            <span className="text-[120px] font-heading font-extrabold text-primary/10 leading-none select-none">404</span>
            <PawPrint className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-16 h-16 text-primary" />
          </div>
          <h1 className="font-heading font-extrabold text-3xl text-navy mb-3">Oops! Page Not Found</h1>
          <p className="text-navy/50 mb-8">Looks like this page went for a walk and didn't come back. Let's get you home!</p>
          <div className="flex items-center justify-center gap-3">
            <Link to="/" className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark transition-colors">
              <Home className="w-4 h-4" /> Go Home
            </Link>
            <button onClick={() => window.history.back()} className="inline-flex items-center gap-2 px-6 py-3 border border-pawgray-border text-navy/60 font-medium rounded-xl hover:bg-pawgray transition-all">
              <ArrowLeft className="w-4 h-4" /> Go Back
            </button>
          </div>
        </div>
      </div>
    </PageTransition>
  );
}
