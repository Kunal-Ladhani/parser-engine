import { useState, useEffect } from "react";
import { AuthApiService, type JwtPayload } from "@/services/authApiService";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

/**
 * JWT Debug Info Component
 * Shows decoded JWT token information for development/testing
 * Remove this component in production
 */
export function JwtDebugInfo() {
  const [jwtData, setJwtData] = useState<JwtPayload | null>(null);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const updateJwtData = () => {
      const data = AuthApiService.getCurrentUserFromToken();
      setJwtData(data);
    };

    updateJwtData();
    // Update every 10 seconds to show token changes
    const interval = setInterval(updateJwtData, 10000);

    return () => clearInterval(interval);
  }, []);

  if (!isVisible) {
    return (
      <Button
        variant="outline"
        size="sm"
        onClick={() => setIsVisible(true)}
        className="fixed bottom-4 right-4 z-50"
      >
        Show JWT Debug
      </Button>
    );
  }

  return (
    <div className="fixed bottom-4 right-4 z-50 max-w-md">
      <Card className="bg-gray-900 text-white border-gray-700">
        <CardHeader className="pb-2">
          <div className="flex items-center justify-between">
            <CardTitle className="text-sm">JWT Debug Info</CardTitle>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsVisible(false)}
              className="text-white hover:bg-gray-800 p-1"
            >
              ×
            </Button>
          </div>
        </CardHeader>
        <CardContent className="text-xs space-y-2">
          {jwtData ? (
            <>
              <div>
                <strong>User ID:</strong> {jwtData.userId}
              </div>
              <div>
                <strong>Username:</strong> {jwtData.username}
              </div>
              <div>
                <strong>Email:</strong> {jwtData.sub}
              </div>
              <div>
                <strong>Role:</strong> {jwtData.role}
              </div>
              <div>
                <strong>Issued At:</strong>{" "}
                {new Date(jwtData.iat * 1000).toLocaleTimeString()}
              </div>
              <div>
                <strong>Expires At:</strong>{" "}
                {new Date(jwtData.exp * 1000).toLocaleTimeString()}
              </div>
              <div>
                <strong>Time Left:</strong>{" "}
                {Math.max(0, Math.floor((jwtData.exp * 1000 - Date.now()) / 1000))}s
              </div>
            </>
          ) : (
            <div>No JWT token available</div>
          )}
          
          <div className="pt-2 border-t border-gray-700">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => AuthApiService.debugSessionStorage()}
              className="text-xs text-blue-300 hover:bg-gray-800 p-1"
            >
              Log to Console
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}