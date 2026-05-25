import { formatShortDate, isFriday, parseLocalDate, toDateString } from '../utils/dates';

export default function WeekCalendar({
  weekAnchor,
  selectedDate,
  onSelectDate,
  onPrevWeek,
  onNextWeek,
  dayMeta = {},
}) {
  const anchor = parseLocalDate(weekAnchor) || new Date();
  const dow = anchor.getDay();
  const diff = dow === 0 ? -6 : 1 - dow;
  const monday = new Date(anchor);
  monday.setDate(anchor.getDate() + diff);
  monday.setHours(0, 0, 0, 0);

  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    return toDateString(d);
  });

  const today = toDateString(new Date());

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
      <div className="mb-3 flex items-center justify-between gap-2">
        <button
          type="button"
          onClick={onPrevWeek}
          className="rounded-lg border border-slate-200 px-3 py-1 text-sm hover:bg-slate-50"
          aria-label="Previous week"
        >
          ←
        </button>
        <p className="text-sm font-medium text-slate-700">
          Week of {formatShortDate(days[0])} – {formatShortDate(days[6])}
        </p>
        <button
          type="button"
          onClick={onNextWeek}
          className="rounded-lg border border-slate-200 px-3 py-1 text-sm hover:bg-slate-50"
          aria-label="Next week"
        >
          →
        </button>
      </div>

      <div className="grid grid-cols-7 gap-1 sm:gap-2">
        {days.map((dateStr) => {
          const friday = isFriday(dateStr);
          const selected = selectedDate === dateStr;
          const isToday = dateStr === today;
          const meta = dayMeta[dateStr];
          const slotCount = meta?.slotCount;

          let ring = 'border-slate-200 bg-white hover:border-brand-300';
          if (friday) ring = 'border-slate-100 bg-slate-50 text-slate-400 cursor-not-allowed';
          else if (selected) ring = 'border-brand-600 bg-brand-50 ring-2 ring-brand-200';
          else if (meta?.hasSlots) ring = 'border-emerald-200 bg-emerald-50';

          return (
            <button
              key={dateStr}
              type="button"
              disabled={friday}
              onClick={() => !friday && onSelectDate(dateStr)}
              className={`flex flex-col items-center rounded-xl border px-1 py-2 text-center transition sm:px-2 ${ring}`}
            >
              <span className="text-[10px] font-medium uppercase text-slate-500 sm:text-xs">
                {formatShortDate(dateStr).slice(0, 3)}
              </span>
              <span
                className={`text-sm font-semibold sm:text-base ${
                  isToday && !friday ? 'text-brand-700' : ''
                }`}
              >
                {parseLocalDate(dateStr)?.getDate()}
              </span>
              {friday ? (
                <span className="mt-0.5 text-[9px] text-slate-400">Off</span>
              ) : slotCount != null ? (
                <span
                  className={`mt-0.5 text-[9px] ${
                    slotCount > 0 ? 'text-emerald-700' : 'text-slate-400'
                  }`}
                >
                  {slotCount > 0 ? `${slotCount} slots` : '—'}
                </span>
              ) : null}
            </button>
          );
        })}
      </div>
    </div>
  );
}
