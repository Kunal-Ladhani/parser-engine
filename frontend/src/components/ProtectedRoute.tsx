import { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { AuthApiService } from "@/services/authApiService";
import { shouldLogoutOnRefresh } from "@/config/security";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      // If security config requires logout on refresh, clear auth data
      if (shouldLogoutOnRefresh()) {
        // Use local clear only for security policy (not user-initiated logout)
        AuthApiService.clearLocalAuthData();
        setIsAuthenticated(false);
        return;
      }
      
      // Check if user has valid tokens
      const hasValidToken = await AuthApiService.ensureValidToken();
      
      setIsAuthenticated(hasValidToken);
    };

    checkAuth();
  }, []);

  // Show loading while checking authentication
  if (isAuthenticated === null) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-gray-600">Checking authentication...</p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  // User is authenticated, render the protected content
  return <>{children}</>;
}