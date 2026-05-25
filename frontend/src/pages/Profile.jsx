import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, updateProfile } = useAuth();
  const [name, setName] = useState(user?.name || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [address, setAddress] = useState(user?.address || '');
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSaved(false);
    if (!name.trim()) {
      setError('Name is required.');
      return;
    }
    const result = await updateProfile({
      name: name.trim(),
      phone: phone.trim(),
      address: address.trim(),
    });
    if (result.ok) {
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    } else {
      setError(result.error || 'Could not save profile.');
    }
  }

  return (
    <div className="page-main max-w-lg">
      <h1 className="text-3xl font-bold text-slate-900">Your profile</h1>
      <p className="mt-2 text-slate-500">
        Used to pre-fill booking forms. Stored on this device (auth server has no profile edit API yet).
      </p>

      <form
        onSubmit={handleSubmit}
        className="mt-6 space-y-4 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm"
      >
        {error && (
          <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700">{error}</div>
        )}
        {saved && (
          <div className="rounded-lg bg-green-50 p-3 text-sm text-green-800">
            Profile saved. New bookings will use these details.
          </div>
        )}

        <div>
          <label htmlFor="email" className="mb-1 block text-sm font-medium text-slate-700">
            Email
          </label>
          <input
            id="email"
            type="email"
            value={user?.email || ''}
            disabled
            className="w-full rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-slate-500"
          />
        </div>

        <div>
          <label htmlFor="profile-name" className="mb-1 block text-sm font-medium text-slate-700">
            Full name
          </label>
          <input
            id="profile-name"
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
        </div>

        <div>
          <label htmlFor="profile-phone" className="mb-1 block text-sm font-medium text-slate-700">
            Phone
          </label>
          <input
            id="profile-phone"
            type="tel"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="+971501234567"
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
        </div>

        <div>
          <label htmlFor="profile-address" className="mb-1 block text-sm font-medium text-slate-700">
            Address
          </label>
          <textarea
            id="profile-address"
            rows={3}
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Building, area, city"
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
        </div>

        <button
          type="submit"
          className="w-full rounded-lg bg-brand-600 py-3 font-semibold text-white hover:bg-brand-700"
        >
          Save profile
        </button>
      </form>

      <p className="mt-6 text-sm text-slate-500">
        <Link to="/bookings" className="text-brand-600 hover:underline">
          ← My bookings
        </Link>
      </p>
    </div>
  );
}
