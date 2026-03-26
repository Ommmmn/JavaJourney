import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, ChevronLeft, ChevronRight } from 'lucide-react';

export default function PhotoGallery({ photos = [], alt = 'Pet photo' }) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [lightboxOpen, setLightboxOpen] = useState(false);

  if (!photos.length) return null;
  const sortedPhotos = [...photos].sort((a, b) => (b.isPrimary ? 1 : 0) - (a.isPrimary ? 1 : 0));
  const urls = sortedPhotos.map((p) => (typeof p === 'string' ? p : p.photoUrl));

  const prev = () => setSelectedIndex((i) => (i === 0 ? urls.length - 1 : i - 1));
  const next = () => setSelectedIndex((i) => (i === urls.length - 1 ? 0 : i + 1));

  return (
    <>
      <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
        <div className="md:col-span-3 aspect-[4/3] rounded-2xl overflow-hidden cursor-pointer bg-gray-100"
          onClick={() => setLightboxOpen(true)}>
          <img src={urls[selectedIndex]} alt={alt} className="w-full h-full object-cover hover:scale-105 transition-transform duration-500" />
        </div>
        <div className="md:col-span-2 grid grid-cols-2 gap-3">
          {urls.slice(0, 4).map((url, i) => (
            <div key={i} onClick={() => { setSelectedIndex(i); }}
              className={`aspect-square rounded-xl overflow-hidden cursor-pointer bg-gray-100 border-2 transition-all ${i === selectedIndex ? 'border-primary' : 'border-transparent hover:border-primary/30'}`}>
              <img src={url} alt={`${alt} ${i + 1}`} className="w-full h-full object-cover" />
            </div>
          ))}
        </div>
      </div>

      <AnimatePresence>
        {lightboxOpen && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 z-[100] bg-black/90 flex items-center justify-center"
            onClick={() => setLightboxOpen(false)}>
            <button onClick={() => setLightboxOpen(false)} className="absolute top-4 right-4 p-2 text-white/80 hover:text-white z-10" aria-label="Close">
              <X className="w-8 h-8" />
            </button>
            <button onClick={(e) => { e.stopPropagation(); prev(); }} className="absolute left-4 p-3 rounded-full bg-white/10 text-white hover:bg-white/20" aria-label="Previous">
              <ChevronLeft className="w-6 h-6" />
            </button>
            <img src={urls[selectedIndex]} alt={alt} className="max-w-[90vw] max-h-[85vh] object-contain rounded-xl" onClick={(e) => e.stopPropagation()} />
            <button onClick={(e) => { e.stopPropagation(); next(); }} className="absolute right-4 p-3 rounded-full bg-white/10 text-white hover:bg-white/20" aria-label="Next">
              <ChevronRight className="w-6 h-6" />
            </button>
            <div className="absolute bottom-6 text-white/60 text-sm">{selectedIndex + 1} / {urls.length}</div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
