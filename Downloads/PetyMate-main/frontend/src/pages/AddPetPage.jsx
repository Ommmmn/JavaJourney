import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { petService } from '../services/petService';
import PageTransition from '../components/common/PageTransition';
import { ArrowLeft, ArrowRight, Upload, X, PawPrint } from 'lucide-react';
import toast from 'react-hot-toast';

const speciesList = [
  { value: 'DOG', label: '🐕 Dog' }, { value: 'CAT', label: '🐈 Cat' }, { value: 'RABBIT', label: '🐇 Rabbit' },
  { value: 'BIRD', label: '🐦 Bird' }, { value: 'FISH', label: '🐟 Fish' }, { value: 'HAMSTER', label: '🐹 Hamster' },
  { value: 'REPTILE', label: '🦎 Reptile' }, { value: 'OTHER', label: '🐾 Other' },
];

const schema = z.object({
  name: z.string().min(1, 'Pet name is required'),
  species: z.string().min(1, 'Species is required'),
  breed: z.string().optional(),
  gender: z.string().min(1, 'Gender is required'),
  ageMonths: z.coerce.number().min(0).optional(),
  color: z.string().optional(),
  weightKg: z.coerce.number().min(0).optional(),
  vaccinationStatus: z.boolean().optional(),
  neutered: z.boolean().optional(),
  pedigreeCertified: z.boolean().optional(),
  hasOwnSpace: z.boolean().optional(),
  healthStatus: z.string().optional(),
  bio: z.string().max(500).optional(),
  city: z.string().min(1, 'City is required'),
  state: z.string().min(1, 'State is required'),
  pincode: z.string().regex(/^[0-9]{6}$/, '6-digit pincode'),
  listingType: z.string().min(1, 'Listing type is required'),
  price: z.coerce.number().optional(),
});

