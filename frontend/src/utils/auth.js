const SESSION_KEY = 'justlife_session';
const USERS_KEY = 'justlife_users';

/** Built-in demo accounts (no backend yet). */
const DEMO_USERS = [
  { email: 'demo@justlife.com', password: 'demo123', name: 'Demo User', role: 'customer' },
  { email: 'customer@justlife.com', password: 'customer123', name: 'Priya Sharma', role: 'customer' },
  { email: 'admin@justlife.com', password: 'admin123', name: 'Admin', role: 'admin' },
];

function normalizeEmail(email) {
  return email.trim().toLowerCase();
}

function loadRegisteredUsers() {
  try {
    const raw = localStorage.getItem(USERS_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

function saveRegisteredUsers(users) {
  localStorage.setItem(USERS_KEY, JSON.stringify(users));
}

function allUsers() {
  const registered = loadRegisteredUsers();
  const byEmail = new Map();
  for (const u of DEMO_USERS) {
    byEmail.set(normalizeEmail(u.email), { ...u });
  }
  for (const u of registered) {
    byEmail.set(normalizeEmail(u.email), { ...u, role: u.role || 'customer' });
  }
  return [...byEmail.values()];
}

function findUser(email, password) {
  const key = normalizeEmail(email);
  const user = allUsers().find((u) => normalizeEmail(u.email) === key);
  if (!user || user.password !== password) {
    return null;
  }
  return {
    email: user.email,
    name: user.name,
    role: user.role,
  };
}

export function getSession() {
  try {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function login(email, password) {
  const user = findUser(email, password);
  if (!user) {
    return { ok: false, error: 'Invalid email or password.' };
  }
  const session = { ...user, loggedInAt: new Date().toISOString() };
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  return { ok: true, user: session };
}

export function register({ name, email, password }) {
  const trimmedName = name?.trim();
  const key = normalizeEmail(email);
  if (!trimmedName || !key || !password) {
    return { ok: false, error: 'Name, email, and password are required.' };
  }
  if (password.length < 6) {
    return { ok: false, error: 'Password must be at least 6 characters.' };
  }
  if (allUsers().some((u) => normalizeEmail(u.email) === key)) {
    return { ok: false, error: 'An account with this email already exists.' };
  }
  const registered = loadRegisteredUsers();
  registered.push({
    email: key,
    password,
    name: trimmedName,
    role: 'customer',
  });
  saveRegisteredUsers(registered);
  return login(key, password);
}

export function logout() {
  localStorage.removeItem(SESSION_KEY);
}

export function getDemoAccountsHint() {
  return DEMO_USERS.map((u) => `${u.email} / ${u.password}`).join(' · ');
}
