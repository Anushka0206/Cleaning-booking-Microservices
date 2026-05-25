import { api, unwrap } from './client';

/** Gateway health — safe to call anytime */
export async function checkGatewayHealth() {
  const { data } = await api.get('/actuator/health');
  return data;
}

/** GET /api/availability?date=YYYY-MM-DD */
export async function fetchAvailabilityByDate(date) {
  const { data } = await api.get('/api/availability', { params: { date } });
  return unwrap(data);
}

/** GET /api/availability/slot */
export async function fetchAvailabilityForSlot({ startAt, durationHours, professionalCount }) {
  const { data } = await api.get('/api/availability/slot', {
    params: { startAt, durationHours, professionalCount },
  });
  return unwrap(data);
}

/** GET /api/bookings/me */
export async function fetchMyBookingsFromApi() {
  const { data } = await api.get('/api/bookings/me');
  return unwrap(data);
}

/** POST /api/bookings */
export async function createBooking({ startAt, durationHours, professionalCount }) {
  const { data } = await api.post('/api/bookings', {
    startAt,
    durationHours,
    professionalCount,
  });
  return unwrap(data);
}

/** PUT /api/bookings/{id} */
export async function updateBooking(bookingId, { newStartAt, newDurationHours }) {
  const { data } = await api.put(`/api/bookings/${bookingId}`, {
    newStartAt,
    newDurationHours,
  });
  return unwrap(data);
}

/** POST /api/bookings/{id}/cancel */
export async function cancelCustomerBooking(bookingId) {
  const { data } = await api.post(`/api/bookings/${bookingId}/cancel`);
  return unwrap(data);
}
