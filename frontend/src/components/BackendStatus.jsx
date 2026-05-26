import { useEffect, useState } from 'react';
import { checkGatewayHealth, isGatewayUp } from '../api/bookingApi';
import { getApiErrorMessage } from '../api/client';

export default function BackendStatus() {
  const [status, setStatus] = useState('checking');
  const [detail, setDetail] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function run() {
      try {
        const health = await checkGatewayHealth();
        if (cancelled) return;
        if (isGatewayUp(health)) {
          setStatus('up');
          setDetail('Server is running');
        } else {
          setStatus('down');
          setDetail('Server is not ready yet');
        }
      } catch (err) {
        if (!cancelled) {
          setStatus('down');
          setDetail(getApiErrorMessage(err));
        }
      }
    }

    run();
    const id = setInterval(run, 30000);
    return () => {
      cancelled = true;
      clearInterval(id);
    };
  }, []);

  const colors = {
    checking: 'bg-amber-100 text-amber-800',
    up: 'bg-green-100 text-green-800',
    down: 'bg-red-100 text-red-800',
  };

  return (
    <div
      className={`hidden max-w-xs truncate rounded-full px-2 py-1 text-xs font-medium sm:block ${colors[status]}`}
      title={detail}
    >
      {status === 'checking' ? 'Checking…' : status === 'up' ? 'Online' : 'Offline'}
    </div>
  );
}
