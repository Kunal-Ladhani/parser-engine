import { post } from "./api";
import { jwtDecode } from "jwt-decode";

// JWT Token Payload Structure
export interface JwtPayload {
  role: string;
  userId: string;
  username: string;
  sub: string; // email
  iat: number; // issued at (timestamp)
  exp: number; // expiration (timestamp)
}

// Types for authentication
export type SignupRequest = {
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  password: string;
};

export type SignupResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number; // in seconds
  username: string;
  email: string;
  role: string;
  firstName: string;
  lastName: string;
};

export type LoginRequest = {
  email?: string; // Optional - either email OR username required
  username?: string; // Optional - either email OR username required
  password: string; // Required
};

export type LoginResponse = SignupResponse; // Same structure

export type LogoutResponse = {
  message: string;
  note: string;
  timestamp: number;
};

// Auth endpoints
const AUTH_ENDPOINTS = {
  SIGNUP: "/auth/v1/signup",
  LOGIN: "/auth/v1/login",
  REFRESH: "/auth/v1/refresh",
  LOGOUT: "/auth/v1/logout",
} as const;

// Token storage keys
const STORAGE_KEYS = {
  ACCESS_TOKEN: "accessToken",
  REFRESH_TOKEN: "refreshToken",
  USER_DATA: "userData",
  TOKEN_EXPIRY: "tokenExpiry",
} as const;

/**
 * Authentication service for handling user signup, login, and token management
 */
