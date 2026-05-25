import { getStoredUser } from '../api/authApi';

const LEGACY_KEY = 'cleaning_bookings';

function storageKey(userId) {
  return userId ? `cleaning_bookings_${userId}` : LEGACY_KEY;
}

export function getBookings(userId = getStoredUser()?.userId) {
  try {
    const raw = localStorage.getItem(storageKey(userId));
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

/** Save booking after successful API response (per logged-in user). */
export function saveBookingFromApi({ customer, service, apiBooking, userId = getStoredUser()?.userId }) {
  const list = getBookings(userId);
  const entry = {
    id: apiBooking.id,
    localId: crypto.randomUUID(),
    userId,
    serviceId: service.id,
    serviceName: service.name,
    price: service.price,
    duration: service.duration,
    durationHours: apiBooking.durationHours,
    name: customer.name,
    address: customer.address,
    date: customer.date,
    time: customer.time,
    startAt: apiBooking.startAt,
    endAt: apiBooking.endAt,
    vehicleId: apiBooking.vehicleId,
    professionalCount: service.professionals,
    fromApi: true,
    createdAt: new Date().toISOString(),
  };
  list.unshift(entry);
  localStorage.setItem(storageKey(userId), JSON.stringify(list));
  return entry;
}

export function updateLocalBooking(bookingId, apiBooking, customer, userId = getStoredUser()?.userId) {
  const list = getBookings(userId);
  const idx = list.findIndex((b) => b.id === bookingId);
  if (idx === -1) return null;
  list[idx] = {
    ...list[idx],
    startAt: apiBooking.startAt,
    endAt: apiBooking.endAt,
    durationHours: apiBooking.durationHours,
    vehicleId: apiBooking.vehicleId,
    date: customer?.date ?? list[idx].date,
    time: customer?.time ?? list[idx].time,
    updatedAt: new Date().toISOString(),
  };
  localStorage.setItem(storageKey(userId), JSON.stringify(list));
  return list[idx];
}

export function clearBookings(userId = getStoredUser()?.userId) {
  localStorage.removeItem(storageKey(userId));
}

/** On logout: drop cached lists so next user does not see previous bookings. */
export function clearAllBookingCaches() {
  const keys = [];
  for (let i = 0; i < localStorage.length; i++) {
    const k = localStorage.key(i);
    if (k && (k === LEGACY_KEY || k.startsWith('cleaning_bookings_'))) {
      keys.push(k);
    }
  }
  keys.forEach((k) => localStorage.removeItem(k));
}

/** Build ISO startAt for Java backend: 2026-02-26T10:00:00 */
export function toStartAt(date, time) {
  const t = time.length === 5 ? `${time}:00` : time;
  return `${date}T${t}`;
}

export function mapApiBookingToCard(b) {
  const start = b.startAt ? new Date(b.startAt) : null;
  const pad = (n) => String(n).padStart(2, '0');
  const date = start
    ? `${start.getFullYear()}-${pad(start.getMonth() + 1)}-${pad(start.getDate())}`
    : '';
  const time = start ? `${pad(start.getHours())}:${pad(start.getMinutes())}` : '';
  return {
    id: b.id,
    userId: b.userId,
    serviceName: `Cleaning · ${b.durationHours}h`,
    price: '—',
    duration: `${b.durationHours} hour(s)`,
    durationHours: b.durationHours,
    name: b.customerName || '—',
    address: b.customerAddress || '—',
    date,
    time,
    startAt: b.startAt,
    endAt: b.endAt,
    vehicleId: b.vehicleId,
    status: b.status,
    fromApi: true,
    createdAt: b.startAt || new Date().toISOString(),
  };
}
