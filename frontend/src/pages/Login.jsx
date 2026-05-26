import { useEffect, useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import Alert from '../components/Alert';
import { fetchTeams } from '../api/professionalsApi';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { isAuthenticated, user, login, register, registerCleaner } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const redirectTo =
    location.state?.from || (user?.role === 'CLEANER' ? '/cleaner' : '/bookings');

  const [tab, setTab] = useState('signin');
  const [accountType, setAccountType] = useState('customer');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [vehicleId, setVehicleId] = useState('');
  const [teams, setTeams] = useState([]);
  const [teamsLoading, setTeamsLoading] = useState(false);
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (tab !== 'register' || accountType !== 'cleaner') return;
    let cancelled = false;
    setTeamsLoading(true);
    fetchTeams()
      .then((list) => {
        if (!cancelled) {
          setTeams(list);
          if (list.length && !vehicleId) {
            setVehicleId(list[0].id);
          }
        }
      })
      .catch(() => {
        if (!cancelled) setTeams([]);
      })
      .finally(() => {
        if (!cancelled) setTeamsLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [tab, accountType]);

  if (isAuthenticated) {
    return <Navigate to={user?.role === 'CLEANER' ? '/cleaner' : '/'} replace />;
  }

  async function handleSignIn(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    const result = await login(email, password);
    setLoading(false);
    if (!result.ok) {
      setError(result.error);
      return;
    }
    navigate(redirectTo, { replace: true });
  }

  async function handleRegister(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    let result;
    if (accountType === 'cleaner') {
      if (!vehicleId) {
        setLoading(false);
        setError('Choose a team / vehicle.');
        return;
      }
      if (!phone || phone.replace(/\D/g, '').length < 8) {
        setLoading(false);
        setError('Enter a valid phone number (at least 8 digits).');
        return;
      }
      result = await registerCleaner({
        email,
        password,
        fullName: name,
        phone,
        vehicleId,
      });
    } else {
      result = await register({
        email,
        password,
        fullName: name,
        phone,
        address,
      });
    }
    setLoading(false);
    if (!result.ok) {
      setError(result.error);
      return;
    }
    navigate(accountType === 'cleaner' ? '/cleaner' : '/bookings', { replace: true });
  }

  const isRegister = tab === 'register';

  return (
    <div className="mx-auto flex min-h-[70vh] max-w-lg flex-col justify-center px-4 py-12">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-slate-900">
          {tab === 'signin' ? 'Welcome back' : 'Create account'}
        </h1>
        <p className="mt-2 text-slate-500">
          {tab === 'signin'
            ? 'Sign in to book or manage cleanings'
            : accountType === 'cleaner'
              ? 'Register as a cleaner — join a team'
              : 'Register as a customer'}
        </p>
      </div>

      <div className="mt-8 flex rounded-xl border border-slate-200 bg-slate-100/80 p-1">
        <button
          type="button"
          onClick={() => {
            setTab('signin');
            setError('');
          }}
          className={`flex-1 rounded-lg py-2.5 text-sm font-semibold transition ${
            tab === 'signin' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
          }`}
        >
          Sign in
        </button>
        <button
          type="button"
          onClick={() => {
            setTab('register');
            setError('');
          }}
          className={`flex-1 rounded-lg py-2.5 text-sm font-semibold transition ${
            tab === 'register' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
          }`}
        >
          Register
        </button>
      </div>

      {isRegister && (
        <div className="mt-4 flex rounded-xl border border-slate-200 bg-slate-50 p-1">
          <button
            type="button"
            onClick={() => setAccountType('customer')}
            className={`flex-1 rounded-lg py-2 text-sm font-medium ${
              accountType === 'customer' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
            }`}
          >
            Customer
          </button>
          <button
            type="button"
            onClick={() => setAccountType('cleaner')}
            className={`flex-1 rounded-lg py-2 text-sm font-medium ${
              accountType === 'cleaner' ? 'bg-white text-brand-700 shadow-sm' : 'text-slate-600'
            }`}
          >
            Cleaner
          </button>
        </div>
      )}

      <form
        onSubmit={tab === 'signin' ? handleSignIn : handleRegister}
        className="card mt-6 space-y-4 p-6"
      >
        {isRegister && (
          <>
            <div>
              <label htmlFor="name" className="mb-1 block text-sm font-medium text-slate-700">
                Full name
              </label>
              <input
                id="name"
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="input-field"
              />
            </div>
            <div>
              <label htmlFor="phone" className="mb-1 block text-sm font-medium text-slate-700">
                Phone
              </label>
              <input
                id="phone"
                type="tel"
                required={accountType === 'cleaner'}
                minLength={accountType === 'cleaner' ? 8 : undefined}
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="input-field"
                placeholder="+971501234567"
              />
            </div>
            {accountType === 'customer' && (
              <div>
                <label htmlFor="address" className="mb-1 block text-sm font-medium text-slate-700">
                  Address
                </label>
                <input
                  id="address"
                  type="text"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  className="input-field"
                  placeholder="Building, area, city"
                />
              </div>
            )}
            {accountType === 'cleaner' && (
              <div>
                <label htmlFor="team" className="mb-1 block text-sm font-medium text-slate-700">
                  Team / vehicle
                </label>
                <select
                  id="team"
                  required
                  value={vehicleId}
                  onChange={(e) => setVehicleId(e.target.value)}
                  className="input-field"
                >
                  {teamsLoading && <option value="">Loading teams…</option>}
                  {!teamsLoading && teams.length === 0 && (
                    <option value="">No teams available — check server is online</option>
                  )}
                  {teams.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.code || t.licensePlate || t.id}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </>
        )}

        <div>
          <label htmlFor="email" className="mb-1 block text-sm font-medium text-slate-700">
            Email
          </label>
          <input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input-field"
          />
        </div>

        <div>
          <label htmlFor="password" className="mb-1 block text-sm font-medium text-slate-700">
            Password
          </label>
          <input
            id="password"
            type="password"
            required
            minLength={tab === 'register' ? 6 : 1}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input-field"
          />
        </div>

        {error && <Alert variant="error">{error}</Alert>}

        <button type="submit" disabled={loading} className="btn-primary w-full !py-3">
          {loading ? 'Please wait…' : tab === 'signin' ? 'Sign in' : 'Create account'}
        </button>
      </form>

      <div className="card mt-4 border-dashed p-4 text-xs text-slate-600">
        <p className="font-semibold text-slate-700">Demo accounts</p>
        <p className="mt-2">
          Customer: <code className="rounded bg-slate-100 px-1">customer@justlife.com</code> /{' '}
          <code className="rounded bg-slate-100 px-1">customer123</code>
        </p>
        <p className="mt-1">
          Cleaner: <code className="rounded bg-slate-100 px-1">cleaner@justlife.com</code> /{' '}
          <code className="rounded bg-slate-100 px-1">cleaner123</code>
        </p>
      </div>

      <p className="mt-6 text-center text-sm text-slate-500">
        <Link to="/" className="font-medium text-brand-600 hover:text-brand-700">
          ← Back to home
        </Link>
      </p>
    </div>
  );
}
