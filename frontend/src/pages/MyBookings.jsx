import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getApiErrorMessage } from '../api/client';
import {
  cancelCustomerBooking,
  fetchAvailabilityByDate,
  fetchMyBookingsFromApi,
  updateBooking,
} from '../api/bookingApi';
import Alert from '../components/Alert';
import ConfirmModal from '../components/ConfirmModal';
import Spinner from '../components/Spinner';
import DatePickerField from '../components/DatePickerField';
import TimeSlotPicker from '../components/TimeSlotPicker';
import { useAuth } from '../context/AuthContext';
import { mapApiBookingToCard, toStartAt, updateLocalBooking } from '../utils/bookings';
import { collectAvailableTimes, formatShortDate, isFriday } from '../utils/dates';
function statusBadgeClass(status) {
  if (status === 'CANCELLED') return 'bg-red-50 text-red-700';
  return 'bg-brand-50 text-brand-700';
}

export default function MyBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [rescheduleId, setRescheduleId] = useState(null);
  const [rescheduleForm, setRescheduleForm] = useState({ date: '', time: '' });
  const [rescheduleSlots, setRescheduleSlots] = useState(null);
  const [slotsLoading, setSlotsLoading] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [cancellingId, setCancellingId] = useState(null);
  const [cancelTarget, setCancelTarget] = useState(null);

  const load = useCallback(async () => {
    if (!user?.userId) return;
    setLoading(true);
    setError('');
    try {
      const list = await fetchMyBookingsFromApi();
      setBookings((list || []).map(mapApiBookingToCard));
    } catch (err) {
      setError(getApiErrorMessage(err));
      setBookings([]);
    } finally {
      setLoading(false);
    }
  }, [user?.userId]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    if (!rescheduleForm.date || isFriday(rescheduleForm.date)) {
      setRescheduleSlots(null);
      return;
    }
    const b = bookings.find((x) => x.id === rescheduleId);
    if (!b) return;

    let cancelled = false;
    setSlotsLoading(true);
    fetchAvailabilityByDate(rescheduleForm.date)
      .then((result) => {
        if (!cancelled) {
          setRescheduleSlots(collectAvailableTimes(result?.vehicles, b.durationHours));
        }
      })
      .catch(() => {
        if (!cancelled) setRescheduleSlots([]);
      })
      .finally(() => {
        if (!cancelled) setSlotsLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [rescheduleForm.date, rescheduleId, bookings]);

  function openReschedule(b) {
    setRescheduleId(b.id);
    setRescheduleForm({ date: b.date || '', time: b.time || '' });
    setRescheduleSlots(null);
    setError('');
  }

  async function confirmCancel() {
    if (!cancelTarget) return;
    setCancellingId(cancelTarget.id);
    setError('');
    try {
      await cancelCustomerBooking(cancelTarget.id);
      setCancelTarget(null);
      await load();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setCancellingId(null);
    }
  }

  async function handleReschedule(e) {
    e.preventDefault();
    const b = bookings.find((x) => x.id === rescheduleId);
    if (!b || !rescheduleForm.date || !rescheduleForm.time) return;
    if (isFriday(rescheduleForm.date)) {
      setError('Cannot reschedule to a Friday.');
      return;
    }

    setSaving(true);
    setError('');
    try {
      const newStartAt = toStartAt(rescheduleForm.date, rescheduleForm.time);
      const apiBooking = await updateBooking(b.id, {
        newStartAt,
        newDurationHours: b.durationHours,
      });
      updateLocalBooking(b.id, apiBooking, {
        date: rescheduleForm.date,
        time: rescheduleForm.time,
      });
      setRescheduleId(null);
      await load();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  const activeCount = bookings.filter((b) => b.status !== 'CANCELLED').length;

  return (
    <div className="page-main max-w-3xl">
      <ConfirmModal
        open={Boolean(cancelTarget)}
        title="Cancel booking?"
        message={
          cancelTarget
            ? `${cancelTarget.serviceName} on ${formatShortDate(cancelTarget.date)} at ${cancelTarget.time} will be cancelled. This cannot be undone.`
            : ''
        }
        confirmLabel="Yes, cancel"
        cancelLabel="Keep booking"
        loading={Boolean(cancellingId)}
        onConfirm={confirmCancel}
        onCancel={() => !cancellingId && setCancelTarget(null)}
      />

      <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">My bookings</h1>
          <p className="text-slate-500">
            {user?.name ? `${user.name} · ` : ''}
            {activeCount} active · synced from server
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Link
            to="/profile"
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm hover:bg-slate-50"
          >
            Edit profile
          </Link>
          <button
            type="button"
            onClick={load}
            className="rounded-lg border border-slate-200 px-4 py-2 text-sm hover:bg-slate-50"
          >
            Refresh
          </button>
        </div>
      </div>

      {error && <Alert className="mb-4">{error}</Alert>}

      {loading ? (
        <Spinner label="Loading your bookings…" className="py-12" />
      ) : bookings.length === 0 ? (
        <div className="card border-dashed p-12 text-center">
          <p className="text-slate-500">No bookings for this account yet.</p>
          <Link
            to="/services"
            className="btn-primary mt-4"
          >
            Browse services
          </Link>
        </div>
      ) : (
        <ul className="space-y-4">
          {bookings.map((b) => (
            <li
              key={b.id}
              className={`card p-5 ${
                b.status === 'CANCELLED' ? 'border-slate-200 opacity-75' : 'border-slate-200'
              }`}
            >
              <div className="flex flex-wrap items-start justify-between gap-2">
                <h2 className="font-semibold text-slate-900">{b.serviceName}</h2>
                <span
                  className={`rounded-full px-2 py-1 text-sm font-medium ${statusBadgeClass(b.status)}`}
                >
                  {b.status || 'ACTIVE'}
                </span>
              </div>
              <p className="mt-2 text-sm text-slate-600">
                <strong>{b.name}</strong> · {b.address}
              </p>
              <p className="mt-1 text-sm text-slate-500">
                {formatShortDate(b.date)} at {b.time} · {b.duration}
              </p>
              {b.id && (
                <p className="mt-1 font-mono text-xs text-slate-400">
                  ID: {b.id}
                  {b.vehicleId ? ` · Vehicle: ${b.vehicleId}` : ''}
                </p>
              )}

              {b.id && b.status !== 'CANCELLED' && (
                <div className="mt-3 flex flex-wrap gap-2">
                  <button
                    type="button"
                    onClick={() => openReschedule(b)}
                    className="rounded-lg border border-brand-200 px-3 py-1.5 text-sm font-medium text-brand-700 hover:bg-brand-50"
                  >
                    Reschedule
                  </button>
                  <button
                    type="button"
                    onClick={() => setCancelTarget(b)}
                    disabled={cancellingId === b.id}
                    className="rounded-lg border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 hover:bg-red-50 disabled:opacity-50"
                  >
                    Cancel booking
                  </button>
                </div>
              )}

              {rescheduleId === b.id && (
                <form onSubmit={handleReschedule} className="mt-4 space-y-4 border-t border-slate-100 pt-4">
                  <p className="text-sm font-medium text-slate-700">Choose a new date & time</p>
                  <DatePickerField
                    id={`reschedule-date-${b.id}`}
                    label="New date"
                    value={rescheduleForm.date}
                    onChange={(date) =>
                      setRescheduleForm((f) => ({ ...f, date, time: '' }))
                    }
                    required
                  />
                  <TimeSlotPicker
                    value={rescheduleForm.time}
                    onChange={(time) => setRescheduleForm((f) => ({ ...f, time }))}
                    availableTimes={rescheduleSlots}
                    loading={slotsLoading}
                    emptyMessage="Select a date to load open slots from the API."
                  />
                  <div className="flex gap-2">
                    <button
                      type="submit"
                      disabled={saving || !rescheduleForm.time}
                      className="rounded-lg bg-brand-600 px-4 py-2 text-sm text-white disabled:opacity-50"
                    >
                      {saving ? 'Saving…' : 'Confirm reschedule'}
                    </button>
                    <button
                      type="button"
                      onClick={() => setRescheduleId(null)}
                      className="rounded-lg border border-slate-200 px-4 py-2 text-sm"
                    >
                      Close
                    </button>
                  </div>
                </form>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
