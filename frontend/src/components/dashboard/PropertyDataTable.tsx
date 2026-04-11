import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { useNavigate } from "react-router-dom"
import type { PropertyDetails, FurnishingStatus, AvailabilityStatus, ListingType } from "@/types/property"
import { FurnishingStatusLabels, AvailabilityStatusLabels, ListingTypeLabels } from "@/types/property"

interface PropertyDataTableProps {
  data: PropertyDetails[]
  isLoading?: boolean
  onSort?: (field: string) => void
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
  getSortIcon?: (field: string) => React.ReactElement | null
}

export default function PropertyDataTable({ 
  data, 
  isLoading, 
  onSort,
  sortBy,
  sortDirection,
  getSortIcon
}: PropertyDataTableProps) {
  const navigate = useNavigate()
  if (isLoading) {
    return (
      <Card className="w-full p-4">
        <div className="text-center text-muted-foreground">Loading properties</div>
      </Card>
    )
  }

  if (data.length === 0) {
    return (
      <Card className="w-full p-4">
        <div className="text-center text-muted-foreground">No properties found</div>
      </Card>
    )
  }

  const handleSort = (field: string) => {
    if (onSort) {
      onSort(field)
    }
  }

  const formatPrice = (price?: number) => {
    if (!price) return ""
    // Price is already in thousands, so multiply by 1000 to get actual price
    const actualPrice = price * 1000
    return `₹${actualPrice.toLocaleString('en-IN')}/month`
  }

  const formatArea = (area?: number) => {
    if (!area) return ""
    return `${area} sq ft`
  }

  const getFurnishingBadge = (status?: FurnishingStatus) => {
    if (!status) return null
    
    const variants: Record<FurnishingStatus, "default" | "secondary" | "outline"> = {
      'FF': 'default',
      'SF': 'secondary',
      'UF': 'outline'
    }

    return (
      <Badge variant={variants[status] || 'outline'}>
        {FurnishingStatusLabels[status] || status}
      </Badge>
    )
  }

  const getAvailabilityBadge = (status?: AvailabilityStatus) => {
    if (!status) return null
    
    const variants: Record<AvailabilityStatus, "default" | "secondary" | "outline" | "destructive"> = {
      'AVAILABLE': 'default',
      'SOLD': 'destructive',
      'RENTED': 'secondary',
      'LEASED': 'outline'
    }

    return (
      <Badge variant={variants[status] || 'outline'}>
        {AvailabilityStatusLabels[status] || status}
      </Badge>
    )
  }

  const getListingTypeBadge = (type?: ListingType) => {
    if (!type) return null
    
    const variants: Record<ListingType, "default" | "secondary" | "outline"> = {
      'SALE': 'default',
      'RENT': 'secondary',
      'LEASE': 'outline'
    }

    return (
      <Badge variant={variants[type] || 'outline'}>
        {ListingTypeLabels[type] || type}
      </Badge>
    )
  }

  return (
    <div className="w-full rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead 
              className="w-[140px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('buildingName')}
            >
              <div className="flex items-center gap-1">
                Building Name
                {getSortIcon && getSortIcon('buildingName')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[120px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('location')}
            >
              <div className="flex items-center gap-1">
                Location
                {getSortIcon && getSortIcon('location')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[70px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('numberOfBhk')}
            >
              <div className="flex items-center gap-1">
                BHK
                {getSortIcon && getSortIcon('numberOfBhk')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[60px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('numberOfRk')}
            >
              <div className="flex items-center gap-1">
                RK
                {getSortIcon && getSortIcon('numberOfRk')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[75px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('area')}
            >
              <div className="flex items-center gap-1">
                Area
                {getSortIcon && getSortIcon('area')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[95px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('furnishingStatus')}
            >
              <div className="flex items-center gap-1">
                Furnishing
                {getSortIcon && getSortIcon('furnishingStatus')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[70px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('floor')}
            >
              <div className="flex items-center gap-1">
                Floor
                {getSortIcon && getSortIcon('floor')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[70px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('carParkingSlots')}
            >
              <div className="flex items-center gap-1">
                Parking
                {getSortIcon && getSortIcon('carParkingSlots')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[100px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('quotedAmount')}
            >
              <div className="flex items-center gap-1">
                Quoted Amount
                {getSortIcon && getSortIcon('quotedAmount')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[85px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('listingType')}
            >
              <div className="flex items-center gap-1">
                Listing Type
                {getSortIcon && getSortIcon('listingType')}
              </div>
            </TableHead>
            <TableHead 
              className="w-[80px] cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('availabilityStatus')}
            >
              <div className="flex items-center gap-1">
                Available
                {getSortIcon && getSortIcon('availabilityStatus')}
              </div>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.map((property, index) => (
            <TableRow 
              key={property.id || index}
              className="cursor-pointer hover:bg-muted/50 transition-colors"
              onClick={() => navigate(`/dashboard/property/${property.id || index + 1}`)}
            >
              <TableCell className="font-medium">
                {property.buildingName}
              </TableCell>
              <TableCell className="text-sm">
                {property.location}
              </TableCell>
              <TableCell>
                {property.numberOfBhk}
              </TableCell>
              <TableCell>
                {property.numberOfRk}
              </TableCell>
              <TableCell>
                {formatArea(property.area)}
              </TableCell>
              <TableCell>
                {getFurnishingBadge(property.furnishingStatus)}
              </TableCell>
              <TableCell>
                {property.floor}
              </TableCell>
              <TableCell>
                {property.carParkingSlots}
              </TableCell>
              <TableCell className="font-medium">
                {formatPrice(property.quotedAmount)}
              </TableCell>
              <TableCell>
                {getListingTypeBadge(property.listingType)}
              </TableCell>
              <TableCell>
                {getAvailabilityBadge(property.availabilityStatus)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
} 