export const FILE_UPLOAD_ENDPOINT = "/wb/aws/v1/upload";

// Property API endpoints
export const PROPERTY_API_ENDPOINTS = {
  SEARCH: "/wb/v1/properties",
  GET_BY_ID: (id: number) => `/wb/v1/properties/${id}`,
} as const;
