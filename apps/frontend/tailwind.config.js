/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          dark: '#1B4235', // Main sidebar active/weather card background
          light: '#F9FAFB', // Background
        },
        status: {
          optimal: '#D1EAE0',
          warning: '#FDF2D0',
          critical: '#FAD5D5',
          optimalDark: '#21A366',
          warningDark: '#F2994A',
          criticalDark: '#EB5757',
        },
        green: {
          50: '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          300: '#86efac',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',
          700: '#3D6B4F',   // FarmSense brand green
          800: '#166534',
          900: '#14532d',
        },
      },
    },
  },
  plugins: [],
}
