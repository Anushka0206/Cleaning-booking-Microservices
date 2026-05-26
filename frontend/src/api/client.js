import axios from 'axios';

/** Dev: empty base → Vite proxies /api and /actuator to :8080 (no CORS). Prod: set VITE_API_BASE_URL. */
const baseURL =
  import.meta.env.VITE_API_BASE_URL ??
  (import.meta.env.DEV ? '' : 'http://localhost:8080');

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('justlife_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/** Read Spring ProblemDetail or fallback message */
export function getApiErrorMessage(error) {
  if (!error.response) {
    return (
      'Cannot connect to the booking server. On your computer, start the backend apps ' +
      '(see project QUICKSTART.md), then refresh this page.'
    );
  }
  const data = error.response.data;
  if (typeof data === 'string') return data;
  if (data?.detail) return data.detail;
  if (data?.response && typeof data.response === 'string') return data.response;
  if (data?.title) return data.title;
  if (data?.message) return data.message;
  if (typeof data?.error === 'string' && data.error) return data.error;
  if (error.response.status === 503 || error.response.status === 502) {
    return 'A backend service is temporarily unavailable. Wait a few seconds and try again.';
  }
  if (error.response.status === 409) {
    return data?.detail || 'That time is not available. Pick another slot from Availability.';
  }
  if (error.response.status === 403) {
    return 'You are not allowed to do this. Try logging in again with the right account.';
  }
  if (error.response.status === 500) {
    const detail =
      typeof data?.detail === 'string' && data.detail && !data.detail.includes('Internal Server Error')
        ? data.detail
        : null;
    return (
      detail ||
      'Something went wrong on the server. Make sure all backend apps are running, then try again.'
    );
  }
  return 'Something went wrong. Please try again in a moment.';
}

/** Backend wraps data in { isSuccess, response, ... } */
export function unwrap(data) {
  if (data && typeof data === 'object' && 'response' in data && data.response != null) {
    return data.response;
  }
  return data;
}
