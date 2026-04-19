import { get, patch, post, ApiResponse } from "./api";
import { API_CONFIG, type FileListParams, type ApiPageResponse } from "@/config/api";

// Types for API responses
export type FileItem = {
  id?: string;
  fileName: string;
  fileSize?: number;
  size?: number;
  uploadedAt: string;
  fileProcessingStatus: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";
  uploadedBy: string;
  fileType: string;
  contentType: string;
  // Optional sensitive fields - consider removing from frontend
  s3Key?: string;
  awsKey?: string;
  etag?: string;
  bucketName?: string;
};

/** Spring page shape plus optional legacy fields from the API */
type FileListPayload = Partial<ApiPageResponse<FileItem>> & {
  data?: FileItem[];
  total?: number;
};

export type FilesResponse = {
  data: FileItem[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
};

// Use the type from config
export type FilesQueryParams = FileListParams;

// Real API service for files
export class FileApiService {
  private baseEndpoint = API_CONFIG.FILES.BASE;

  // Get files with filtering, pagination, and sorting
  async getFiles(params: FilesQueryParams = {}): Promise<FilesResponse> {
    const {
      fileName,
      fileType,
      fileProcessingStatus,
      uploadedBy,
      page = 0, // Spring Boot uses 0-based pagination
      size = API_CONFIG.PAGINATION.DEFAULT_PAGE_SIZE,
      sort = API_CONFIG.SORTING.DEFAULT_SORT,
    } = params;

    // Build query parameters
    const queryParams = new URLSearchParams();

    if (fileName) {
      queryParams.append("fileName", fileName);
    }

    if (fileType) {
      queryParams.append("fileType", fileType);
    }

    if (fileProcessingStatus) {
      queryParams.append("fileProcessingStatus", fileProcessingStatus);
    }

    if (uploadedBy) {
      queryParams.append("uploadedBy", uploadedBy);
    }

    if (page !== undefined) {
      queryParams.append("page", page.toString());
    }

    if (size !== undefined) {
      queryParams.append("size", size.toString());
    }

    if (sort) {
      queryParams.append("sort", sort);
    }

    const endpoint = `${this.baseEndpoint}?${queryParams.toString()}`;

    try {
      const response: ApiResponse<FileListPayload> = await get(endpoint);

      if (response.error) {
        throw new Error(response.error);
      }

      // Transform the response to match our expected format
      // Assuming the backend returns a Spring Boot Page object
      const data = response.data;
      if (!data) {
        throw new Error("No data in files list response");
      }

      return {
        data: data.content || data.data || [],
        total: data.totalElements || data.total || 0,
        page: data.number !== undefined ? data.number + 1 : page + 1, // Convert to 1-based for frontend
        pageSize: data.size || size,
        totalPages:
          data.totalPages ||
          Math.ceil((data.totalElements || 0) / (data.size || size)),
      };
    } catch (error) {
      console.error("Error fetching files:", error);
      throw error;
    }
  }

  // Get file by ID
  async getFileById(fileId: string): Promise<FileItem | null> {
    try {
      const response: ApiResponse<FileItem> = await get(
        `${this.baseEndpoint}/${fileId}`
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return response.data;
    } catch (error) {
      console.error("Error fetching file:", error);
      throw error;
    }
  }

  // Soft delete a file
  async deleteFile(fileId: string): Promise<boolean> {
    try {
      const response: ApiResponse<unknown> = await patch(
        API_CONFIG.FILES.DELETE(fileId)
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return true;
    } catch (error) {
      console.error("Error soft deleting file:", error);
      throw error;
    }
  }

  // Download a file
  async downloadFile(fileId: string): Promise<Blob> {
    try {
      const response = await fetch(
        `${API_CONFIG.BASE_URL}${API_CONFIG.FILES.DOWNLOAD(fileId)}`,
        {
          headers: {
            ...this.getAuthHeaders(),
          },
        }
      );

      if (!response.ok) {
        throw new Error(`Download failed: ${response.statusText}`);
      }

      return await response.blob();
    } catch (error) {
      console.error("Error downloading file:", error);
      throw error;
    }
  }

  // Reprocess a file
  async reprocessFile(fileId: string): Promise<boolean> {
    try {
      const response: ApiResponse<unknown> = await post(
        API_CONFIG.FILES.PROCESS(fileId)
      );

      if (response.error) {
        throw new Error(response.error);
      }

      return true;
    } catch (error) {
      console.error("Error reprocessing file:", error);
      throw error;
    }
  }

  // Get file types for filter dropdown (hardcoded for now since API doesn't provide this endpoint)
  async getFileTypes(): Promise<string[]> {
    return ["EXCEL", "PDF", "CSV", "TXT", "DOC", "DOCX"];
  }

  // Get uploaders for filter dropdown (hardcoded for now since API doesn't provide this endpoint)
  async getUploaders(): Promise<string[]> {
    return ["admin@example.com", "user@example.com", "test@example.com"];
  }

  // Get statuses for filter dropdown (hardcoded for now since API doesn't provide this endpoint)
  async getStatuses(): Promise<string[]> {
    return ["PENDING", "PROCESSING", "COMPLETED", "FAILED"];
  }

  private getAuthHeaders(): Record<string, string> {
    const token = sessionStorage.getItem("accessToken");
    return token ? { Authorization: `Bearer ${token}` } : {};
  }
}

// Export singleton instance
export const fileApiService = new FileApiService();
