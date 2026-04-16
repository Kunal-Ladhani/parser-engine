import { useCallback, useEffect, useState } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { ArrowLeft, Edit, Save, X, AlertCircle, CheckCircle } from "lucide-react"
import PropertyApiService from "@/services/propertyApiService"
import type { PropertyDetails, FurnishingStatus, ListingType, AvailabilityStatus, MarkStatusRequest } from "@/types/property"
import { AvailabilityStatusLabels, FurnishingStatusLabels, ListingTypeLabels } from "@/types/property"

interface PropertyFormData {
  buildingName: string
  location: string
  floor: string
  numberOfBhk: number
  numberOfRk: number
  furnishingStatus: FurnishingStatus
  area: number
  quotedAmount: number
  carParkingSlots: number
  comment: string
  brokerName: string
  brokerPhone: string
  listingType: ListingType
  availabilityStatus: AvailabilityStatus
  leaseEndDate: string
  rentalEndDate: string
}

export default function PropertyDetailsPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [property, setProperty] = useState<PropertyDetails | null>(null)
  const [formData, setFormData] = useState<PropertyFormData>({
    buildingName: "",
    location: "",
    floor: "",
    numberOfBhk: 0,
    numberOfRk: 0,
    furnishingStatus: "UF",
    area: 0,
    quotedAmount: 0,
    carParkingSlots: 0,
    comment: "",
    brokerName: "",
    brokerPhone: "",
    listingType: "RENT",
    availabilityStatus: "AVAILABLE",
    leaseEndDate: "",
    rentalEndDate: ""
  })
  const [isLoading, setIsLoading] = useState(true)
  const [isEditing, setIsEditing] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  
  // Status marking state
  const [isMarkingStatus, setIsMarkingStatus] = useState(false)
  const [selectedStatus, setSelectedStatus] = useState<AvailabilityStatus>("AVAILABLE")
  const [statusFormData, setStatusFormData] = useState({
    soldBy: "",
    soldTo: "",
    soldOn: "",
    soldForAmount: 0,
    leasedBy: "",
    leasedTo: "",
    leasedOn: "",
    leasedForAmount: 0,
    rentedBy: "",
    rentedTo: "",
    rentedOn: "",
    rentedForAmount: 0
  })

  const updateFormDataFromProperty = useCallback((updatedProperty: PropertyDetails) => {
    setFormData({
      buildingName: updatedProperty.buildingName || "",
      location: updatedProperty.location || "",
      floor: updatedProperty.floor || "",
      numberOfBhk: updatedProperty.numberOfBhk || 0,
      numberOfRk: updatedProperty.numberOfRk || 0,
      furnishingStatus: updatedProperty.furnishingStatus || "UF",
      area: updatedProperty.area || 0,
      quotedAmount: updatedProperty.quotedAmount || 0,
      carParkingSlots: updatedProperty.carParkingSlots || 0,
      comment: updatedProperty.comment || "",
      brokerName: updatedProperty.brokerName || "",
      brokerPhone: updatedProperty.brokerPhone || "",
      listingType: updatedProperty.listingType || "RENT",
      availabilityStatus: updatedProperty.availabilityStatus || "AVAILABLE",
      leaseEndDate: updatedProperty.leaseEndDate ? updatedProperty.leaseEndDate.split('T')[0] : "",
      rentalEndDate: updatedProperty.rentalEndDate ? updatedProperty.rentalEndDate.split('T')[0] : ""
    })
  }, [])

  const fetchProperty = useCallback(async (propertyId: string) => {
    setIsLoading(true)
    setError(null)
    try {
      const data = await PropertyApiService.getPropertyById(propertyId)
      if (data) {
        setProperty(data)
        updateFormDataFromProperty(data)
      } else {
        setError("Property not found.")
      }
    } catch {
      setError("Failed to load property details.")
    } finally {
      setIsLoading(false)
    }
  }, [updateFormDataFromProperty])

  useEffect(() => {
    if (id) {
      void fetchProperty(id)
    }
  }, [id, fetchProperty])

  // Auto-clear success and error messages after 5 seconds
  useEffect(() => {
    if (success || error) {
      const timer = setTimeout(() => {
        setSuccess(null)
        setError(null)
      }, 5000)
      
      return () => clearTimeout(timer)
    }
  }, [success, error])

  const handleInputChange = (field: keyof PropertyFormData, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    setError(null)
    setSuccess(null)
  }

  const handleSave = async () => {
    if (!property?.id) return

    setIsSaving(true)
    setError(null)
    setSuccess(null)

    try {
      const updates: Partial<PropertyDetails> = {
        buildingName: formData.buildingName,
        location: formData.location,
        floor: formData.floor,
        numberOfBhk: Number(formData.numberOfBhk),
        numberOfRk: Number(formData.numberOfRk),
        furnishingStatus: formData.furnishingStatus,
        area: Number(formData.area),
        quotedAmount: Number(formData.quotedAmount),
        carParkingSlots: Number(formData.carParkingSlots),
        comment: formData.comment,
        brokerName: formData.brokerName,
        brokerPhone: formData.brokerPhone,
        listingType: formData.listingType,
        availabilityStatus: formData.availabilityStatus,
        leaseEndDate: formData.leaseEndDate || undefined,
        rentalEndDate: formData.rentalEndDate || undefined
      }

      const updatedProperty = await PropertyApiService.updateProperty(property.id, updates)
      
      if (updatedProperty) {
        setProperty(updatedProperty)
        updateFormDataFromProperty(updatedProperty)
        setSuccess("Property updated successfully!")
        setIsEditing(false)
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Failed to update property. Please try again."
      setError(errorMessage)
    } finally {
      setIsSaving(false)
    }
  }

  const handleCancel = () => {
    if (property) {
      updateFormDataFromProperty(property)
    }
    setIsEditing(false)
    setError(null)
    setSuccess(null)
  }

  const handleStatusChange = (field: string, value: string | number) => {
    setStatusFormData(prev => ({ ...prev, [field]: value }))
  }

  const handleMarkStatus = async () => {
    if (!property?.id) return

    setIsSaving(true)
    setError(null)
    setSuccess(null)

    try {
      const statusRequest: MarkStatusRequest = {
        availabilityStatus: selectedStatus
      }

      // Add relevant fields based on status
      if (selectedStatus === "SOLD") {
        statusRequest.soldBy = statusFormData.soldBy
        statusRequest.soldTo = statusFormData.soldTo
        statusRequest.soldOn = statusFormData.soldOn
        statusRequest.soldForAmount = statusFormData.soldForAmount
      } else if (selectedStatus === "LEASED") {
        statusRequest.leasedBy = statusFormData.leasedBy
        statusRequest.leasedTo = statusFormData.leasedTo
        statusRequest.leasedOn = statusFormData.leasedOn
        statusRequest.leasedForAmount = statusFormData.leasedForAmount
      } else if (selectedStatus === "RENTED") {
        statusRequest.rentedBy = statusFormData.rentedBy
        statusRequest.rentedTo = statusFormData.rentedTo
        statusRequest.rentedOn = statusFormData.rentedOn
        statusRequest.rentedForAmount = statusFormData.rentedForAmount
      }

      const updatedProperty = await PropertyApiService.markPropertyStatus(property.id, statusRequest)
      
      if (updatedProperty) {
        setProperty(updatedProperty)
        updateFormDataFromProperty(updatedProperty)
        setSuccess(`Property marked as ${AvailabilityStatusLabels[selectedStatus]} successfully!`)
        setIsMarkingStatus(false)
        // Reset status form
        setStatusFormData({
          soldBy: "",
          soldTo: "",
          soldOn: "",
          soldForAmount: 0,
          leasedBy: "",
          leasedTo: "",
          leasedOn: "",
          leasedForAmount: 0,
          rentedBy: "",
          rentedTo: "",
          rentedOn: "",
          rentedForAmount: 0
        })
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Failed to mark property status. Please try again."
      setError(errorMessage)
    } finally {
      setIsSaving(false)
    }
  }

  const handleStatusCancel = () => {
    setIsMarkingStatus(false)
    setSelectedStatus("AVAILABLE")
    setStatusFormData({
      soldBy: "",
      soldTo: "",
      soldOn: "",
      soldForAmount: 0,
      leasedBy: "",
      leasedTo: "",
      leasedOn: "",
      leasedForAmount: 0,
      rentedBy: "",
      rentedTo: "",
      rentedOn: "",
      rentedForAmount: 0
    })
    setError(null)
    setSuccess(null)
  }

  const formatPrice = (price?: number) => {
    if (!price) return "-"
    const actualPrice = price * 1000
    return `₹${actualPrice.toLocaleString('en-IN')}/month`
  }

  const formatDisplayValue = (value: unknown, field: string): string => {
    if (value === undefined || value === null || value === "") return "-"

    switch (field) {
      case "quotedAmount":
        return formatPrice(value as number)
      case "furnishingStatus":
        return (
          FurnishingStatusLabels[value as FurnishingStatus] ?? String(value)
        )
      case "listingType":
        return ListingTypeLabels[value as ListingType] ?? String(value)
      case "availabilityStatus":
        return (
          AvailabilityStatusLabels[value as AvailabilityStatus] ??
          String(value)
        )
      case "area":
        return `${value} sq ft`
      default:
        return String(value)
    }
  }

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading property details...</div>
  }

  if (error && !property) {
    return (
      <div className="p-8 text-center text-red-600">
        {error}
        <div className="mt-4">
          <Button variant="outline" onClick={() => navigate(-1)}>
            <ArrowLeft className="mr-2 h-4 w-4" /> Back to Search
          </Button>
        </div>
      </div>
    )
  }

  if (!property) {
    return (
      <div className="p-8 text-center text-red-600">
        Property not found.
        <div className="mt-4">
          <Button variant="outline" onClick={() => navigate(-1)}>
            <ArrowLeft className="mr-2 h-4 w-4" /> Back to Search
          </Button>
        </div>
      </div>
    )
  }

  const fieldDefinitions = [
    { key: "buildingName", label: "Building Name", type: "text" as const },
    { key: "location", label: "Location", type: "text" as const },
    { key: "floor", label: "Floor", type: "text" as const },
    { key: "numberOfBhk", label: "Number of BHK", type: "number" as const },
    { key: "numberOfRk", label: "Number of RK", type: "number" as const },
    { key: "furnishingStatus", label: "Furnishing Status", type: "select" as const, options: [
      { value: "UF", label: "Unfurnished" },
      { value: "SF", label: "Semi Furnished" },
      { value: "FF", label: "Fully Furnished" }
    ]},
    { key: "area", label: "Area (sq ft)", type: "number" as const },
    { key: "quotedAmount", label: "Quoted Amount (in thousands)", type: "number" as const },
    { key: "carParkingSlots", label: "Car Parking Slots", type: "number" as const },
    { key: "listingType", label: "Listing Type", type: "select" as const, options: [
      { value: "SALE", label: "Sale" },
      { value: "RENT", label: "Rent" },
      { value: "LEASE", label: "Lease" }
    ]},
    { key: "availabilityStatus", label: "Availability Status", type: "select" as const, options: [
      { value: "AVAILABLE", label: "Available" },
      { value: "SOLD", label: "Sold" },
      { value: "RENTED", label: "Rented" },
      { value: "LEASED", label: "Leased" }
    ]},
    { key: "leaseEndDate", label: "Lease End Date", type: "date" as const },
    { key: "rentalEndDate", label: "Rental End Date", type: "date" as const },
    { key: "brokerName", label: "Broker Name", type: "text" as const },
    { key: "brokerPhone", label: "Broker Phone", type: "text" as const },
    { key: "comment", label: "Comment", type: "textarea" as const }
  ]

  return (
    <div className="container mx-auto max-w-4xl p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="outline" onClick={() => navigate(-1)}>
            <ArrowLeft className="mr-2 h-4 w-4" /> Back
          </Button>
          <h1 className="text-2xl font-bold">
            {formData.buildingName || "Property Details"}
          </h1>
          <Badge variant={property.availabilityStatus === "AVAILABLE" ? "default" : "secondary"}>
            {formatDisplayValue(property.availabilityStatus, "availabilityStatus")}
          </Badge>
        </div>
        
        <div className="flex items-center gap-2">
          {!isEditing ? (
            <Button onClick={() => setIsEditing(true)}>
              <Edit className="mr-2 h-4 w-4" />
              Edit Property
            </Button>
          ) : (
            <>
              <Button variant="outline" onClick={handleCancel}>
                <X className="mr-2 h-4 w-4" />
                Cancel
              </Button>
              <Button onClick={handleSave} disabled={isSaving}>
                <Save className="mr-2 h-4 w-4" />
                {isSaving ? "Saving..." : "Save Changes"}
              </Button>
            </>
          )}
        </div>
      </div>

      {/* Status Messages */}
      {error && (
        <div className="rounded-md bg-destructive/15 p-3 text-sm text-destructive flex items-center gap-2">
          <AlertCircle className="h-4 w-4" />
          {error}
        </div>
      )}
      
      {success && (
        <div className="rounded-md bg-green-500/15 p-3 text-sm text-green-600 flex items-center gap-2">
          <CheckCircle className="h-4 w-4" />
          {success}
        </div>
      )}

      {/* Property Details Card */}
      <Card>
        <CardHeader>
          <CardTitle>Property Details</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {fieldDefinitions.map((field) => {
              const value = formData[field.key as keyof PropertyFormData]
              
              return (
                <div key={field.key} className="space-y-2">
                  <Label className="text-sm font-medium text-muted-foreground">
                    {field.label}
                  </Label>
                  
                  {isEditing ? (
                    <div>
                      {field.type === "text" && (
                        <Input
                          value={String(value)}
                          onChange={(e) => handleInputChange(field.key as keyof PropertyFormData, e.target.value)}
                        />
                      )}
                      
                      {field.type === "number" && (
                        <Input
                          type="text"
                          value={String(value)}
                          onChange={(e) => {
                            const numValue = parseFloat(e.target.value) || 0
                            handleInputChange(field.key as keyof PropertyFormData, numValue)
                          }}
                          placeholder="Enter number"
                        />
                      )}
                      
                      {field.type === "date" && (
                        <Input
                          type="date"
                          value={String(value)}
                          onChange={(e) => handleInputChange(field.key as keyof PropertyFormData, e.target.value)}
                        />
                      )}
                      
                      {field.type === "select" && field.options && (
                        <Select
                          value={String(value)}
                          onValueChange={(newValue) => handleInputChange(field.key as keyof PropertyFormData, newValue)}
                        >
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            {field.options.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      )}
                      
                      {field.type === "textarea" && (
                        <Textarea
                          value={String(value)}
                          onChange={(e) => handleInputChange(field.key as keyof PropertyFormData, e.target.value)}
                          rows={3}
                        />
                      )}
                    </div>
                  ) : (
                    <div className="font-medium">
                      {formatDisplayValue(value, field.key)}
                    </div>
                  )}
                </div>
              )
            })}
          </div>

          {/* Property ID - Always read-only */}
          <div className="mt-6 pt-4 border-t">
            <div className="space-y-2">
              <Label className="text-sm font-medium text-muted-foreground">Property ID</Label>
              <div className="font-mono text-xs text-muted-foreground">{property.id}</div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Status Management Card */}
      <Card>
        <CardHeader>
          <CardTitle>Status Management</CardTitle>
          <p className="text-sm text-muted-foreground">Mark property as sold, rented, leased, or available</p>
        </CardHeader>
        <CardContent>
          {!isMarkingStatus ? (
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <span className="text-sm font-medium">Current Status:</span>
                <Badge variant={property.availabilityStatus === "AVAILABLE" ? "default" : "secondary"}>
                  {formatDisplayValue(property.availabilityStatus, "availabilityStatus")}
                </Badge>
              </div>
              <Button 
                onClick={() => setIsMarkingStatus(true)}
                disabled={isEditing || isSaving}
              >
                Update Status
              </Button>
            </div>
          ) : (
            <div className="space-y-4">
              <div className="space-y-2">
                <Label>New Status</Label>
                <Select
                  value={selectedStatus}
                  onValueChange={(value) => setSelectedStatus(value as AvailabilityStatus)}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="AVAILABLE">Available</SelectItem>
                    <SelectItem value="SOLD">Sold</SelectItem>
                    <SelectItem value="RENTED">Rented</SelectItem>
                    <SelectItem value="LEASED">Leased</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Status-specific fields */}
              {selectedStatus === "SOLD" && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Sold By</Label>
                    <Input
                      value={statusFormData.soldBy}
                      onChange={(e) => handleStatusChange("soldBy", e.target.value)}
                      placeholder="Enter broker/agent name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Sold To</Label>
                    <Input
                      value={statusFormData.soldTo}
                      onChange={(e) => handleStatusChange("soldTo", e.target.value)}
                      placeholder="Enter buyer name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Sold On</Label>
                    <Input
                      type="date"
                      value={statusFormData.soldOn}
                      onChange={(e) => handleStatusChange("soldOn", e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Sold For Amount</Label>
                    <Input
                      type="text"
                      value={String(statusFormData.soldForAmount)}
                      onChange={(e) => {
                        const numValue = parseFloat(e.target.value) || 0
                        handleStatusChange("soldForAmount", numValue)
                      }}
                      placeholder="Enter sold amount"
                    />
                  </div>
                </div>
              )}

              {selectedStatus === "LEASED" && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Leased By</Label>
                    <Input
                      value={statusFormData.leasedBy}
                      onChange={(e) => handleStatusChange("leasedBy", e.target.value)}
                      placeholder="Enter broker/agent name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Leased To</Label>
                    <Input
                      value={statusFormData.leasedTo}
                      onChange={(e) => handleStatusChange("leasedTo", e.target.value)}
                      placeholder="Enter tenant company name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Leased On</Label>
                    <Input
                      type="date"
                      value={statusFormData.leasedOn}
                      onChange={(e) => handleStatusChange("leasedOn", e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Leased For Amount</Label>
                    <Input
                      type="text"
                      value={String(statusFormData.leasedForAmount)}
                      onChange={(e) => {
                        const numValue = parseFloat(e.target.value) || 0
                        handleStatusChange("leasedForAmount", numValue)
                      }}
                      placeholder="Enter lease amount"
                    />
                  </div>
                </div>
              )}

              {selectedStatus === "RENTED" && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label>Rented By</Label>
                    <Input
                      value={statusFormData.rentedBy}
                      onChange={(e) => handleStatusChange("rentedBy", e.target.value)}
                      placeholder="Enter broker/agent name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Rented To</Label>
                    <Input
                      value={statusFormData.rentedTo}
                      onChange={(e) => handleStatusChange("rentedTo", e.target.value)}
                      placeholder="Enter tenant name"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Rented On</Label>
                    <Input
                      type="date"
                      value={statusFormData.rentedOn}
                      onChange={(e) => handleStatusChange("rentedOn", e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label>Rented For Amount</Label>
                    <Input
                      type="text"
                      value={String(statusFormData.rentedForAmount)}
                      onChange={(e) => {
                        const numValue = parseFloat(e.target.value) || 0
                        handleStatusChange("rentedForAmount", numValue)
                      }}
                      placeholder="Enter rental amount"
                    />
                  </div>
                </div>
              )}

              <div className="flex justify-end gap-2 pt-4">
                <Button variant="outline" onClick={handleStatusCancel}>
                  Cancel
                </Button>
                <Button onClick={handleMarkStatus} disabled={isSaving}>
                  {isSaving ? "Updating..." : `Mark as ${AvailabilityStatusLabels[selectedStatus]}`}
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}