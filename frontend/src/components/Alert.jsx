const styles = {
  error: 'border-red-200 bg-red-50 text-red-800',
  success: 'border-green-200 bg-green-50 text-green-800',
  info: 'border-brand-200 bg-brand-50 text-brand-900',
  warning: 'border-amber-200 bg-amber-50 text-amber-900',
};

export default function Alert({ variant = 'error', children, className = '' }) {
  return (
    <div
      className={`rounded-xl border px-4 py-3 text-sm ${styles[variant] || styles.error} ${className}`}
      role="alert"
    >
      {children}
    </div>
  );
}
