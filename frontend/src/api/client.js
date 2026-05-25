import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

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
      'Cannot reach the API at ' +
      baseURL +
      '. Start Docker + config, discovery, gateway, professional & booking services.'
    );
  }
  const data = error.response.data;
  if (typeof data === 'string') return data;
  if (data?.detail) return data.detail;
  if (data?.response && typeof data.response === 'string') return data.response;
  if (data?.title) return data.title;
  if (data?.message) return data.message;
  if (error.response.status === 403) {
    return 'Access denied (403). Restart auth-service and api-gateway, then try again.';
  }
  if (error.response.status === 500) {
    return 'Server error (500). Restart api-gateway and the service behind this API (booking/auth/notification). In Eureka each service must show 127.0.0.1, not Anushka.mshome.net.';
  }
  return `Request failed (${error.response.status})`;
}

/** Backend wraps data in { isSuccess, response, ... } */
export function unwrap(data) {
  if (data && typeof data === 'object' && 'response' in data && data.response != null) {
    return data.response;
  }
  return data;
}
