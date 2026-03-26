import { PawPrint } from 'lucide-react';

export default function LoadingSpinner({ text = 'Loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-20">
      <div className="relative">
        <PawPrint className="w-12 h-12 text-primary animate-spin" />
      </div>
      <p className="mt-4 text-sm text-navy/50 font-medium">{text}</p>
    </div>
  );
}
