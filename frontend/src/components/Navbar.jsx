import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import BackendStatus from './BackendStatus';

const linkClass = ({ isActive }) =>
  `block rounded-lg px-3 py-2 text-sm font-medium transition md:inline-block ${
    isActive ? 'bg-brand-600 text-white' : 'text-slate-600 hover:bg-slate-100'
  }`;

export default function Navbar() {
  const { isAuthenticated, isCleaner, user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  const navItems = (
    <>
      <BackendStatus />
      <NavLink to="/" className={linkClass} end onClick={() => setMenuOpen(false)}>
        Home
      </NavLink>
      <NavLink to="/services" className={linkClass} onClick={() => setMenuOpen(false)}>
        Services
      </NavLink>
      <NavLink to="/availability" className={linkClass} onClick={() => setMenuOpen(false)}>
        Availability
      </NavLink>
      {!isCleaner && (
        <NavLink to="/bookings" className={linkClass} onClick={() => setMenuOpen(false)}>
          My Bookings
        </NavLink>
      )}
      {isCleaner && (
        <NavLink to="/cleaner" className={linkClass} onClick={() => setMenuOpen(false)}>
          Cleaner
        </NavLink>
      )}
      {isAuthenticated && !isCleaner && (
        <NavLink to="/profile" className={linkClass} onClick={() => setMenuOpen(false)}>
          Profile
        </NavLink>
      )}
    </>
  );

  return (
    <header className="sticky top-0 z-50 border-b border-slate-200/80 bg-white/95 shadow-sm backdrop-blur-md">
      <div className="mx-auto max-w-6xl px-4 py-3">
        <div className="flex items-center justify-between gap-4">
          <Link
            to="/"
            className="flex items-center gap-2.5 font-bold text-brand-700 transition hover:text-brand-800"
          >
            <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-brand-500 to-brand-700 text-lg text-white shadow-md shadow-brand-500/30">
              ✨
            </span>
            <span className="hidden sm:inline">JustLife Clean</span>
          </Link>

          <nav className="hidden items-center gap-0.5 md:flex">{navItems}</nav>

          <div className="hidden items-center gap-1 md:flex">
            {isAuthenticated ? (
              <>
                <NavLink
                  to={isCleaner ? '/cleaner' : '/profile'}
                  className="max-w-[140px] truncate px-2 text-xs font-medium text-slate-500 hover:text-brand-600"
                  title={user.email}
                >
                  {user.name}
                </NavLink>
                <button type="button" onClick={logout} className="btn-secondary !py-2 !shadow-none">
                  Logout
                </button>
              </>
            ) : (
              <NavLink to="/login" className={linkClass}>
                Login
              </NavLink>
            )}
          </div>

          <button
            type="button"
            className="rounded-lg border border-slate-200 p-2 text-slate-600 md:hidden"
            aria-expanded={menuOpen}
            aria-label="Toggle menu"
            onClick={() => setMenuOpen((o) => !o)}
          >
            {menuOpen ? (
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            ) : (
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            )}
          </button>
        </div>

        {menuOpen && (
          <nav className="mt-3 space-y-1 border-t border-slate-100 pt-3 md:hidden">
            {navItems}
            <div className="border-t border-slate-100 pt-2">
              {isAuthenticated ? (
                <>
                  <p className="px-3 py-1 text-xs text-slate-500">{user.email}</p>
                  <button
                    type="button"
                    onClick={() => {
                      logout();
                      setMenuOpen(false);
                    }}
                    className="btn-secondary mt-1 w-full"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <NavLink to="/login" className={linkClass} onClick={() => setMenuOpen(false)}>
                  Login
                </NavLink>
              )}
            </div>
          </nav>
        )}
      </div>
    </header>
  );
}
