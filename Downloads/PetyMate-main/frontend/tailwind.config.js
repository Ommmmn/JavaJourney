/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: { DEFAULT: '#FF6B35', light: '#FF8C61', dark: '#E55A25' },
        secondary: { DEFAULT: '#4CAF50', light: '#66BB6A', dark: '#388E3C' },
        accent: { DEFAULT: '#9C27B0', light: '#AB47BC', dark: '#7B1FA2' },
        warm: { DEFAULT: '#FFF3E0', card: '#FFFAF5', dark: '#FFE0B2' },
        navy: { DEFAULT: '#1A1A2E', muted: '#3D3D5C' },
        pawgray: { DEFAULT: '#F8F7F4', border: '#E8E5DF' },
      },
      fontFamily: {
        heading: ['Nunito', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
      },
      borderRadius: {
        card: '16px',
        pill: '50px',
        badge: '8px',
      },
      keyframes: {
        gradientShift: {
          '0%, 100%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
        },
        bob: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-20px)' },
        },
        pulseRing: {
          '0%': { transform: 'scale(1)', opacity: '1' },
          '100%': { transform: 'scale(1.5)', opacity: '0' },
        },
        bounce3: {
          '0%, 80%, 100%': { transform: 'translateY(0)' },
          '40%': { transform: 'translateY(-6px)' },
        },
      },
      animation: {
        gradientShift: 'gradientShift 8s ease infinite',
        bob1: 'bob 3s ease-in-out infinite',
        bob2: 'bob 4s ease-in-out infinite 0.5s',
        bob3: 'bob 2.5s ease-in-out infinite 1s',
        bob4: 'bob 3.5s ease-in-out infinite 0.3s',
        bob5: 'bob 5s ease-in-out infinite 0.7s',
        pulseRing: 'pulseRing 1.5s ease-out infinite',
        bounce3: 'bounce3 1.2s ease-in-out infinite',
      },
    },
  },
  plugins: [],
}
