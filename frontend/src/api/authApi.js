import axios from 'axios';
import { api, unwrap } from './client';

const AUTH_BASE =
  import.meta.env.VITE_AUTH_BASE_URL ||
  import.meta.env.VITE_API_BASE_URL ||
  (import.meta.env.DEV ? '' : 'http://localhost:8080');

const authHttp = axios.create({
  baseURL: AUTH_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
});

const TOKEN_KEY = 'justlife_token';
const USER_KEY = 'justlife_user';

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function updateStoredUser(partial) {
  const current = getStoredUser();
  if (!current) return null;
  const next = { ...current, ...partial };
  localStorage.setItem(USER_KEY, JSON.stringify(next));
  return next;
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  // Drop cached booking lists so the next login does not see another user's data
  for (let i = localStorage.length - 1; i >= 0; i--) {
    const k = localStorage.key(i);
    if (k && (k === 'cleaning_bookings' || k.startsWith('cleaning_bookings_'))) {
      localStorage.removeItem(k);
    }
  }
}

function saveSession(data) {
  localStorage.setItem(TOKEN_KEY, data.token);
  localStorage.setItem(
    USER_KEY,
    JSON.stringify({
      userId: data.userId,
      email: data.email,
      name: data.fullName,
      phone: data.phone,
      address: data.address,
      role: data.role,
      cleanerId: data.cleanerId,
      vehicleName: data.vehicleName,
    })
  );
  return getStoredUser();
}

export async function loginApi(email, password) {
  const { data } = await authHttp.post('/api/auth/login', { email, password });
  const body = unwrap(data);
  return saveSession(body);
}

export async function registerApi({ email, password, fullName, phone, address }) {
  const { data } = await authHttp.post('/api/auth/register', {
    email,
    password,
    fullName,
    phone,
    address,
  });
  const body = unwrap(data);
  return saveSession(body);
}

export async function registerCleanerApi({ email, password, fullName, phone, vehicleId }) {
  const { data } = await authHttp.post('/api/auth/register/cleaner', {
    email,
    password,
    fullName,
    phone,
    vehicleId,
  });
  const body = unwrap(data);
  return saveSession(body);
}
