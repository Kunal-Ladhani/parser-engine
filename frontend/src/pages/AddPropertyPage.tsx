import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { ArrowLeft, Save, AlertCircle, CheckCircle } from "lucide-react"
import PropertyApiService from "@/services/propertyApiService"
import type { PropertyDetails, FurnishingStatus, ListingType, AvailabilityStatus } from "@/types/property"

interface AddPropertyFormData {
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

export default function AddPropertyPage() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState<AddPropertyFormData>({
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
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  const handleInputChange = (field: keyof AddPropertyFormData, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    setError(null)
    setSuccess(null)
  }

  const validateForm = (): boolean => {
    // Check mandatory fields
    if (!formData.buildingName.trim()) {
      setError("Building Name is required")
      return false
    }
    if (!formData.location.trim()) {
      setError("Location is required")
      return false
    }
    if (!formData.listingType) {
      setError("Listing Type is required")
      return false
    }
    if (!formData.availabilityStatus) {
      setError("Availability Status is required")
      return false
    }
    return true
  }

  const handleSave = async () => {
    if (!validateForm()) return

    setIsSaving(true)
    setError(null)
    setSuccess(null)

    try {
      const propertyData: Omit<PropertyDetails, 'id' | 'dateAdded'> = {
        buildingName: formData.buildingName,
        location: formData.location,
        floor: formData.floor || undefined,
        numberOfBhk: formData.numberOfBhk || undefined,
        numberOfRk: formData.numberOfRk || undefined,
        furnishingStatus: formData.furnishingStatus,
        area: formData.area || undefined,
        quotedAmount: formData.quotedAmount || undefined,
        carParkingSlots: formData.carParkingSlots || undefined,
        comment: formData.comment || undefined,
        brokerName: formData.brokerName || undefined,
        brokerPhone: formData.brokerPhone || undefined,
        listingType: formData.listingType,
        availabilityStatus: formData.availabilityStatus,
        leaseEndDate: formData.leaseEndDate || undefined,
        rentalEndDate: formData.rentalEndDate || undefined
      }

      const createdProperty = await PropertyApiService.createProperty(propertyData)
      
      if (createdProperty && createdProperty.id) {
        setSuccess("Property created successfully!")
        // Redirect to the property details page after a short delay
        setTimeout(() => {
          navigate(`/dashboard/property/${createdProperty.id}`)
        }, 1500)
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Failed to create property. Please try again."
      setError(errorMessage)
    } finally {
      setIsSaving(false)
    }
  }

  const fieldDefinitions = [
    { key: "buildingName", label: "Building Name", type: "text" as const, required: true },
    { key: "location", label: "Location", type: "text" as const, required: true },
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
    { key: "listingType", label: "Listing Type", type: "select" as const, required: true, options: [
      { value: "SALE", label: "Sale" },
      { value: "RENT", label: "Rent" },
      { value: "LEASE", label: "Lease" }
    ]},
    { key: "availabilityStatus", label: "Availability Status", type: "select" as const, required: true, options: [
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
          <h1 className="text-2xl font-bold">Add New Property</h1>
        </div>
        
        <Button onClick={handleSave} disabled={isSaving}>
          <Save className="mr-2 h-4 w-4" />
          {isSaving ? "Creating..." : "Create Property"}
        </Button>
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

      {/* Property Form Card */}
      <Card>
        <CardHeader>
          <CardTitle>Property Information</CardTitle>
          <p className="text-sm text-muted-foreground">
            Fields marked with <span className="text-red-500">*</span> are required
          </p>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {fieldDefinitions.map((field) => {
              const value = formData[field.key as keyof AddPropertyFormData]
              
              return (
                <div key={field.key} className="space-y-2">
                  <Label className="text-sm font-medium text-muted-foreground">
                    {field.label}
                    {field.required && <span className="text-red-500 ml-1">*</span>}
                  </Label>
                  
                  <div>
                    {field.type === "text" && (
                      <Input
                        value={String(value)}
                        onChange={(e) => handleInputChange(field.key as keyof AddPropertyFormData, e.target.value)}
                        placeholder={`Enter ${field.label.toLowerCase()}`}
                      />
                    )}
                    
                    {field.type === "number" && (
                      <Input
                        type="text"
                        value={String(value)}
                        onChange={(e) => {
                          const numValue = parseFloat(e.target.value) || 0
                          handleInputChange(field.key as keyof AddPropertyFormData, numValue)
                        }}
                        placeholder="Enter number"
                      />
                    )}
                    
                    {field.type === "date" && (
                      <Input
                        type="date"
                        value={String(value)}
                        onChange={(e) => handleInputChange(field.key as keyof AddPropertyFormData, e.target.value)}
                      />
                    )}
                    
                    {field.type === "select" && field.options && (
                      <Select
                        value={String(value)}
                        onValueChange={(newValue) => handleInputChange(field.key as keyof AddPropertyFormData, newValue)}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder={`Select ${field.label.toLowerCase()}`} />
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
                        onChange={(e) => handleInputChange(field.key as keyof AddPropertyFormData, e.target.value)}
                        rows={3}
                        placeholder={`Enter ${field.label.toLowerCase()}`}
                      />
                    )}
                  </div>
                </div>
              )
            })}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