export default function AddPetPage() {
  const [step, setStep] = useState(1);
  const [photos, setPhotos] = useState([]);
  const [primaryIdx, setPrimaryIdx] = useState(0);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const { register, handleSubmit, watch, setValue, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: { vaccinationStatus: false, neutered: false, pedigreeCertified: false, hasOwnSpace: false },
  });

  const species = watch('species');
  const listingType = watch('listingType');
  const totalSteps = 5;

  const handlePhotos = (e) => {
    const files = Array.from(e.target.files);
    if (photos.length + files.length > 8) { toast.error('Max 8 photos'); return; }
    const newPhotos = files.map((f) => ({ file: f, preview: URL.createObjectURL(f) }));
    setPhotos((p) => [...p, ...newPhotos]);
  };

  const removePhoto = (idx) => {
    setPhotos((p) => p.filter((_, i) => i !== idx));
    if (primaryIdx >= idx && primaryIdx > 0) setPrimaryIdx(primaryIdx - 1);
  };

  const onSubmit = async (data) => {
    if (photos.length === 0) { toast.error('Add at least one photo'); setStep(5); return; }
    setLoading(true);
    try {
      const fd = new FormData();
      Object.entries(data).forEach(([k, v]) => { if (v !== undefined && v !== '') fd.append(k, v); });
      photos.forEach((p, i) => {
        fd.append('photos', p.file);
        if (i === primaryIdx) fd.append('primaryPhotoIndex', i);
      });
      await petService.createPet(fd);
      toast.success('Pet listed successfully! 🐾');
      navigate('/match');
    } catch (err) { toast.error(err.response?.data?.message || 'Failed to create listing'); }
    finally { setLoading(false); }
  };

  return (
    <PageTransition>
      <div className="max-w-3xl mx-auto px-4 py-8">
        <h1 className="font-heading font-extrabold text-2xl text-navy mb-2">Add New Pet 🐾</h1>
        <p className="text-sm text-navy/50 mb-8">Create a listing for your pet</p>

        <div className="flex items-center gap-2 mb-8">
          {Array.from({ length: totalSteps }).map((_, i) => (
            <div key={i} className="flex items-center gap-2 flex-1">
              <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${step > i ? 'bg-primary text-white' : step === i + 1 ? 'bg-primary text-white ring-4 ring-primary/20' : 'bg-pawgray text-navy/30'}`}>
                {i + 1}
              </div>
              {i < totalSteps - 1 && <div className={`flex-1 h-0.5 ${step > i + 1 ? 'bg-primary' : 'bg-pawgray-border'}`} />}
            </div>
          ))}
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="bg-white rounded-card border border-pawgray-border p-6">
            {step === 1 && (
              <div className="space-y-5">
                <h2 className="font-heading font-bold text-navy text-lg">Basic Info</h2>
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Pet Name *</label>
                  <input {...register('name')} placeholder="What's your pet's name?"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary" />
                  {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
                </div>
                <div>
                  <label className="text-sm font-medium text-navy mb-2 block">Species *</label>
                  <div className="grid grid-cols-4 gap-2">
                    {speciesList.map((s) => (
                      <button key={s.value} type="button" onClick={() => setValue('species', s.value)}
                        className={`p-3 rounded-xl text-center text-sm font-medium transition-all ${species === s.value ? 'bg-primary text-white shadow-md' : 'bg-pawgray text-navy/60 hover:bg-primary/10'}`}>
                        {s.label}
                      </button>
                    ))}
                  </div>
                  {errors.species && <p className="text-xs text-red-500 mt-1">{errors.species.message}</p>}
                </div>
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Breed</label>
                  <input {...register('breed')} placeholder="e.g., Labrador, Persian, Lop"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                </div>
                <div>
                  <label className="text-sm font-medium text-navy mb-2 block">Gender *</label>
                  <div className="flex gap-3">
                    {['MALE', 'FEMALE'].map((g) => (
                      <button key={g} type="button" onClick={() => setValue('gender', g)}
                        className={`flex-1 py-3 rounded-xl text-sm font-semibold transition-all ${watch('gender') === g ? 'bg-primary text-white' : 'bg-pawgray text-navy/60 hover:bg-primary/10'}`}>
                        {g === 'MALE' ? '♂️ Male' : '♀️ Female'}
                      </button>
                    ))}
                  </div>
                  {errors.gender && <p className="text-xs text-red-500 mt-1">{errors.gender.message}</p>}
                </div>
              </div>
            )}

            {step === 2 && (
              <div className="space-y-5">
                <h2 className="font-heading font-bold text-navy text-lg">Age & Appearance</h2>
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Age (months)</label>
                  <input {...register('ageMonths')} type="number" min="0" placeholder="e.g., 24"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                </div>
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Color</label>
                  <input {...register('color')} placeholder="e.g., Golden, Black, White"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                </div>
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Weight (kg)</label>
                  <input {...register('weightKg')} type="number" step="0.1" min="0" placeholder="e.g., 25.5"
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                </div>
              </div>
            )}

            {step === 3 && (
              <div className="space-y-5">
                <h2 className="font-heading font-bold text-navy text-lg">Health & Certifications</h2>
                {[
                  { key: 'vaccinationStatus', label: '💉 Vaccinated' },
                  { key: 'pedigreeCertified', label: '🏆 Pedigree Certified' },
                  { key: 'neutered', label: '✂️ Neutered / Spayed' },
                  { key: 'hasOwnSpace', label: '🏠 Has Own Space for Mating' },
                ].map((f) => (
                  <label key={f.key} className={`flex items-center gap-3 p-4 rounded-xl cursor-pointer transition-all border-2 ${watch(f.key) ? 'border-primary bg-primary/5' : 'border-pawgray-border hover:border-primary/30'}`}>
                    <input type="checkbox" {...register(f.key)} className="accent-primary w-5 h-5" />
                    <span className="text-sm font-medium text-navy">{f.label}</span>
                  </label>
                ))}
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Health Notes</label>
                  <textarea {...register('healthStatus')} placeholder="Any health conditions or notes..."
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm resize-none h-24 focus:outline-none focus:ring-2 focus:ring-primary/30" />
                </div>
              </div>
            )}

            {step === 4 && (
              <div className="space-y-5">
                <h2 className="font-heading font-bold text-navy text-lg">Listing Details</h2>
                <div>
                  <label className="text-sm font-medium text-navy mb-2 block">Listing Type *</label>
                  <div className="grid grid-cols-3 gap-3">
                    {[{ v: 'MATING', l: '💕 Mating', c: 'pink' }, { v: 'SALE', l: '💰 For Sale', c: 'blue' }, { v: 'ADOPTION', l: '🏡 Adoption', c: 'green' }].map((t) => (
                      <button key={t.v} type="button" onClick={() => setValue('listingType', t.v)}
                        className={`py-3 rounded-xl text-sm font-semibold transition-all ${listingType === t.v ? 'bg-primary text-white' : 'bg-pawgray text-navy/60'}`}>
                        {t.l}
                      </button>
                    ))}
                  </div>
                  {errors.listingType && <p className="text-xs text-red-500 mt-1">{errors.listingType.message}</p>}
                </div>
                {listingType === 'SALE' && (
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Price (₹)</label>
                    <input {...register('price')} type="number" min="0" placeholder="e.g., 15000"
                      className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                  </div>
                )}
                <div>
                  <label className="text-sm font-medium text-navy mb-1 block">Bio</label>
                  <textarea {...register('bio')} placeholder="Tell us about your pet..."
                    className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm resize-none h-24 focus:outline-none focus:ring-2 focus:ring-primary/30" maxLength={500} />
                  <p className="text-xs text-navy/30 text-right mt-1">{(watch('bio') || '').length}/500</p>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">City *</label>
                    <input {...register('city')} placeholder="City"
                      className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                    {errors.city && <p className="text-xs text-red-500 mt-1">{errors.city.message}</p>}
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">State *</label>
                    <input {...register('state')} placeholder="State"
                      className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                    {errors.state && <p className="text-xs text-red-500 mt-1">{errors.state.message}</p>}
                  </div>
                  <div>
                    <label className="text-sm font-medium text-navy mb-1 block">Pincode *</label>
                    <input {...register('pincode')} placeholder="6 digits"
                      className="w-full px-4 py-3 rounded-xl border border-pawgray-border text-sm focus:outline-none focus:ring-2 focus:ring-primary/30" />
                    {errors.pincode && <p className="text-xs text-red-500 mt-1">{errors.pincode.message}</p>}
                  </div>
                </div>
              </div>
            )}

            {step === 5 && (
              <div className="space-y-5">
                <h2 className="font-heading font-bold text-navy text-lg">Photos</h2>
                <label className="block border-2 border-dashed border-pawgray-border hover:border-primary/30 rounded-xl p-8 text-center cursor-pointer transition-all">
                  <Upload className="w-10 h-10 text-navy/20 mx-auto mb-3" />
                  <p className="text-sm text-navy/50">Click to upload photos (max 8, each max 5MB)</p>
                  <p className="text-xs text-navy/30 mt-1">JPG, PNG, WebP</p>
                  <input type="file" accept="image/*" multiple className="hidden" onChange={handlePhotos} />
                </label>
                {photos.length > 0 && (
                  <div className="grid grid-cols-4 gap-3">
                    {photos.map((p, i) => (
                      <div key={i} className={`relative aspect-square rounded-xl overflow-hidden border-2 ${i === primaryIdx ? 'border-primary' : 'border-transparent'}`}>
                        <img src={p.preview} alt={`Pet photo ${i + 1}`} className="w-full h-full object-cover" />
                        <button type="button" onClick={() => removePhoto(i)} className="absolute top-1 right-1 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center" aria-label="Remove photo">
                          <X className="w-3 h-3" />
                        </button>
                        <button type="button" onClick={() => setPrimaryIdx(i)}
                          className={`absolute bottom-1 left-1 text-[10px] px-2 py-0.5 rounded-badge font-bold ${i === primaryIdx ? 'bg-primary text-white' : 'bg-white/80 text-navy/60 hover:bg-primary hover:text-white'}`}>
                          {i === primaryIdx ? '★ Primary' : 'Set Primary'}
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>

          <div className="flex gap-3 mt-6">
            {step > 1 && (
              <button type="button" onClick={() => setStep(step - 1)}
                className="flex-1 py-3 border border-pawgray-border text-navy/60 font-medium rounded-xl hover:bg-pawgray flex items-center justify-center gap-2">
                <ArrowLeft className="w-4 h-4" /> Back
              </button>
            )}
            {step < totalSteps ? (
              <button type="button" onClick={() => setStep(step + 1)}
                className="flex-1 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark flex items-center justify-center gap-2">
                Next <ArrowRight className="w-4 h-4" />
              </button>
            ) : (
              <button type="submit" disabled={loading}
                className="flex-1 py-3 bg-primary text-white font-semibold rounded-xl hover:bg-primary-dark disabled:opacity-60 flex items-center justify-center gap-2">
                {loading ? <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : <PawPrint className="w-4 h-4" />}
                {loading ? 'Publishing...' : 'Publish Listing'}
              </button>
            )}
          </div>
        </form>
      </div>
    </PageTransition>
  );
}
