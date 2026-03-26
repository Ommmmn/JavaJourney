const petImages = {
  DOG: [
    'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400',
    'https://images.unsplash.com/photo-1534361960057-19f4e2c040da?w=400',
    'https://images.unsplash.com/photo-1550697851-920b181d8ca8?w=400',
  ],
  CAT: [
    'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400',
    'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=400',
  ],
  RABBIT: ['https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=400'],
  BIRD: ['https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=400'],
  HAMSTER: ['https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=400'],
  FISH: ['https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=400'],
  REPTILE: ['https://images.unsplash.com/photo-1548767797-d8c844163c4a?w=400'],
  OTHER: ['https://images.unsplash.com/photo-1548767797-d8c844163c4a?w=400'],
};

export const getPetImage = (species, index = 0) => {
  const imgs = petImages[species] || petImages.OTHER;
  return imgs[index % imgs.length];
};

export const getRandomPetImage = (species) => {
  const imgs = petImages[species] || petImages.OTHER;
  return imgs[Math.floor(Math.random() * imgs.length)];
};

export default petImages;
