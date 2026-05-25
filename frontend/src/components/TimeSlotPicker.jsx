import { buildDefaultTimeSlots } from '../utils/dates';

export default function TimeSlotPicker({
  label = 'Time',
  value,
  onChange,
  availableTimes,
  loading = false,
  emptyMessage = 'Pick a date first to load available times.',
}) {
  const fromApi = Array.isArray(availableTimes);
  const slots = fromApi
    ? availableTimes
    : buildDefaultTimeSlots();

  return (
    <div>
      <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
      {loading ? (
        <p className="text-sm text-slate-500">Loading available times…</p>
      ) : !fromApi ? (
        <p className="mb-2 text-xs text-slate-500">{emptyMessage}</p>
      ) : availableTimes.length === 0 ? (
        <p className="text-sm text-amber-700">No open slots on this day. Try another date.</p>
      ) : null}

      <div className="flex max-h-40 flex-wrap gap-2 overflow-y-auto">
        {(fromApi && !availableTimes.length ? [] : slots).map((slot) => {
          const selected = value === slot;
          return (
            <button
              key={slot}
              type="button"
              onClick={() => onChange(slot)}
              className={`rounded-lg border px-3 py-1.5 text-sm font-medium transition ${
                selected
                  ? 'border-brand-600 bg-brand-600 text-white'
                  : 'border-slate-200 bg-white text-slate-700 hover:border-brand-300 hover:bg-brand-50'
              }`}
            >
              {slot}
            </button>
          );
        })}
      </div>
    </div>
  );
}
