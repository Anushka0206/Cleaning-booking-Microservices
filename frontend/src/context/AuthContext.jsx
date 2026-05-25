import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import {
  clearSession,
  getStoredUser,
  loginApi,
  registerApi,
  updateStoredUser,
} from '../api/authApi';
import { getApiErrorMessage } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredUser());

  const login = useCallback(async (email, password) => {
    try {
      const u = await loginApi(email, password);
      setUser(u);
      return { ok: true };
    } catch (err) {
      return { ok: false, error: getApiErrorMessage(err) || 'Login failed' };
    }
  }, []);

  const register = useCallback(async (payload) => {
    try {
      const u = await registerApi(payload);
      setUser(u);
      return { ok: true };
    } catch (err) {
      return { ok: false, error: getApiErrorMessage(err) || 'Registration failed' };
    }
  }, []);

  const logout = useCallback(() => {
    clearSession();
    setUser(null);
  }, []);

  const updateProfile = useCallback(async ({ name, phone, address }) => {
    const u = updateStoredUser({ name, phone, address });
    if (!u) {
      return { ok: false, error: 'Not signed in' };
    }
    setUser(u);
    return { ok: true };
  }, []);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isCleaner: user?.role === 'CLEANER',
      isCustomer: user?.role === 'CUSTOMER',
      login,
      register,
      logout,
      updateProfile,
    }),
    [user, login, register, logout, updateProfile]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}
