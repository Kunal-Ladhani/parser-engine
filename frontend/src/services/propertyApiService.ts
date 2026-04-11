import api, { patch, post } from "./api";
import type {
  PropertySearchFilter,
  PropertySearchResponse,
  PropertyDetails,
  MarkStatusRequest,
} from "@/types/property";

/**
 * Property API Service
 * Handles all property-related API calls
 */
export class PropertyApiService {
  private static readonly BASE_ENDPOINT = "/wb/v1/properties";

  /**
   * Search properties with filters and pagination
   */
  static async searchProperties(
    filters: PropertySearchFilter = {},
    page: number = 0,
    size: number = 10,
    sort?: string
  ): Promise<PropertySearchResponse | null> {
    try {
      // Build query parameters
      const params: Record<string, any> = {
        page,
        size,
        ...filters,
      };

      // Add sorting if provided
      if (sort) {
        params.sort = sort;
      }

      // Remove undefined values
      Object.keys(params).forEach((key) => {
        if (
          params[key] === undefined ||
          params[key] === null ||
          params[key] === ""
        ) {
          delete params[key];
        }
      });

      const response = await api.get<PropertySearchResponse>(
        this.BASE_ENDPOINT,
        {
          params,
        }
      );

      if (response.error) {
        console.error("Property search error:", response.error);
        return null;
      }

      return response.data;
    } catch (error) {
      console.error("Error searching properties:", error);
      return null;
    }
  }

  /**
   * Get property by ID
   */
  static async getPropertyById(
    id: string | number
  ): Promise<PropertySearchResponse["content"][0] | null> {
    try {
      const response = await api.get<PropertySearchResponse["content"][0]>(
        `${this.BASE_ENDPOINT}/${id}`
      );

      if (response.error) {
        console.error("Get property error:", response.error);
        return null;
      }

      return response.data;
    } catch (error) {
      console.error("Error getting property:", error);
      return null;
    }
  }

  /**
   * Update property by ID
   */
  static async updateProperty(
    id: string,
    updates: Partial<PropertyDetails>
  ): Promise<PropertyDetails | null> {
    try {
      // Format dates properly - add time component if only date is provided
      const formattedUpdates = { ...updates };

      if (
        formattedUpdates.leaseEndDate &&
        !formattedUpdates.leaseEndDate.includes("T")
      ) {
        formattedUpdates.leaseEndDate = `${formattedUpdates.leaseEndDate}T23:59:59`;
      }

      if (
        formattedUpdates.rentalEndDate &&
        !formattedUpdates.rentalEndDate.includes("T")
      ) {
        formattedUpdates.rentalEndDate = `${formattedUpdates.rentalEndDate}T23:59:59`;
      }

      const response = await patch<PropertyDetails>(
        `${this.BASE_ENDPOINT}/${id}`,
        formattedUpdates
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return response.data;
    } catch (error) {
      console.error("Error updating property:", error);
      throw error;
    }
  }

  /**
   * Mark property status (SOLD, RENTED, LEASED, AVAILABLE)
   */
  static async markPropertyStatus(
    id: string,
    statusData: MarkStatusRequest
  ): Promise<PropertyDetails | null> {
    try {
      // Format datetime fields properly - add time component if only date is provided
      const formattedData = { ...statusData };

      if (formattedData.soldOn && !formattedData.soldOn.includes("T")) {
        formattedData.soldOn = `${formattedData.soldOn}T23:59:59`;
      }

      if (formattedData.leasedOn && !formattedData.leasedOn.includes("T")) {
        formattedData.leasedOn = `${formattedData.leasedOn}T23:59:59`;
      }

      if (formattedData.rentedOn && !formattedData.rentedOn.includes("T")) {
        formattedData.rentedOn = `${formattedData.rentedOn}T23:59:59`;
      }

      const response = await patch<PropertyDetails>(
        `${this.BASE_ENDPOINT}/${id}/mark_status`,
        formattedData
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return response.data;
    } catch (error) {
      console.error("Error marking property status:", error);
      throw error;
    }
  }

  /**
   * Create a new property
   */
  static async createProperty(
    propertyData: Omit<PropertyDetails, "id" | "dateAdded">
  ): Promise<PropertyDetails | null> {
    try {
      // Format dates properly - add time component if only date is provided
      const formattedData = { ...propertyData };

      if (
        formattedData.leaseEndDate &&
        !formattedData.leaseEndDate.includes("T")
      ) {
        formattedData.leaseEndDate = `${formattedData.leaseEndDate}T23:59:59`;
      }

      if (
        formattedData.rentalEndDate &&
        !formattedData.rentalEndDate.includes("T")
      ) {
        formattedData.rentalEndDate = `${formattedData.rentalEndDate}T23:59:59`;
      }

      const response = await post<PropertyDetails>(
        this.BASE_ENDPOINT,
        formattedData
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return response.data;
    } catch (error) {
      console.error("Error creating property:", error);
      throw error;
    }
  }
}

export default PropertyApiService;
