export default function Spinner({ label = 'Loading…', className = '' }) {
  return (
    <div className={`flex items-center gap-3 text-slate-500 ${className}`} role="status">
      <span
        className="inline-block h-5 w-5 animate-spin rounded-full border-2 border-slate-200 border-t-brand-600"
        aria-hidden
      />
      <span className="text-sm">{label}</span>
    </div>
  );
}
