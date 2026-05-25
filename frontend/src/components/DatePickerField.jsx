import { FRIDAY_MESSAGE, getMinBookableDate, isFriday } from '../utils/dates';

export default function DatePickerField({
  id = 'date',
  label = 'Date',
  value,
  onChange,
  required = false,
  className = '',
  hint,
}) {
  const friday = value ? isFriday(value) : false;

  function handleChange(e) {
    const next = e.target.value;
    if (next && isFriday(next)) {
      onChange('');
      return;
    }
    onChange(next);
  }

  return (
    <div className={className}>
      <label htmlFor={id} className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </label>
      <input
        id={id}
        type="date"
        value={value}
        min={getMinBookableDate()}
        onChange={handleChange}
        required={required}
        className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:border-brand-500 focus:outline-none focus:ring-1 focus:ring-brand-500"
      />
      <p className="mt-1 text-xs text-amber-700">
        {friday ? FRIDAY_MESSAGE : hint || 'Fridays are not available for bookings.'}
      </p>
    </div>
  );
}
