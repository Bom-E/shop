/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      width: {
        '128' : '32rem',
      },
      height: {
        '27.8' : '6.8rem',
        '28.1' : '7.1rem',
      }
    }
  },
  variants:{},
  plugins: [],
}