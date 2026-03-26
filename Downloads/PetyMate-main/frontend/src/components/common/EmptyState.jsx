import { SearchX } from 'lucide-react';

export default function EmptyState({ icon: Icon = SearchX, title = 'Nothing found', description = '', actionLabel, onAction }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center max-w-md mx-auto">
      <div className="w-20 h-20 rounded-full bg-pawgray flex items-center justify-center mb-5">
        <Icon className="w-10 h-10 text-navy/20" />
      </div>
      <h3 className="font-heading font-bold text-xl text-navy mb-2">{title}</h3>
      {description && <p className="text-sm text-navy/50 mb-6">{description}</p>}
      {actionLabel && onAction && (
        <button onClick={onAction} className="px-6 py-2.5 text-sm font-semibold text-white bg-primary hover:bg-primary-dark rounded-xl transition-colors">
          {actionLabel}
        </button>
      )}
    </div>
  );
}
