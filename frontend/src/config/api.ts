// API Configuration
export const API_CONFIG = {
  // Base URL for the parser engine API
  BASE_URL:
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/parser-engine",

  // File management endpoints
  FILES: {
    BASE: "/wb/v1/files",
    LIST: "/wb/v1/files",
    DOWNLOAD: (id: string) => `/wb/aws/v1/download/${id}`,
    DELETE: (id: string) => `/wb/v1/files/${id}/delete`,
    PROCESS: (id: string) => `/wb/v1/files/${id}/process`,
  },

  // Authentication
  AUTH: {
    TOKEN_KEY: import.meta.env.VITE_TOKEN_STORAGE_KEY || "auth_token",
  },

  // Pagination defaults
  PAGINATION: {
    DEFAULT_PAGE_SIZE: 10,
    MAX_PAGE_SIZE: 100,
  },

  // Sorting options
  SORTING: {
    DEFAULT_SORT: "uploadedAt",
    DEFAULT_DIRECTION: "DESC" as const,
    AVAILABLE_FIELDS: [
      "fileName",
      "fileSize",
      "uploadedAt",
      "status",
      "uploadedBy",
    ],
  },
} as const;

// Query parameter types for better type safety
export type FileListParams = {
  fileName?: string;
  fileType?: string;
  fileProcessingStatus?: string;
  uploadedBy?: string;
  page?: number;
  size?: number;
  sort?: string;
};

// Response types
export type ApiPageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};
