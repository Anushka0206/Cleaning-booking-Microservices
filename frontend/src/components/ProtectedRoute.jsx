import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, cleanerOnly = false, customerOnly = false }) {
  const { isAuthenticated, isCleaner, isCustomer } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  if (cleanerOnly && !isCleaner) {
    return <Navigate to="/" replace />;
  }
  if (customerOnly && !isCustomer) {
    return <Navigate to="/cleaner" replace />;
  }
  if (location.pathname === '/cleaner' && !isCleaner) {
    return <Navigate to="/" replace />;
  }
  if (location.pathname.startsWith('/book') && isCleaner) {
    return <Navigate to="/cleaner" replace />;
  }
  return children;
}
