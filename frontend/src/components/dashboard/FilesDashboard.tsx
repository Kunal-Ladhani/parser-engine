"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, RefreshCw, ChevronUp, ChevronDown } from "lucide-react"
import { FilesTable } from "./FilesTable"
import { fileApiService, type FileItem, type FilesQueryParams } from "@/services/fileApiService"

export function FilesDashboard() {
  const [files, setFiles] = useState<FileItem[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  // Track rows whose processing has been triggered to disable button + show spinner
  const [processingIds, setProcessingIds] = useState<string[]>([])
  
  // Filter states
  const [searchTerm, setSearchTerm] = useState("")
  const [fileTypeFilter, setFileTypeFilter] = useState<string>("all")
  const [statusFilter, setStatusFilter] = useState<string>("all")
  const [uploadedByFilter, setUploadedByFilter] = useState<string>("all")
  const [isSearching, setIsSearching] = useState(false)
  
  // Pagination states
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [totalFiles, setTotalFiles] = useState(0)
  
  // Sorting states
  const [sortBy, setSortBy] = useState<string>("uploadedAt,desc")
  
  // Filter options
  const [fileTypeOptions, setFileTypeOptions] = useState<string[]>([])
  const [statusOptions, setStatusOptions] = useState<string[]>([])
  const [uploaderOptions, setUploaderOptions] = useState<string[]>([])
  
  // User role for admin actions
  const [userRole] = useState<string>("ADMIN") // "USER" or "ADMIN"
  
  // Ref to prevent duplicate API calls
  const hasLoaded = useRef(false)

  // Auto-clear success and error messages after 5 seconds
  useEffect(() => {
    if (success || error) {
      const timer = setTimeout(() => {
        setSuccess(null)
        setError(null)
      }, 5000) // Clear after 5 seconds
      
      return () => clearTimeout(timer)
    }
  }, [success, error])

  // Load files and filter options on component mount
  useEffect(() => {
    if (!hasLoaded.current) {
      loadFiles()
      loadFilterOptions()
      hasLoaded.current = true
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- bootstrap once on mount
  }, [])

  // Load files when pagination changes (but not on initial mount)
  useEffect(() => {
    if (hasLoaded.current && currentPage > 1) {
      loadFiles()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- pagination only; avoids double fetch on mount
  }, [currentPage])

  const loadFilterOptions = async () => {
    try {
      const [fileTypes, statuses, uploaders] = await Promise.all([
        fileApiService.getFileTypes(),
        fileApiService.getStatuses(),
        fileApiService.getUploaders()
      ])
      setFileTypeOptions(fileTypes)
      setStatusOptions(statuses)
      setUploaderOptions(uploaders)
    } catch (err) {
      console.error("Error loading filter options:", err)
    }
  }

  const loadFiles = async () => {
    setIsLoading(true)
    setIsSearching(true)
    setError(null)

    try {
      const params: FilesQueryParams = {
        fileName: searchTerm || undefined,
        fileType: fileTypeFilter !== "all" ? fileTypeFilter : undefined,
        fileProcessingStatus: statusFilter !== "all" ? statusFilter : undefined,
        uploadedBy: uploadedByFilter !== "all" ? uploadedByFilter : undefined,
        page: currentPage - 1, // Convert to 0-based for Spring Boot
        size: pageSize,
        sort: sortBy
      }

      const response = await fileApiService.getFiles(params)
      setFiles(response.data)
      setTotalPages(response.totalPages)
      setTotalFiles(response.total)
    } catch (err) {
      setError("Failed to load files. Please try again.")
      console.error("Load files error:", err)
    } finally {
      setIsLoading(false)
      setIsSearching(false)
    }
  }

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    loadFiles() // Trigger search when form is submitted
  }

  const handleStatusFilterChange = (value: string) => {
    setStatusFilter(value)
    setCurrentPage(1) // Reset to first page when filtering
  }

  const handleUploadedByFilterChange = (value: string) => {
    setUploadedByFilter(value)
    setCurrentPage(1) // Reset to first page when filtering
  }

  const handleSort = (field: string) => {
    let newSort: string;
    if (sortBy.startsWith(field)) {
      // Toggle direction if same field
      newSort = sortBy.includes(',desc') ? `${field},asc` : `${field},desc`
    } else {
      // New field, default to desc
      newSort = `${field},desc`
    }
    setSortBy(newSort)
    setCurrentPage(1) // Reset to first page when sorting
    // Call API to get sorted data
    loadFiles()
  }

  const handleRefresh = () => {
    loadFiles()
    loadFilterOptions()
  }

  const handleDelete = async (fileId: string) => {
    try {
      const success = await fileApiService.deleteFile(fileId)
      if (success) {
        setSuccess("File deleted successfully!")
        loadFiles() // Reload the current page
      } else {
        setError("Failed to delete file. Please try again.")
      }
    } catch (err: unknown) {
      console.error("Delete error:", err)
      const errorMessage =
        err instanceof Error
          ? err.message
          : "Failed to delete file. Please try again."
      setError(errorMessage)
    }
  }

  const handleReprocess = async (fileId: string) => {
    try {
      const success = await fileApiService.reprocessFile(fileId)
      if (success) {
        setSuccess("File reprocessing started successfully!")
        loadFiles() // Reload the current page to show updated status
      } else {
        setError("Failed to start reprocessing. Please try again.")
      }
    } catch (err: unknown) {
      console.error("Reprocess error:", err)
      const errorMessage =
        err instanceof Error
          ? err.message
          : "Failed to start reprocessing. Please try again."
      setError(errorMessage)
    }
  }

  const handleProcess = async (fileId: string) => {
    try {
      // mark row as in-flight
      setProcessingIds((prev) => (prev.includes(fileId) ? prev : [...prev, fileId]))
      const success = await fileApiService.reprocessFile(fileId)
      if (success) {
        setSuccess("File processing started successfully!")
        loadFiles() // Reload the current page to show updated status
      } else {
        setError("Failed to start processing. Please try again.")
      }
    } catch (err: unknown) {
      console.error("Process error:", err)
      const errorMessage =
        err instanceof Error
          ? err.message
          : "Failed to start processing. Please try again."
      setError(errorMessage)
    }
    finally {
      // clear in-flight marker
      setProcessingIds((prev) => prev.filter((id) => id !== fileId))
    }
  }

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
  }

  const getSortIcon = (field: string) => {
    if (!sortBy.startsWith(field)) return null
    return sortBy.includes(',desc') ? <ChevronDown className="h-4 w-4" /> : <ChevronUp className="h-4 w-4" />
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardContent className="space-y-4 pt-6">
          <form onSubmit={handleSearch} className="space-y-4">
            {/* Filters in one row with labels on top */}
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
              <div className="space-y-2">
                <Label htmlFor="fileName">File Name</Label>
                <Input
                  id="fileName"
                  type="text"
                  placeholder="Search files by name"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="fileType">File Type</Label>
                <Select value={fileTypeFilter} onValueChange={(value) => {
                  setFileTypeFilter(value)
                  setCurrentPage(1) // Reset to first page when filtering
                }}>
                  <SelectTrigger id="fileType">
                    <SelectValue placeholder="Select type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Types</SelectItem>
                    {fileTypeOptions.map(type => (
                      <SelectItem key={type} value={type}>{type}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="status">Processing Status</Label>
                <Select value={statusFilter} onValueChange={handleStatusFilterChange}>
                  <SelectTrigger id="status">
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Status</SelectItem>
                    {statusOptions.map(status => (
                      <SelectItem key={status} value={status}>{status}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="uploadedBy">Uploaded By</Label>
                <Select value={uploadedByFilter} onValueChange={handleUploadedByFilterChange}>
                  <SelectTrigger id="uploadedBy">
                    <SelectValue placeholder="Select user" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Users</SelectItem>
                    {uploaderOptions.map(uploader => (
                      <SelectItem key={uploader} value={uploader}>{uploader}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>
          </form>

          {/* Sort Controls and Search Button */}
          <div className="flex items-center justify-between gap-4 p-3 bg-muted/30 rounded-lg">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <Label className="text-sm font-medium">Sort by:</Label>
                <Select value={sortBy.split(',')[0]} onValueChange={(value) => handleSort(value)}>
                  <SelectTrigger className="w-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="uploadedAt">Upload Date</SelectItem>
                    <SelectItem value="fileName">File Name</SelectItem>
                    <SelectItem value="fileSize">File Size</SelectItem>
                    <SelectItem value="fileProcessingStatus">Status</SelectItem>
                    <SelectItem value="uploadedBy">Uploaded By</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              
              <div className="flex items-center gap-2">
                <Label className="text-sm font-medium">Order:</Label>
                <div className="flex border rounded-md">
                  <Button
                    variant={sortBy.includes(',asc') ? 'default' : 'ghost'}
                    size="sm"
                    onClick={() => {
                      const field = sortBy.split(',')[0]
                      setSortBy(`${field},asc`)
                      setCurrentPage(1)
                      loadFiles()
                    }}
                    className="rounded-r-none"
                  >
                    <ChevronUp className="h-4 w-4 mr-1" />
                    ASC
                  </Button>
                  <Button
                    variant={sortBy.includes(',desc') ? 'default' : 'ghost'}
                    size="sm"
                    onClick={() => {
                      const field = sortBy.split(',')[0]
                      setSortBy(`${field},desc`)
                      setCurrentPage(1)
                      loadFiles()
                    }}
                    className="rounded-l-none"
                  >
                    <ChevronDown className="h-4 w-4 mr-1" />
                    DESC
                  </Button>
                </div>
              </div>
              
              <div className="text-sm text-muted-foreground">
                Currently sorting by <span className="font-medium">{sortBy.split(',')[0]}</span> in <span className="font-medium">{sortBy.includes(',desc') ? 'DESC' : 'ASC'}</span> order
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-2">
              <Button
                onClick={handleRefresh}
                disabled={isLoading}
                className="px-6 bg-purple-600 hover:bg-purple-700"
              >
                <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                Refresh
              </Button>
              <Button 
                onClick={loadFiles} 
                disabled={isSearching}
                className="px-6"
              >
                {isSearching ? (
                  <>
                    <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
                    Searching
                  </>
                ) : (
                  <>
                    <Search className="h-4 w-4 mr-2" />
                    Search Files
                  </>
                )}
              </Button>
            </div>
          </div>

          {/* Error and Success Messages */}
          {error && (
            <div className="rounded-md bg-destructive/15 p-3 text-sm text-destructive">
              {error}
            </div>
          )}
          
          {success && (
            <div className="rounded-md bg-green-500/15 p-3 text-sm text-green-600">
              {success}
            </div>
          )}

          {/* Files Table */}
          <FilesTable
            files={files}
            isLoading={isLoading}
            onDelete={handleDelete}
            onReprocess={handleReprocess}
            processingIds={processingIds}
            onProcess={handleProcess}
            userRole={userRole}
            onSort={handleSort}
            getSortIcon={getSortIcon}
          />

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                Showing {((currentPage - 1) * pageSize) + 1} to {Math.min(currentPage * pageSize, totalFiles)} of {totalFiles} files
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 1}
                >
                  Previous
                </Button>
                <span className="text-sm">
                  Page {currentPage} of {totalPages}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage === totalPages}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
} 