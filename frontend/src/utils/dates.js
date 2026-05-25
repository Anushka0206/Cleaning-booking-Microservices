export const FRIDAY_MESSAGE = 'Cleaners are not working on Fridays.';

const DAY_NAMES = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

/** Parse YYYY-MM-DD as local calendar date (avoids UTC shift). */
export function parseLocalDate(dateStr) {
  if (!dateStr) return null;
  const [y, m, d] = dateStr.split('-').map(Number);
  return new Date(y, m - 1, d);
}

export function toDateString(date) {
  const pad = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

export function isFriday(dateStr) {
  const d = parseLocalDate(dateStr);
  return d ? d.getDay() === 5 : false;
}

export function formatShortDate(dateStr) {
  const d = parseLocalDate(dateStr);
  if (!d) return dateStr;
  return `${DAY_NAMES[d.getDay()]} ${d.getDate()}`;
}

export function formatMonthYear(date) {
  return date.toLocaleDateString(undefined, { month: 'long', year: 'numeric' });
}

/** Monday-based week containing anchor (local). */
export function getWeekDays(anchor = new Date()) {
  const start = new Date(anchor);
  const dow = start.getDay();
  const diff = dow === 0 ? -6 : 1 - dow;
  start.setDate(start.getDate() + diff);
  start.setHours(0, 0, 0, 0);

  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    return toDateString(d);
  });
}

export function addDays(dateStr, days) {
  const d = parseLocalDate(dateStr);
  if (!d) return dateStr;
  d.setDate(d.getDate() + days);
  return toDateString(d);
}

export function getMinBookableDate() {
  return toDateString(new Date());
}

/** 30-minute slots from 08:00 through 20:00 (start times). */
export function buildDefaultTimeSlots() {
  const slots = [];
  for (let h = 8; h <= 20; h++) {
    for (const m of [0, 30]) {
      if (h === 20 && m === 30) break;
      slots.push(`${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`);
    }
  }
  return slots;
}

/** Union of start times for a duration bucket from availability API. */
export function collectAvailableTimes(vehicles, durationHours) {
  const key = durationHours === 4 ? 'startTimes4h' : 'startTimes2h';
  const set = new Set();
  for (const v of vehicles || []) {
    for (const c of v.cleaners || []) {
      for (const t of c[key] || []) {
        const s = String(t);
        set.add(s.length >= 5 ? s.slice(0, 5) : s);
      }
    }
  }
  return [...set].sort();
}
