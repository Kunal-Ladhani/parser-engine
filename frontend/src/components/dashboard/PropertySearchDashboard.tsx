"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ChevronUp, ChevronDown, Search, Filter, X } from "lucide-react"
import PropertyDataTable from "./PropertyDataTable"
import { Pagination } from "./Pagination"
import PropertyApiService from "@/services/propertyApiService"
import type { PropertySearchFilter, PropertyDetails, PropertySearchResponse } from "@/types/property"

export function PropertySearchDashboard() {
  const [filters, setFilters] = useState<PropertySearchFilter>({})
  const [currentPage, setCurrentPage] = useState(0) // Spring Boot uses 0-based pagination
  const [pageSize, setPageSize] = useState(10)
  const [isLoading, setIsLoading] = useState(false)
  const [properties, setProperties] = useState<PropertyDetails[]>([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [sortBy, setSortBy] = useState<string>("id,asc");
  const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('ASC')
  const [error, setError] = useState<string | null>(null)

  // Auto-clear error messages after 5 seconds
  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        setError(null)
      }, 5000) // Clear after 5 seconds
      
      return () => clearTimeout(timer)
    }
  }, [error])

  // Load initial data
  useEffect(() => {
    searchProperties()
  }, [])

  const searchProperties = async (newFilters?: PropertySearchFilter, newPage?: number, newSort?: string) => {
    setIsLoading(true)
    setError(null)

    try {
      const searchFilters = newFilters || filters
      const page = newPage !== undefined ? newPage : currentPage
      const sort = newSort || sortBy

      const response = await PropertyApiService.searchProperties(
        searchFilters,
        page,
        pageSize,
        sort
      )

      if (response) {
        setProperties(response.content)
        setTotalPages(response.totalPages)
        setTotalElements(response.totalElements)
        setCurrentPage(response.number)
      } else {
        setError("Failed to fetch properties")
        setProperties([])
        setTotalPages(0)
        setTotalElements(0)
      }
    } catch (error) {
      console.error("Error searching properties:", error)
      setError("An error occurred while searching properties")
      setProperties([])
      setTotalPages(0)
      setTotalElements(0)
    } finally {
      setIsLoading(false)
    }
  }

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    setCurrentPage(0) // Reset to first page
    await searchProperties(filters, 0)
  }

  const handleFilterChange = (key: keyof PropertySearchFilter, value: string) => {
    setFilters(prev => ({
      ...prev,
      [key]: value || undefined
    }))
  }

  const clearFilters = () => {
    setFilters({})
    setCurrentPage(0)
    searchProperties({}, 0)
  }

  const handleSort = (field: string) => {
    let newSort;
    if (sortBy.startsWith(field)) {
      newSort = sortBy.endsWith(",desc") ? `${field},asc` : `${field},desc`;
    } else {
      newSort = `${field},asc`;
    }
    setSortBy(newSort);
    setCurrentPage(0);
    searchProperties(filters, 0, newSort);
  }

  const getSortIcon = (field: string) => {
    if (!sortBy.startsWith(field)) return null;
    return sortBy.endsWith(",desc")
      ? <ChevronDown className="h-4 w-4" />
      : <ChevronUp className="h-4 w-4" />;
  }

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1) // Convert to 0-based for API
    searchProperties(filters, page - 1)
  }

  return (
    <div className="space-y-6 w-full">
      <Card className="w-full">
        <CardContent className="pt-6">
          <form onSubmit={handleSearch} className="space-y-4">
            {/* Row 1: Building name, location, BHK, RK */}
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
              <div className="space-y-2">
                <Label htmlFor="buildingName">Building Name</Label>
                <Input
                  id="buildingName"
                  placeholder="Enter building name"
                  value={filters.buildingName || ""}
                  onChange={(e) => handleFilterChange('buildingName', e.target.value)}
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="location">Location</Label>
                <Input
                  id="location"
                  placeholder="Enter location"
                  value={filters.location || ""}
                  onChange={(e) => handleFilterChange('location', e.target.value)}
                />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="numberOfBhk">BHK</Label>
                <Select 
                  value={filters.numberOfBhk || ""} 
                  onValueChange={(value) => handleFilterChange('numberOfBhk', value)}
                >
                  <SelectTrigger id="numberOfBhk">
                    <SelectValue placeholder="Select BHK" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="1">1 BHK</SelectItem>
                    <SelectItem value="2">2 BHK</SelectItem>
                    <SelectItem value="3">3 BHK</SelectItem>
                    <SelectItem value="4">4 BHK</SelectItem>
                    <SelectItem value="5">5+ BHK</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="numberOfRk">RK</Label>
                <Select 
                  value={filters.numberOfRk || ""} 
                  onValueChange={(value) => handleFilterChange('numberOfRk', value)}
                >
                  <SelectTrigger id="numberOfRk">
                    <SelectValue placeholder="Select RK" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="1">1 RK</SelectItem>
                    <SelectItem value="2">2 RK</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Row 2: Area, Furnishing status, Floor, Parking slot */}
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
              <div className="space-y-2">
                <Label htmlFor="area">Area (sq ft)</Label>
                <Input
                  id="area"
                  placeholder="Enter area"
                  value={filters.area || ""}
                  onChange={(e) => handleFilterChange('area', e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="furnishingStatus">Furnishing Status</Label>
                <Select 
                  value={filters.furnishingStatus || ""} 
                  onValueChange={(value) => handleFilterChange('furnishingStatus', value)}
                >
                  <SelectTrigger id="furnishingStatus">
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="UF">Unfurnished</SelectItem>
                    <SelectItem value="SF">Semi Furnished</SelectItem>
                    <SelectItem value="FF">Fully Furnished</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="floor">Floor</Label>
                <Input
                  id="floor"
                  placeholder="Enter floor"
                  value={filters.floor || ""}
                  onChange={(e) => handleFilterChange('floor', e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="carParkingSlots">Parking Slots</Label>
                <Select 
                  value={filters.carParkingSlots || ""} 
                  onValueChange={(value) => handleFilterChange('carParkingSlots', value)}
                >
                  <SelectTrigger id="carParkingSlots">
                    <SelectValue placeholder="Select slots" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="0">0</SelectItem>
                    <SelectItem value="1">1</SelectItem>
                    <SelectItem value="2">2</SelectItem>
                    <SelectItem value="3">3+</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Row 3: Quoted Amount, Listing type, Available */}
            <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
              <div className="space-y-2">
                <Label htmlFor="quotedAmount">Quoted Amount</Label>
                <Input
                  id="quotedAmount"
                  placeholder="Enter amount"
                  value={filters.quotedAmount || ""}
                  onChange={(e) => handleFilterChange('quotedAmount', e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="listingType">Listing Type</Label>
                <Select 
                  value={filters.listingType || ""} 
                  onValueChange={(value) => handleFilterChange('listingType', value)}
                >
                  <SelectTrigger id="listingType">
                    <SelectValue placeholder="Select type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="SALE">Sale</SelectItem>
                    <SelectItem value="RENT">Rent</SelectItem>
                    <SelectItem value="LEASE">Lease</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="availabilityStatus">Available</Label>
                <Select 
                  value={filters.availabilityStatus || ""} 
                  onValueChange={(value) => handleFilterChange('availabilityStatus', value)}
                >
                  <SelectTrigger id="availabilityStatus">
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="AVAILABLE">Available</SelectItem>
                    <SelectItem value="SOLD">Sold</SelectItem>
                    <SelectItem value="RENTED">Rented</SelectItem>
                    <SelectItem value="LEASED">Leased</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Empty div to maintain grid alignment */}
              <div></div>
            </div>



            <div className="flex gap-2">
              <Button type="submit" disabled={isLoading} className="flex items-center gap-2">
                <Search className="h-4 w-4" />
                {isLoading ? "Searching" : "Search Properties"}
              </Button>
              <Button 
                type="button" 
                variant="outline" 
                onClick={clearFilters}
                className="flex items-center gap-2"
              >
                <X className="h-4 w-4" />
                Clear Filters
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {error && (
        <Card className="w-full border-red-200 bg-red-50">
          <CardContent className="pt-6">
            <div className="text-center text-red-600">{error}</div>
          </CardContent>
        </Card>
      )}

      <div className="w-full overflow-x-auto">
        <PropertyDataTable 
          data={properties} 
          isLoading={isLoading}
          onSort={handleSort}
          sortBy={sortBy}
          sortDirection={sortDirection}
          getSortIcon={getSortIcon}
        />
      </div>

      {totalPages > 0 && (
        <div className="w-full flex justify-between items-center">
          <div className="text-sm text-muted-foreground">
            Showing {properties.length} of {totalElements} properties
          </div>
          <Pagination 
            currentPage={currentPage + 1} 
            totalPages={totalPages} 
            onPageChange={handlePageChange} 
          />
        </div>
      )}
    </div>
  )
} 