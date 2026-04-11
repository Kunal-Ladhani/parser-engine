import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import { PropertySearchDashboard } from "@/components/dashboard/PropertySearchDashboard"

export default function SearchPage() {
    const navigate = useNavigate()

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-start">
                <div>
                    <h1 className="text-3xl font-bold">Property Search</h1>
                    <p className="text-muted-foreground">Search and filter properties with advanced options</p>
                </div>
                <Button 
                    onClick={() => navigate("/dashboard/add-property")}
                    className="flex items-center gap-2"
                >
                    <Plus className="h-4 w-4" />
                    Add Property
                </Button>
            </div>
            <PropertySearchDashboard />
        </div>
    )
}
