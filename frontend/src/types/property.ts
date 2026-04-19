// Enum types matching backend
export type FurnishingStatus = "UF" | "SF" | "FF";
export type ListingType = "SALE" | "RENT" | "LEASE";
export type AvailabilityStatus = "AVAILABLE" | "SOLD" | "RENTED" | "LEASED";

// Utility functions for enum display labels
export const FurnishingStatusLabels: Record<FurnishingStatus, string> = {
  UF: "Unfurnished",
  SF: "Semi Furnished",
  FF: "Fully Furnished",
};

export const ListingTypeLabels: Record<ListingType, string> = {
  SALE: "Sale",
  RENT: "Rent",
  LEASE: "Lease",
};

export const AvailabilityStatusLabels: Record<AvailabilityStatus, string> = {
  AVAILABLE: "Available",
  SOLD: "Sold",
  RENTED: "Rented",
  LEASED: "Leased",
};

// Property search filter DTO
export interface PropertySearchFilter {
  buildingName?: string;
  numberOfBhk?: string;
  numberOfRk?: string;
  location?: string;
  floor?: string;
  furnishingStatus?: string;
  area?: string;
  quotedAmount?: string;
  carParkingSlots?: string;
  listingType?: string;
  availabilityStatus?: string;
}

// Property details response DTO - matches Spring Boot PropertySearchRespDto
export interface PropertyDetails {
  id?: string; // UUID as string
  buildingName?: string;
  location?: string;
  floor?: string;
  numberOfBhk?: number;
  numberOfRk?: number;
  furnishingStatus?: FurnishingStatus;
  area?: number;
  quotedAmount?: number;
  carParkingSlots?: number;
  comment?: string;
  brokerName?: string;
  brokerPhone?: string;
  listingType?: ListingType;
  availabilityStatus?: AvailabilityStatus;
  dateAdded?: string; // LocalDateTime as string
  leaseEndDate?: string; // LocalDateTime as string - only for LEASE listings
  rentalEndDate?: string; // LocalDateTime as string - only for RENT listings
}

// Pagination response from Spring Boot
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

// Property search response
export type PropertySearchResponse = PageResponse<PropertyDetails>;

// Status marking request types
export interface MarkStatusRequest {
  availabilityStatus: AvailabilityStatus;
  soldBy?: string;
  soldTo?: string;
  soldOn?: string;
  soldForAmount?: number;
  leasedBy?: string;
  leasedTo?: string;
  leasedOn?: string;
  leasedForAmount?: number;
  rentedBy?: string;
  rentedTo?: string;
  rentedOn?: string;
  rentedForAmount?: number;
}