export class AuthApiService {
  /**
   * Decode JWT token and extract payload
   */
  private static decodeToken(token: string): JwtPayload | null {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded;
    } catch (error) {
      console.error("[AUTH] Failed to decode JWT token:", error);
      return null;
    }
  }

  /**
   * Extract user data from JWT token
   */
  private static extractUserDataFromToken(
    token: string,
    signupData?: SignupRequest
  ): any {
    const payload = this.decodeToken(token);
    if (!payload) {
      console.warn(
        "[AUTH] Could not decode token, falling back to API response data"
      );
      return null;
    }

    const userData = {
      userId: payload.userId,
      username: payload.username,
      email: payload.sub, // JWT uses 'sub' for email
      role: payload.role,
      // Include firstName and lastName from signup if available
      ...(signupData && {
        firstName: signupData.firstName,
        lastName: signupData.lastName,
      }),
    };

    return userData;
  }

  /**
   * Get token expiration time from JWT payload
   */
  private static getTokenExpiryFromJWT(token: string): number | null {
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) {
      return null;
    }

    // JWT exp is in seconds, convert to milliseconds
    const expiryTime = payload.exp * 1000;
    return expiryTime;
  }

  /**
   * Sign up a new user
   */
  static async signup(signupData: SignupRequest): Promise<SignupResponse> {
    try {
      const response = await post<SignupResponse>(
        AUTH_ENDPOINTS.SIGNUP,
        signupData
      );

      if (response.error) {
        throw new Error(response.error);
      }

      if (!response.data) {
        throw new Error("No data received from signup API");
      }

      // Store tokens and user data in session storage
      this.storeAuthData(response.data, signupData);

      console.log("[AUTH] User signed up successfully");
      return response.data;
    } catch (error: any) {
      console.error("[AUTH] Signup failed:", error);

      // For signup errors, extract the actual backend error message
      if (error.response) {
        const errorData = error.response.data;
        if (errorData && typeof errorData === "object") {
          const backendError =
            errorData.errorMessage || errorData.message || errorData.error;
          if (backendError) {
            throw new Error(backendError);
          }
        }
      }

      // For other errors, use the original error message
      throw error;
    }
  }

  /**
   * Log in an existing user
   */
  static async login(loginData: LoginRequest): Promise<LoginResponse> {
    // Preserve existing user data (like firstName/lastName) before login
    const existingUserData = this.getUserData();

    try {
      const response = await post<LoginResponse>(
        AUTH_ENDPOINTS.LOGIN,
        loginData
      );

      if (response.error) {
        throw new Error(response.error);
      }

      if (!response.data) {
        throw new Error("No data received from login API");
      }

      // Store tokens and user data in session storage
      this.storeAuthData(response.data, undefined, existingUserData);

      console.log("[AUTH] User logged in successfully");
      return response.data;
    } catch (error: any) {
      console.error("[AUTH] Login failed:", error);

      // For login errors, extract the actual backend error message
      if (error.response && error.response.status === 401) {
        const errorData = error.response.data;
        if (errorData && typeof errorData === "object") {
          const backendError =
            errorData.errorMessage || errorData.message || errorData.error;
          if (backendError) {
            throw new Error(backendError);
          }
        }
        // Fallback for 401 on login
        throw new Error(
          "Invalid credentials. Please check your email/username and password."
        );
      }

      // For other errors, use the original error message
      throw error;
    }
  }

  /**
   * Store authentication data in session storage
   */
  private static storeAuthData(
    authData: SignupResponse,
    signupData?: SignupRequest,
    existingUserData?: {
      firstName?: string;
      lastName?: string;
      [key: string]: any;
    } | null
  ): void {
    // Extract user data from JWT token
    const jwtUserData = this.extractUserDataFromToken(
      authData.accessToken,
      signupData
    );

    // Get token expiry from JWT (more accurate than API response)
    const jwtExpiry = this.getTokenExpiryFromJWT(authData.accessToken);
    const expiryTime = jwtExpiry || Date.now() + authData.expiresIn * 1000;

    sessionStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, authData.accessToken);
    sessionStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, authData.refreshToken);
    sessionStorage.setItem(STORAGE_KEYS.TOKEN_EXPIRY, expiryTime.toString());

    // Combine JWT data with additional data
    const userData = {
      // Start with JWT payload data
      ...(jwtUserData || {
        username: authData.username,
        email: authData.email,
        role: authData.role,
      }),
      // Add firstName/lastName from API response (available for both signup and login)
      firstName: authData.firstName,
      lastName: authData.lastName,
      // Override with signup data if available (for signup flow)
      ...(signupData && {
        firstName: signupData.firstName,
        lastName: signupData.lastName,
      }),
      // Or preserve existing firstName/lastName from previous session (fallback)
      ...(existingUserData &&
        !authData.firstName &&
        !authData.lastName && {
          firstName: existingUserData.firstName,
          lastName: existingUserData.lastName,
        }),
    };

    sessionStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
  }

  /**
   * Get stored access token
   */
  static getAccessToken(): string | null {
    return sessionStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  }

  /**
   * Get stored refresh token
   */
  static getRefreshToken(): string | null {
    return sessionStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  }

  /**
   * Get stored user data
   */
  static getUserData(): {
    userId?: string;
    username: string;
    email: string;
    role: string;
    firstName?: string;
    lastName?: string;
  } | null {
    const userData = sessionStorage.getItem(STORAGE_KEYS.USER_DATA);
    return userData ? JSON.parse(userData) : null;
  }

  /**
   * Get current user info directly from JWT token (most up-to-date)
   */
  static getCurrentUserFromToken(): JwtPayload | null {
    const token = this.getAccessToken();
    if (!token) {
      return null;
    }
    return this.decodeToken(token);
  }

  /**
   * Get user ID from JWT token
   */
  static getUserId(): string | null {
    const payload = this.getCurrentUserFromToken();
    return payload?.userId || null;
  }

  /**
   * Check if token is expired (using JWT payload)
   */
  static isTokenExpired(): boolean {
    const token = this.getAccessToken();
    if (!token) return true;

    // First try to get expiry from JWT payload (most accurate)
    const payload = this.decodeToken(token);
    if (payload && payload.exp) {
      return Date.now() >= payload.exp * 1000;
    }

    // Fallback to stored expiry time
    const expiryTime = sessionStorage.getItem(STORAGE_KEYS.TOKEN_EXPIRY);
    if (!expiryTime) return true;

    return Date.now() >= parseInt(expiryTime);
  }

  /**
   * Check if token will expire soon (within 2 minutes)
   */
  static isTokenExpiringSoon(): boolean {
    const expiryTime = sessionStorage.getItem(STORAGE_KEYS.TOKEN_EXPIRY);
    if (!expiryTime) return true;

    const twoMinutesFromNow = Date.now() + 2 * 60 * 1000;
    return twoMinutesFromNow >= parseInt(expiryTime);
  }

  /**
   * Check if user is authenticated
   */
  static isAuthenticated(): boolean {
    const token = this.getAccessToken();
    return !!token && !this.isTokenExpired();
  }

  /**
   * Ensure valid token - refresh if needed
   */
  static async ensureValidToken(): Promise<boolean> {
    if (!this.getAccessToken()) {
      return false; // No token at all
    }

    if (!this.isTokenExpired()) {
      return true; // Token is still valid
    }

    try {
      // Token is expired, try to refresh
      await this.refreshToken();
      return true;
    } catch (error) {
      // Refresh failed, clear local auth data only (no backend call needed)
      this.clearLocalAuthData();
      return false;
    }
  }

  /**
   * Auto-refresh token if it's expiring soon
   */
  static async autoRefreshIfNeeded(): Promise<void> {
    if (this.isTokenExpiringSoon() && this.getRefreshToken()) {
      try {
        await this.refreshToken();
      } catch (error) {
        // If auto-refresh fails, we'll let the user continue
        // and handle it when the next API call fails
        console.warn("Auto-refresh failed:", error);
      }
    }
  }

  /**
   * Clear all authentication data (logout)
   * Calls backend logout API to clear server-side context
   */
  static async logout(): Promise<void> {
    const token = this.getAccessToken();

    // If we have a token, try to logout on the backend first
    if (token) {
      try {
        const response = await post<LogoutResponse>(AUTH_ENDPOINTS.LOGOUT);

        if (response.data) {
          console.log(
            "[AUTH] Backend logout successful:",
            response.data.message
          );
        }
      } catch (error) {
        // Don't let backend logout failure prevent local cleanup
        console.warn(
          "[AUTH] Backend logout failed, proceeding with local cleanup:",
          error
        );
      }
    }

    // Always clear local storage regardless of backend call result
    this.clearLocalAuthData();
  }

  /**
   * Clear local authentication data only
   * Use this for security policy logouts, refresh failures, etc.
   * For user-initiated logout, use logout() which calls backend API
   */
  static clearLocalAuthData(): void {
    sessionStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    sessionStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    sessionStorage.removeItem(STORAGE_KEYS.USER_DATA);
    sessionStorage.removeItem(STORAGE_KEYS.TOKEN_EXPIRY);
  }

  /**
   * Refresh access token using refresh token
   */
  static async refreshToken(): Promise<SignupResponse> {
    const refreshToken = this.getRefreshToken();
    const accessToken = this.getAccessToken();

    if (!refreshToken) {
      throw new Error("No refresh token available");
    }

    if (!accessToken) {
      throw new Error("No access token available");
    }

    // Preserve existing user data before refresh
    const existingUserData = this.getUserData();

    const response = await post<SignupResponse>(
      AUTH_ENDPOINTS.REFRESH,
      null, // No body data
      {
        headers: {
          "X-Refresh-Token": refreshToken,
          Authorization: `Bearer ${accessToken}`,
        },
      }
    );

    if (response.error) {
      // If refresh fails, clear local auth data only (no backend logout needed)
      this.clearLocalAuthData();
      throw new Error(response.error);
    }

    if (!response.data) {
      throw new Error("No data received from refresh API");
    }

    // Store new auth data while preserving firstName/lastName
    this.storeAuthData(response.data, undefined, existingUserData);

    return response.data;
  }

  /**
   * Validate JWT token structure and content
   */
  static validateToken(token?: string): {
    valid: boolean;
    reason?: string;
    payload?: JwtPayload;
  } {
    const accessToken = token || this.getAccessToken();

    if (!accessToken) {
      return { valid: false, reason: "No token provided" };
    }

    const payload = this.decodeToken(accessToken);
    if (!payload) {
      return { valid: false, reason: "Invalid token format" };
    }

    // Check if token is expired
    if (payload.exp && Date.now() >= payload.exp * 1000) {
      return { valid: false, reason: "Token expired", payload };
    }

    // Check required fields
    if (!payload.userId || !payload.username || !payload.sub || !payload.role) {
      return {
        valid: false,
        reason: "Missing required fields in token",
        payload,
      };
    }

    return { valid: true, payload };
  }

  /**
   * Debug utility to check current session storage data
   * Call this in browser console: AuthApiService.debugSessionStorage()
   */
  static debugSessionStorage(): void {
    const debugData = {
      accessToken: !!this.getAccessToken(),
      refreshToken: !!this.getRefreshToken(),
      userData: this.getUserData(),
      jwtData: this.getCurrentUserFromToken(),
      tokenValidation: this.validateToken(),
      isAuthenticated: this.isAuthenticated(),
      isTokenExpired: this.isTokenExpired(),
    };

    console.table(debugData);
  }

  /**
   * Manually update user data in session storage
   * Useful for profile updates
   */
  static updateUserData(
    updates: Partial<{ firstName: string; lastName: string; email: string }>
  ): void {
    const currentData = this.getUserData();
    if (!currentData) {
      console.error("[AUTH] No user data found to update");
      return;
    }

    const updatedData = {
      ...currentData,
      ...updates,
    };

    sessionStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(updatedData));
  }

  /**
   * Get storage keys for external use
   */
  static getStorageKeys() {
    return STORAGE_KEYS;
  }
}

// Export singleton-like access
export const authApiService = AuthApiService;
