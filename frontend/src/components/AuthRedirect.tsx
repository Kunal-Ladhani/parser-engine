import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { AuthApiService } from "@/services/authApiService";
import { shouldLogoutOnRefresh } from "@/config/security";

interface AuthRedirectProps {
  children: React.ReactNode;
}

/**
 * Redirects authenticated users away from login/signup pages to dashboard
 */
export function AuthRedirect({ children }: AuthRedirectProps) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

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
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // Redirect to dashboard if already authenticated
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  // User is not authenticated, show login/signup page
  return <>{children}</>;
}