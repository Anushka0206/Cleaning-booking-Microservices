import { useMemo, useState } from 'react';
import PageHeader from '../components/PageHeader';
import ServiceCard from '../components/ServiceCard';
import { services } from '../data/services';

export default function Services() {
  const [query, setQuery] = useState('');

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return services;
    return services.filter(
      (s) =>
        s.name.toLowerCase().includes(q) ||
        s.description.toLowerCase().includes(q)
    );
  }, [query]);

  return (
    <div className="page-main">
      <PageHeader
        title="All cleaning services"
        subtitle="Compare packages and book in a few taps. Prices shown per visit."
      />

      <div className="mb-8">
        <label htmlFor="service-search" className="sr-only">
          Search services
        </label>
        <input
          id="service-search"
          type="search"
          placeholder="Search by name or description…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="input-field max-w-md"
        />
      </div>

      {filtered.length === 0 ? (
        <div className="card p-12 text-center">
          <p className="text-slate-500">No services match &ldquo;{query}&rdquo;</p>
          <button type="button" onClick={() => setQuery('')} className="btn-secondary mt-4">
            Clear search
          </button>
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </div>
      )}
    </div>
  );
}
