import { Link } from 'react-router-dom';

export default function ServiceCard({ service }) {
  return (
    <article className="card group flex flex-col overflow-hidden transition duration-300 hover:-translate-y-0.5 hover:shadow-lg hover:shadow-slate-200/60">
      <div className="relative overflow-hidden">
        <img
          src={service.image}
          alt={service.name}
          className="h-44 w-full object-cover transition duration-500 group-hover:scale-105"
          loading="lazy"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-900/40 via-transparent to-transparent" />
        <span className="absolute bottom-3 right-3 rounded-full bg-white/95 px-3 py-1 text-sm font-bold text-brand-700 shadow-sm backdrop-blur">
          ${service.price}
        </span>
      </div>
      <div className="flex flex-1 flex-col p-5">
        <h3 className="text-lg font-semibold text-slate-900 group-hover:text-brand-700">
          {service.name}
        </h3>
        <p className="mb-3 mt-1 line-clamp-2 text-sm text-slate-500">{service.description}</p>
        <p className="mb-4 flex items-center gap-2 text-xs text-slate-400">
          <span className="rounded-md bg-slate-100 px-2 py-0.5">{service.duration}</span>
          <span>
            {service.professionals} pro{service.professionals > 1 ? 's' : ''}
          </span>
        </p>
        <div className="mt-auto flex gap-2">
          <Link to={`/services/${service.id}`} className="btn-secondary flex-1 !py-2">
            Details
          </Link>
          <Link to={`/book/${service.id}`} className="btn-primary flex-1 !py-2">
            Book now
          </Link>
        </div>
      </div>
    </article>
  );
}
