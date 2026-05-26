import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getApiErrorMessage } from '../api/client';
import { cancelBookingAsCleaner, fetchMyNotifications, markNotificationRead } from '../api/notificationApi';
import ConfirmModal from '../components/ConfirmModal';
import { useAuth } from '../context/AuthContext';

export default function CleanerDashboard() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState('');
  const [cancelBookingId, setCancelBookingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const list = await fetchMyNotifications();
      setNotifications(list);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
    const id = setInterval(load, 15000);
    return () => clearInterval(id);
  }, [load]);

  async function handleMarkRead(id) {
    try {
      await markNotificationRead(id);
      await load();
    } catch (err) {
      setError(getApiErrorMessage(err));
    }
  }

  async function confirmCleanerCancel() {
    if (!cancelBookingId) return;
    setActionId(cancelBookingId);
    setError('');
    try {
      await cancelBookingAsCleaner(cancelBookingId);
      setCancelBookingId(null);
      await load();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setActionId('');
    }
  }

  return (
    <div className="page-main max-w-3xl">
      <ConfirmModal
        open={Boolean(cancelBookingId)}
        title="Cancel this booking?"
        message="The time slot will be freed for other customers. The customer will see the booking as cancelled."
        confirmLabel="Cancel booking"
        cancelLabel="Keep"
        loading={Boolean(actionId)}
        onConfirm={confirmCleanerCancel}
        onCancel={() => !actionId && setCancelBookingId(null)}
      />

      <h1 className="text-3xl font-bold text-slate-900">Cleaner dashboard</h1>
      <p className="mt-2 text-slate-500">
        Signed in as <strong>{user?.name}</strong>
        {user?.phone ? ` · ${user.phone}` : ''}
        {user?.vehicleName ? ` · Team: ${user.vehicleName}` : ''}
      </p>

      {error && <div className="mt-4 rounded-lg bg-red-50 p-4 text-sm text-red-700">{error}</div>}

      {loading ? (
        <p className="mt-8 text-slate-500">Loading notifications…</p>
      ) : notifications.length === 0 ? (
        <p className="mt-8 text-slate-500">No notifications yet. Wait for a customer booking.</p>
      ) : (
        <ul className="mt-8 space-y-4">
          {notifications.map((n) => (
            <li
              key={n.id}
              className={`rounded-2xl border p-5 shadow-sm ${
                n.read ? 'border-slate-200 bg-white' : 'border-brand-200 bg-brand-50'
              }`}
            >
              <div className="flex flex-wrap items-start justify-between gap-2">
                <div>
                  <p className="font-semibold text-slate-900">{n.eventType}</p>
                  {(n.slotLabel || n.slotStartAt) && (
                    <p className="mt-2 rounded-lg bg-brand-100 px-3 py-2 text-sm font-medium text-brand-900">
                      Your time slot: {n.slotLabel || n.slotStartAt}
                    </p>
                  )}
                  <p className="mt-2 text-sm text-slate-600">{n.message}</p>
                  <p className="mt-2 text-xs text-slate-500">
                    Customer: {n.customerName || '—'} · {n.customerPhone || 'no phone'}
                  </p>
                  <p className="text-xs text-slate-500">Address: {n.customerAddress || '—'}</p>
                  <p className="mt-1 text-xs text-slate-400">Booking ID: {n.bookingId}</p>
                </div>
                {!n.read && (
                  <button
                    type="button"
                    onClick={() => handleMarkRead(n.id)}
                    className="text-xs font-medium text-brand-600 hover:underline"
                  >
                    Mark read
                  </button>
                )}
              </div>
              {(n.eventType === 'BOOKING_CREATED' || n.eventType === 'BOOKING_RESCHEDULED') && (
                <button
                  type="button"
                  disabled={actionId === n.bookingId}
                  onClick={() => setCancelBookingId(n.bookingId)}
                  className="mt-4 rounded-lg border border-red-200 px-4 py-2 text-sm text-red-600 hover:bg-red-50 disabled:opacity-50"
                >
                  {actionId === n.bookingId ? 'Cancelling…' : 'Cancel booking (free slot)'}
                </button>
              )}
            </li>
          ))}
        </ul>
      )}

      <p className="mt-8 text-sm text-slate-500">
        <Link to="/" className="text-brand-600 hover:underline">
          Back to home
        </Link>
      </p>
    </div>
  );
}
