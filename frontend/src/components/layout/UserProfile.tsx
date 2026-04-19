"use client"

import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { LogOut } from "lucide-react"
import { AuthApiService } from "@/services/authApiService"

export function UserProfile() {
  const navigate = useNavigate()
  const [isLoggingOut, setIsLoggingOut] = useState(false)

  const handleLogout = async () => {
    setIsLoggingOut(true)
    
    try {
      // Call backend logout API and clear local storage
      await AuthApiService.logout()
      
      // Redirect to login page
      navigate("/login")
    } catch (error) {
      console.error("[USER_PROFILE] Logout error:", error)
      
      // Even if there's an error, redirect to login
      // (local storage is cleared by AuthApiService.logout())
      navigate("/login")
    } finally {
      setIsLoggingOut(false)
    }
  }

  return (
    <Button 
      variant="outline" 
      size="sm"
      onClick={handleLogout}
      disabled={isLoggingOut}
      className="flex items-center gap-2 hover:bg-red-50 hover:text-red-600 hover:border-red-200 transition-colors disabled:opacity-50"
    >
      <LogOut className="h-4 w-4" />
      <span className="hidden sm:inline">
        {isLoggingOut ? "Logging out..." : "Logout"}
      </span>
    </Button>
  )
} 