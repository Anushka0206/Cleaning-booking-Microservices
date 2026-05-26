import { useCallback, useEffect, useState } from 'react';
import { getApiErrorMessage } from '../api/client';
import { fetchAvailabilityByDate } from '../api/bookingApi';
import WeekCalendar from '../components/WeekCalendar';
import { addDays, collectAvailableTimes, getMinBookableDate, isFriday, toDateString } from '../utils/dates';

function formatTimes(times) {
  if (!times?.length) return 'none';
  return times
    .map((t) => {
      const s = String(t);
      return s.length >= 5 ? s.slice(0, 5) : s;
    })
    .join(', ');
}

function countSlots(vehicles) {
  const two = collectAvailableTimes(vehicles, 2);
  const four = collectAvailableTimes(vehicles, 4);
  return new Set([...two, ...four]).size;
}

export default function Availability() {
  const [weekAnchor, setWeekAnchor] = useState(getMinBookableDate());
  const [selectedDate, setSelectedDate] = useState(getMinBookableDate());
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [data, setData] = useState(null);
  const [dayMeta, setDayMeta] = useState({});

  const loadDate = useCallback(async (date) => {
    if (!date || isFriday(date)) {
      setData(null);
      if (isFriday(date)) {
        setError('Cleaners are not working on Fridays.');
      }
      return;
    }
    setLoading(true);
    setError('');
    try {
      const result = await fetchAvailabilityByDate(date);
      setData(result);
      const slots = countSlots(result?.vehicles);
      setDayMeta((prev) => ({
        ...prev,
        [date]: { hasSlots: slots > 0, slotCount: slots },
      }));
    } catch (err) {
      setError(getApiErrorMessage(err));
      setData(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDate(selectedDate);
  }, [selectedDate, loadDate]);

  function handleSelectDate(dateStr) {
    setSelectedDate(dateStr);
  }

  return (
    <div className="page-main max-w-4xl">
      <h1 className="text-3xl font-bold text-slate-900">Availability calendar</h1>
      <p className="mt-2 text-slate-500">
        Pick a day in the week view to see real open slots. Fridays are closed.
      </p>

      <div className="mt-6">
        <WeekCalendar
          weekAnchor={weekAnchor}
          selectedDate={selectedDate}
          onSelectDate={handleSelectDate}
          onPrevWeek={() => setWeekAnchor((d) => addDays(d, -7))}
          onNextWeek={() => setWeekAnchor((d) => addDays(d, 7))}
          dayMeta={dayMeta}
        />
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-3">
        <span className="text-sm text-slate-600">
          Selected: <strong>{selectedDate}</strong>
        </span>
        <button
          type="button"
          onClick={() => loadDate(selectedDate)}
          disabled={loading || isFriday(selectedDate)}
          className="rounded-lg border border-slate-200 px-4 py-2 text-sm hover:bg-slate-50 disabled:opacity-50"
        >
          {loading ? 'Refreshing…' : 'Refresh day'}
        </button>
        <button
          type="button"
          onClick={() => {
            const today = getMinBookableDate();
            setWeekAnchor(today);
            setSelectedDate(today);
          }}
          className="text-sm font-medium text-brand-600 hover:underline"
        >
          Jump to today
        </button>
      </div>

      {error && (
        <div className="mt-4 rounded-lg bg-red-50 p-4 text-sm text-red-700">{error}</div>
      )}

      {loading && !data && !isFriday(selectedDate) && (
        <p className="mt-8 text-slate-500">Loading slots for {selectedDate}…</p>
      )}

      {data?.vehicles && (
        <div className="mt-8 space-y-4">
          <div className="rounded-xl bg-brand-50 px-4 py-3 text-sm text-brand-900">
            <strong>{countSlots(data.vehicles)}</strong> unique start times across 2h and 4h jobs on{' '}
            <strong>{data.date}</strong>
          </div>
          {data.vehicles.map((v) => (
            <div
              key={v.vehicleId}
              className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
            >
              <h2 className="font-semibold text-slate-900">
                {v.vehicleName || 'Vehicle'}
              </h2>
              {v.cleaners?.map((c) => (
                <div key={c.cleanerId} className="mt-3 border-t border-slate-100 pt-3">
                  <p className="text-sm font-medium text-slate-800">
                    {c.cleanerName || 'Cleaner'}
                    {c.phone ? (
                      <span className="ml-2 font-normal text-slate-500">· {c.phone}</span>
                    ) : null}
                  </p>
                  <p className="mt-1 text-xs text-slate-500">
                    2h starts: {formatTimes(c.startTimes2h)}
                  </p>
                  <p className="text-xs text-slate-500">
                    4h starts: {formatTimes(c.startTimes4h)}
                  </p>
                </div>
              ))}
            </div>
          ))}
        </div>
      )}

      {!loading && !error && data?.vehicles?.length === 0 && !isFriday(selectedDate) && (
        <p className="mt-8 text-slate-500">No vehicles returned for this date.</p>
      )}
    </div>
  );
}
