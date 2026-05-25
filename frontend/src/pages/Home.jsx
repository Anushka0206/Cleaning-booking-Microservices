import { Link } from 'react-router-dom';
import ServiceCard from '../components/ServiceCard';
import { useAuth } from '../context/AuthContext';
import { services } from '../data/services';

const steps = [
  { n: '1', title: 'Pick a package', desc: 'Standard, deep clean, or move-in/out' },
  { n: '2', title: 'Choose a slot', desc: 'Week calendar + live availability' },
  { n: '3', title: 'Manage bookings', desc: 'Reschedule or cancel anytime' },
];

export default function Home() {
  const { isAuthenticated, user, isCleaner } = useAuth();
  const featured = services.slice(0, 3);

  return (
    <div>
      <section className="relative overflow-hidden bg-gradient-to-br from-brand-800 via-brand-600 to-emerald-500 text-white">
        <div
          className="pointer-events-none absolute -right-20 -top-20 h-72 w-72 rounded-full bg-white/10 blur-3xl"
          aria-hidden
        />
        <div
          className="pointer-events-none absolute -bottom-16 left-10 h-56 w-56 rounded-full bg-emerald-300/20 blur-2xl"
          aria-hidden
        />

        <div className="relative mx-auto max-w-6xl px-4 py-16 md:py-20">
          <p className="mb-3 inline-flex rounded-full bg-white/15 px-3 py-1 text-xs font-semibold uppercase tracking-wider text-brand-50 backdrop-blur">
            Trusted home cleaning
          </p>
          <h1 className="mb-4 max-w-2xl text-4xl font-bold leading-tight md:text-5xl">
            {isAuthenticated && user?.name
              ? `Welcome back, ${user.name.split(' ')[0]}`
              : 'Book trusted cleaners in minutes'}
          </h1>
          <p className="mb-8 max-w-xl text-lg text-brand-50/95">
            Browse services, check real-time slots, and manage visits from one place.
            {!isAuthenticated && ' Sign in to book and see your history.'}
          </p>

          <div className="flex flex-wrap gap-3">
            <Link to="/services" className="btn-primary !bg-white !text-brand-800 hover:!bg-brand-50">
              Browse services
            </Link>
            <Link
              to="/availability"
              className="rounded-xl border border-white/35 px-6 py-2.5 text-sm font-semibold text-white backdrop-blur transition hover:bg-white/10"
            >
              Check availability
            </Link>
            {!isCleaner && (
              <Link
                to={isAuthenticated ? '/bookings' : '/login'}
                className="rounded-xl border border-white/35 px-6 py-2.5 text-sm font-semibold text-white backdrop-blur transition hover:bg-white/10"
              >
                {isAuthenticated ? 'My bookings' : 'Sign in'}
              </Link>
            )}
          </div>

          <div className="mt-10 flex flex-wrap gap-4 text-sm text-brand-50/90">
            <span className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-1.5 backdrop-blur">
              <span className="text-emerald-200">✓</span> Live API slots
            </span>
            <span className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-1.5 backdrop-blur">
              <span className="text-emerald-200">✓</span> Fridays off
            </span>
            <span className="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-1.5 backdrop-blur">
              <span className="text-emerald-200">✓</span> Secure login
            </span>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 py-12">
        <div className="mb-8 flex items-end justify-between gap-4">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Popular services</h2>
            <p className="text-slate-500">Most booked packages this week</p>
          </div>
          <Link
            to="/services"
            className="text-sm font-semibold text-brand-600 hover:text-brand-700"
          >
            See all →
          </Link>
        </div>
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {featured.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </div>
      </section>

      <section className="border-t border-slate-200 bg-white">
        <div className="mx-auto max-w-6xl px-4 py-14">
          <h2 className="text-center text-2xl font-bold text-slate-900">How it works</h2>
          <p className="mx-auto mt-2 max-w-md text-center text-slate-500">
            Three simple steps from browse to confirmed visit
          </p>
          <div className="mt-10 grid gap-8 md:grid-cols-3">
            {steps.map((s) => (
              <div
                key={s.n}
                className="card flex flex-col items-center p-6 text-center transition hover:shadow-md"
              >
                <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-br from-brand-500 to-brand-700 text-lg font-bold text-white shadow-lg shadow-brand-500/25">
                  {s.n}
                </div>
                <h3 className="font-semibold text-slate-900">{s.title}</h3>
                <p className="mt-2 text-sm text-slate-500">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
