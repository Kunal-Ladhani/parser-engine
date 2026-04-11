/**
 * Security configuration for authentication behavior
 */

export type SecurityLevel = "standard" | "strict" | "enterprise";

export const SECURITY_CONFIG = {
  // Current security level - change this to adjust app behavior
  LEVEL: (import.meta.env.VITE_SECURITY_LEVEL as SecurityLevel) || "standard",

  // Different security profiles
  PROFILES: {
    // Standard web app behavior (recommended for most apps)
    standard: {
      logoutOnRefresh: false, // Stay logged in after page refresh
      tokenExpiry: 15 * 60 * 1000, // 15 minutes
      autoRefresh: true, // Automatically refresh tokens
      sessionStorage: true, // Use sessionStorage (cleared when tab closes)
    },

    // More secure for sensitive data
    strict: {
      logoutOnRefresh: true, // Logout on page refresh/reload
      tokenExpiry: 5 * 60 * 1000, // 5 minutes only
      autoRefresh: false, // No auto-refresh, user must re-login
      sessionStorage: true,
    },

    // Enterprise-level security (like your work dashboard)
    enterprise: {
      logoutOnRefresh: true, // Always logout on refresh
      tokenExpiry: 2 * 60 * 1000, // 2 minutes only
      autoRefresh: false, // No auto-refresh
      sessionStorage: false, // Use memory only (cleared on any navigation)
    },
  },
} as const;

/**
 * Get current security settings
 */
export function getSecuritySettings() {
  return SECURITY_CONFIG.PROFILES[SECURITY_CONFIG.LEVEL];
}

/**
 * Check if logout on refresh is enabled
 */
export function shouldLogoutOnRefresh(): boolean {
  return getSecuritySettings().logoutOnRefresh;
}

/**
 * Check if auto-refresh is enabled
 */
export function isAutoRefreshEnabled(): boolean {
  return getSecuritySettings().autoRefresh;
}

/**
 * Get token expiry time in milliseconds
 */
export function getTokenExpiryTime(): number {
  return getSecuritySettings().tokenExpiry;
}
