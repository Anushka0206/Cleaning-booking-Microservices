import { Link, useParams } from 'react-router-dom';
import { getServiceById } from '../data/services';

export default function ServiceDetails() {
  const { id } = useParams();
  const service = getServiceById(id);

  if (!service) {
    return (
      <div className="page-main text-center">
        <h1 className="text-2xl font-bold">Service not found</h1>
        <Link to="/services" className="btn-primary mt-6">
          Back to services
        </Link>
      </div>
    );
  }

  return (
    <div className="page-main">
      <Link
        to="/services"
        className="inline-flex items-center gap-1 text-sm font-medium text-brand-600 hover:text-brand-700"
      >
        ← All services
      </Link>

      <div className="mt-6 grid gap-10 lg:grid-cols-2 lg:items-start">
        <div className="overflow-hidden rounded-2xl shadow-lg shadow-slate-200/50">
          <img
            src={service.image}
            alt={service.name}
            className="h-80 w-full object-cover lg:h-[28rem]"
          />
        </div>

        <div className="lg:sticky lg:top-24">
          <h1 className="text-3xl font-bold text-slate-900">{service.name}</h1>
          <p className="mt-2 text-3xl font-bold text-brand-600">${service.price}</p>
          <p className="mt-4 leading-relaxed text-slate-600">{service.description}</p>

          <div className="mt-6 flex flex-wrap gap-3">
            <span className="rounded-lg bg-slate-100 px-3 py-1 text-sm font-medium text-slate-700">
              {service.duration}
            </span>
            <span className="rounded-lg bg-brand-50 px-3 py-1 text-sm font-medium text-brand-800">
              {service.professionals} professional{service.professionals > 1 ? 's' : ''}
            </span>
          </div>

          <h2 className="mt-8 font-semibold text-slate-900">What&apos;s included</h2>
          <ul className="mt-3 space-y-2">
            {service.features.map((f) => (
              <li key={f} className="flex items-start gap-2 text-slate-600">
                <span className="mt-0.5 text-brand-600">✓</span>
                {f}
              </li>
            ))}
          </ul>

          <div className="card mt-8 p-5">
            <p className="text-sm text-slate-500">Ready to schedule?</p>
            <Link to={`/book/${service.id}`} className="btn-primary mt-3 w-full !py-3">
              Book this service
            </Link>
            <Link
              to="/availability"
              className="mt-2 block text-center text-sm font-medium text-brand-600 hover:underline"
            >
              Or check availability first
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
