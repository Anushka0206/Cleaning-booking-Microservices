import { api, unwrap } from './client';

export async function fetchMyNotifications() {
  const { data } = await api.get('/api/notifications/mine');
  return unwrap(data);
}

export async function markNotificationRead(notificationId) {
  const { data } = await api.post(`/api/notifications/${notificationId}/read`);
  return unwrap(data);
}

export async function cancelBookingAsCleaner(bookingId) {
  const { data } = await api.post(`/api/bookings/${bookingId}/cleaner-cancel`);
  return unwrap(data);
}
