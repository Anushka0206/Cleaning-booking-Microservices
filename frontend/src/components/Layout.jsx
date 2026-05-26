import { Link } from 'react-router-dom';
import Navbar from './Navbar';
import ChatbotWidget from './chatbot/ChatbotWidget';

const footerLinks = [
  { to: '/services', label: 'Services' },
  { to: '/availability', label: 'Availability' },
  { to: '/bookings', label: 'Bookings' },
  { to: '/profile', label: 'Profile' },
  { to: '/login', label: 'Login' },
];

export default function Layout({ children }) {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">{children}</main>
      <ChatbotWidget />
      <footer className="mt-auto border-t border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-4 py-8 sm:flex-row">
          <div className="text-center sm:text-left">
            <p className="font-semibold text-slate-800">JustLife Clean</p>
            <p className="mt-1 text-sm text-slate-500">Home cleaning · Book online in minutes</p>
          </div>
          <nav className="flex flex-wrap justify-center gap-x-5 gap-y-2 text-sm">
            {footerLinks.map((l) => (
              <Link
                key={l.to}
                to={l.to}
                className="text-slate-500 transition hover:text-brand-600"
              >
                {l.label}
              </Link>
            ))}
          </nav>
        </div>
        <p className="border-t border-slate-100 py-4 text-center text-xs text-slate-400">
          © {new Date().getFullYear()} JustLife Clean
        </p>
      </footer>
    </div>
  );
}
