import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from "axios";
import { v4 as uuidv4 } from "uuid";

/**
 * API service for making HTTP requests to Spring Boot backend
 * This utility provides methods for common HTTP operations
 * and handles authentication, headers, and error handling with Axios interceptors
 */

// Types for API requests and responses
export type ApiResponse<T> = {
  data: T | null;
  error: string | null;
  status: number;
  message?: string;
};

// Spring Boot specific error response type
export type SpringBootErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
};

// Base API URL - replace with your actual Spring Boot API URL
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/parser-engine";

// Track if we're currently refreshing to avoid multiple refresh attempts
let isRefreshing = false;
let refreshSubscribers: Array<(token: string) => void> = [];

/**
 * Create Axios instance with base configuration
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

/**
 * Helper functions for refresh token logic
 */
const subscribeTokenRefresh = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

/**
 * Add authentication token to request headers
 */
const getAuthHeaders = (): Record<string, string> => {
  const token = sessionStorage.getItem("accessToken");
  return token ? { Authorization: `Bearer ${token}` } : {};
};

/**
 * Request interceptor to add auth headers
 */
apiClient.interceptors.request.use(
  (config) => {
    // Add correlation ID
    config.headers["x-correlation-id"] = uuidv4();

    // Add auth headers
    const authHeaders = getAuthHeaders();
    Object.assign(config.headers, authHeaders);

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor to handle 401 errors with automatic token refresh
 */
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & {
      _retry?: boolean;
    };

    // Check if it's a 401 error and we haven't already tried to refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Skip auto-refresh for auth endpoints (login, signup, refresh)
      const isAuthEndpoint =
        originalRequest.url?.includes("/auth/v1/login") ||
        originalRequest.url?.includes("/auth/v1/signup") ||
        originalRequest.url?.includes("/auth/v1/refresh");

      if (isAuthEndpoint) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // If we're already refreshing, wait for the new token
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            resolve(apiClient(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Import AuthApiService dynamically to avoid circular dependency
        const { AuthApiService } = await import("./authApiService");

        // Try to refresh the token
        const newTokenData = await AuthApiService.refreshToken();
        const newToken = newTokenData.accessToken;

        // Update the failed request with new token
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }

        // Notify all waiting requests about the new token
        onTokenRefreshed(newToken);

        // Retry the original request
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Import AuthApiService dynamically to avoid circular dependency
        const { AuthApiService } = await import("./authApiService");

        // Refresh failed, clear local storage and redirect to login
        AuthApiService.clearLocalAuthData();

        // Notify waiting requests about the failure
        refreshSubscribers.forEach((callback) => callback(""));
        refreshSubscribers = [];

        // Redirect to login page
        window.location.href = "/login";

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Process Axios response
 */
function processResponse<T>(response: AxiosResponse): ApiResponse<T> {
  return {
    data: response.data as T,
    error: null,
    status: response.status,
  };
}

/**
 * Process Axios error and extract meaningful error messages
 */
function messageFromErrorBody(data: unknown): string | null {
  if (!data || typeof data !== "object") return null;
  const body = data as Record<string, unknown>;
  const pick = (key: string) =>
    typeof body[key] === "string" ? (body[key] as string) : null;
  return pick("message") || pick("errorMessage") || pick("error");
}

function processError<T>(error: AxiosError): ApiResponse<T> {
  let errorMsg = "Unknown error";
  let status = 0;

  if (error.response) {
    status = error.response.status;

    if (typeof error.response.data === "string") {
      errorMsg = error.response.data;
    } else if (error.response.data && typeof error.response.data === "object") {
      errorMsg =
        messageFromErrorBody(error.response.data) ||
        JSON.stringify(error.response.data);
    }
  } else if (error.message) {
    errorMsg = error.message;
  }

  return {
    data: null,
    error: errorMsg,
    status,
  };
}

/**
 * Make a GET request with automatic token refresh via interceptors
 */
export async function get<T>(
  endpoint: string,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const response = await apiClient.get(endpoint, config);
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

/**
 * Make a POST request with automatic token refresh via interceptors
 */
export async function post<T>(
  endpoint: string,
  data?: unknown,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const response = await apiClient.post(endpoint, data, config);
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

/**
 * Make a PUT request with automatic token refresh via interceptors
 */
export async function put<T>(
  endpoint: string,
  data?: unknown,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const response = await apiClient.put(endpoint, data, config);
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

/**
 * Make a DELETE request with automatic token refresh via interceptors
 */
export async function del<T>(
  endpoint: string,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const response = await apiClient.delete(endpoint, config);
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

/**
 * Make a PATCH request with automatic token refresh via interceptors
 */
export async function patch<T>(
  endpoint: string,
  data?: unknown,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const response = await apiClient.patch(endpoint, data, config);
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

/**
 * Upload a file with automatic token refresh via interceptors
 */
export async function uploadFile<T>(
  endpoint: string,
  file: File,
  additionalData?: Record<string, string | number | boolean>,
  config: AxiosRequestConfig = {}
): Promise<ApiResponse<T>> {
  try {
    const formData = new FormData();
    formData.append("file", file);

    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, String(value));
      });
    }

    const response = await apiClient.post(endpoint, formData, {
      ...config,
      headers: {
        // Let axios set Content-Type for FormData automatically
        ...config.headers,
      },
    });
    return processResponse<T>(response);
  } catch (error) {
    return processError<T>(error as AxiosError);
  }
}

// Export a default object with all methods
const api = {
  get,
  post,
  put,
  delete: del,
  patch,
  uploadFile,
};

export default api;
