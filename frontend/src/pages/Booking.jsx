import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getApiErrorMessage } from '../api/client';
import {
  createBooking,
  fetchAvailabilityByDate,
  fetchAvailabilityForSlot,
} from '../api/bookingApi';
import DatePickerField from '../components/DatePickerField';
import TimeSlotPicker from '../components/TimeSlotPicker';
import { useAuth } from '../context/AuthContext';
import { getServiceById } from '../data/services';
import { saveBookingFromApi, toStartAt } from '../utils/bookings';
import { collectAvailableTimes, isFriday } from '../utils/dates';

const initialForm = {
  name: '',
  address: '',
  date: '',
  time: '',
};

export default function Booking() {
  const { serviceId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const service = getServiceById(serviceId);
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [checking, setChecking] = useState(false);
  const [slotPreview, setSlotPreview] = useState(null);
  const [daySlots, setDaySlots] = useState(null);
  const [slotsLoading, setSlotsLoading] = useState(false);

  useEffect(() => {
    if (!user) return;
    setForm((prev) => ({
      ...prev,
      name: prev.name || user.name || '',
      address: prev.address || user.address || '',
    }));
  }, [user]);

  useEffect(() => {
    if (!form.date || isFriday(form.date) || !service) {
      setDaySlots(null);
      return;
    }
    let cancelled = false;
    setSlotsLoading(true);
    fetchAvailabilityByDate(form.date)
      .then((result) => {
        if (!cancelled) {
          setDaySlots(collectAvailableTimes(result?.vehicles, service.durationHours));
        }
      })
      .catch(() => {
        if (!cancelled) setDaySlots([]);
      })
      .finally(() => {
        if (!cancelled) setSlotsLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [form.date, service]);

  if (!service) {
    return (
      <div className="mx-auto max-w-lg px-4 py-16 text-center">
        <p>Service not found.</p>
        <Link to="/services" className="text-brand-600 hover:underline">
          Browse services
        </Link>
      </div>
    );
  }

  function setField(name, value) {
    setForm((prev) => ({ ...prev, [name]: value }));
    if (name === 'date' || name === 'time') setSlotPreview(null);
  }

  async function handleCheckSlot() {
    if (!form.date || !form.time) {
      setError('Pick date and time first.');
      return;
    }
    if (isFriday(form.date)) {
      setError('Fridays are not available.');
      return;
    }
    setError('');
    setChecking(true);
    setSlotPreview(null);
    try {
      const startAt = toStartAt(form.date, form.time);
      const result = await fetchAvailabilityForSlot({
        startAt,
        durationHours: service.durationHours,
        professionalCount: service.professionals,
      });
      setSlotPreview(result);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setChecking(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (!form.name.trim() || !form.address.trim() || !form.date || !form.time) {
      setError('Please fill in all fields.');
      return;
    }
    if (isFriday(form.date)) {
      setError('Fridays are not available for bookings.');
      return;
    }

    const startAt = toStartAt(form.date, form.time);
    setLoading(true);

    try {
      const apiBooking = await createBooking({
        startAt,
        durationHours: service.durationHours,
        professionalCount: service.professionals,
      });

      saveBookingFromApi({
        customer: {
          name: (user?.name || form.name).trim(),
          address: (user?.address || form.address).trim(),
          date: form.date,
          time: form.time,
        },
        service,
        apiBooking,
      });

      setSuccess(true);
      setTimeout(() => navigate('/bookings'), 1500);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page-main max-w-lg">
      <Link to={`/services/${service.id}`} className="text-sm text-brand-600 hover:underline">
        ← Back to service
      </Link>

      <h1 className="mt-4 text-2xl font-bold text-slate-900">Book: {service.name}</h1>
      <p className="text-slate-500">
        ${service.price} · {service.duration} · {service.professionals} pro(s)
      </p>

      {user && (
        <p className="mt-2 text-sm text-slate-500">
          Using your{' '}
          <Link to="/profile" className="font-medium text-brand-600 hover:underline">
            profile
          </Link>{' '}
          for name & address.{' '}
          {!user.address && (
            <span className="text-amber-700">Add an address in profile for faster checkout.</span>
          )}
        </p>
      )}

      {success && (
        <div className="mt-4 rounded-lg border border-green-200 bg-green-50 p-4 text-green-800">
          Booking confirmed on the server! Redirecting…
        </div>
      )}

      <form onSubmit={handleSubmit} className="mt-6 space-y-4 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        {error && (
          <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700">{error}</div>
        )}

        <div>
          <label htmlFor="name" className="mb-1 block text-sm font-medium text-slate-700">
            Full name
          </label>
          <input
            id="name"
            name="name"
            type="text"
            value={form.name}
            onChange={(e) => setField('name', e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
            placeholder="Your name"
          />
        </div>

        <div>
          <label htmlFor="address" className="mb-1 block text-sm font-medium text-slate-700">
            Address
          </label>
          <textarea
            id="address"
            name="address"
            rows={3}
            value={form.address}
            onChange={(e) => setField('address', e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
            placeholder="Street, city, zip"
          />
        </div>

        <DatePickerField
          id="book-date"
          label="Date"
          value={form.date}
          onChange={(date) => setField('date', date)}
          required
        />

        <TimeSlotPicker
          value={form.time}
          onChange={(time) => setField('time', time)}
          availableTimes={daySlots}
          loading={slotsLoading}
          emptyMessage="Select a date to load open slots for this service duration."
        />

        <button
          type="button"
          onClick={handleCheckSlot}
          disabled={checking || loading}
          className="w-full rounded-lg border border-brand-300 py-2 text-sm font-medium text-brand-700 hover:bg-brand-50 disabled:opacity-50"
        >
          {checking ? 'Checking slot…' : 'Check if this time is free'}
        </button>

        {slotPreview?.vehicles?.length > 0 && (
          <div className="rounded-lg bg-slate-50 p-3 text-sm text-slate-700">
            <p className="font-medium">Available vehicles for this slot:</p>
            <ul className="mt-1 list-inside list-disc">
              {slotPreview.vehicles.map((v) => (
                <li key={v.vehicleId}>
                  <strong>{v.vehicleName || 'Team'}</strong>
                  {v.availableCleaners?.length
                    ? `: ${v.availableCleaners.map((c) => c.cleanerName).join(', ')}`
                    : ` — ${v.availableCleanerIds?.length ?? 0} cleaner(s)`}
                </li>
              ))}
            </ul>
          </div>
        )}

        <p className="text-xs text-slate-400">
          Your booking is saved on our servers. Contact details are taken from your profile.
        </p>

        <button
          type="submit"
          disabled={loading || success || !form.time}
          className="w-full rounded-lg bg-brand-600 py-3 font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {loading ? 'Booking…' : 'Confirm booking'}
        </button>
      </form>
    </div>
  );
}
