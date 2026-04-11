"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { User, Save, AlertCircle, CheckCircle, Lock, Eye, EyeOff } from "lucide-react"
import { AuthApiService } from "@/services/authApiService"
import { patch } from "@/services/api"

// Type for user profile
export type UserProfile = {
    email: string
    firstName: string
    lastName: string
}

// Type for password change
export type PasswordChange = {
    currentPassword: string
    newPassword: string
    confirmPassword: string
}

// Type for password change response
export type PasswordChangeResponse = {
    changedAt: string
    email: string
    message: string
    refreshTokensRevoked: number
    securityNote: string
    username: string
}

export function ProfileForm() {
    const navigate = useNavigate()
    const [profile, setProfile] = useState<UserProfile>({
        email: "",
        firstName: "",
        lastName: ""
    })
    const [passwordData, setPasswordData] = useState<PasswordChange>({
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
    })
    const [isLoading, setIsLoading] = useState(false)
    const [isEditing, setIsEditing] = useState(false)
    const [isChangingPassword, setIsChangingPassword] = useState(false)
    const [showPasswords, setShowPasswords] = useState({
        current: false,
        new: false,
        confirm: false
    })
    const [error, setError] = useState<string | null>(null)
    const [success, setSuccess] = useState<string | null>(null)

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

    // Load user profile on component mount
    useEffect(() => {
        loadProfile()
    }, [])

    const loadProfile = () => {
        try {
            const userData = AuthApiService.getUserData()
            
            if (userData) {
                const profileData = {
                    email: userData.email,
                    firstName: userData.firstName || "", 
                    lastName: userData.lastName || ""
                }
                
                setProfile(profileData)
            }
        } catch (error) {
            console.error("Error loading profile:", error)
            setError("Failed to load profile data")
        }
    }

    const handleInputChange = (field: keyof UserProfile, value: string) => {
        setProfile(prev => ({ ...prev, [field]: value }))
        setError(null)
        setSuccess(null)
    }

    const handlePasswordChange = (field: keyof PasswordChange, value: string) => {
        setPasswordData(prev => ({ ...prev, [field]: value }))
        setError(null)
        setSuccess(null)
    }

    const togglePasswordVisibility = (field: 'current' | 'new' | 'confirm') => {
        setShowPasswords(prev => ({ ...prev, [field]: !prev[field] }))
    }

    const validatePasswordChange = (): boolean => {
        if (!passwordData.currentPassword) {
            setError("Current password is required")
            return false
        }
        if (passwordData.newPassword.length < 8) {
            setError("New password must be at least 8 characters long")
            return false
        }
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setError("New passwords do not match")
            return false
        }
        if (passwordData.currentPassword === passwordData.newPassword) {
            setError("New password must be different from current password")
            return false
        }
        return true
    }

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setError(null)
        setSuccess(null)

        try {
            // Call the real update profile API
            const response = await patch<UserProfile>('/auth/v1/update-profile', {
                firstName: profile.firstName,
                lastName: profile.lastName,
                email: profile.email
            });

            if (response.error) {
                throw new Error(response.error);
            }

            // Update user data in session storage
            AuthApiService.updateUserData({
                firstName: profile.firstName,
                lastName: profile.lastName,
                email: profile.email
            });

            setSuccess("Profile updated successfully!")
            setIsEditing(false)
        } catch (err) {
            // Show the actual backend error message
            const errorMessage = err instanceof Error ? err.message : "Failed to update profile. Please try again."
            setError(errorMessage)
        } finally {
            setIsLoading(false)
        }
    }

    const handlePasswordSave = async (e: React.FormEvent) => {
        e.preventDefault()
        
        if (!validatePasswordChange()) {
            return
        }

        setIsLoading(true)
        setError(null)
        setSuccess(null)

        try {
            // Call the real change password API
            const response = await patch<PasswordChangeResponse>('/auth/v1/change-password', {
                currentPassword: passwordData.currentPassword,
                newPassword: passwordData.newPassword,
                confirmPassword: passwordData.confirmPassword
            });

            if (response.error) {
                throw new Error(response.error);
            }

            // Show success message with security note
            const successMessage = response.data?.message || "Password change FAILED!"
            const securityNote = response.data?.securityNote || "Please log in again."
            
            setSuccess(`${successMessage} ${securityNote}`)
            
            // Clear password form
            setPasswordData({
                currentPassword: "",
                newPassword: "",
                confirmPassword: ""
            })
            
            // Log out user and redirect to login after a short delay
            setTimeout(async () => {
                try {
                    await AuthApiService.logout()
                    navigate("/login")
                } catch (logoutError) {
                    console.error("Logout error:", logoutError)
                    // Even if logout fails, clear local data and redirect
                    AuthApiService.clearLocalAuthData()
                    navigate("/login")
                }
            }, 3000) // 3 second delay to show the message
        } catch (err) {
            // Show the actual backend error message
            const errorMessage = err instanceof Error ? err.message : "Failed to change password. Please try again."
            setError(errorMessage)
        } finally {
            setIsLoading(false)
        }
    }

    const handleCancel = () => {
        loadProfile() // Reload original data
        setIsEditing(false)
        setError(null)
        setSuccess(null)
    }

    const handlePasswordCancel = () => {
        setIsChangingPassword(false)
        setPasswordData({
            currentPassword: "",
            newPassword: "",
            confirmPassword: ""
        })
        setError(null)
        setSuccess(null)
    }

    return (
        <div className="space-y-6">
            {/* Profile Information Card */}
            <Card className="w-full max-w-2xl mx-auto">
                <CardHeader className="text-center">
                    <div className="flex items-center justify-center mb-4">
                        <div className="rounded-full bg-primary/10 p-3">
                            <User className="h-8 w-8 text-primary" />
                        </div>
                    </div>
                    <CardTitle className="text-2xl font-bold text-gray-900">Profile Information</CardTitle>
                </CardHeader>
                <form onSubmit={handleSave}>
                    <CardContent className="space-y-6">
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

                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="firstName">First Name</Label>
                                <Input
                                    id="firstName"
                                    type="text"
                                    value={profile.firstName}
                                    onChange={(e) => handleInputChange("firstName", e.target.value)}
                                    disabled={!isEditing}
                                    required
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="lastName">Last Name</Label>
                                <Input
                                    id="lastName"
                                    type="text"
                                    value={profile.lastName}
                                    onChange={(e) => handleInputChange("lastName", e.target.value)}
                                    disabled={!isEditing}
                                    required
                                />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                value={profile.email}
                                onChange={(e) => handleInputChange("email", e.target.value)}
                                disabled={!isEditing}
                                required
                            />
                        </div>
                    </CardContent>
                    <CardFooter className="flex justify-end gap-2">
                        {!isEditing ? (
                            <Button 
                                type="button" 
                                onClick={() => setIsEditing(true)}
                                className="w-full sm:w-auto"
                            >
                                Edit Profile
                            </Button>
                        ) : (
                            <>
                                <Button 
                                    type="button" 
                                    variant="outline" 
                                    onClick={handleCancel}
                                    className="w-full sm:w-auto"
                                >
                                    Cancel
                                </Button>
                                <Button 
                                    type="submit" 
                                    disabled={isLoading}
                                    className="w-full sm:w-auto"
                                >
                                    {isLoading ? (
                                        "Saving"
                                    ) : (
                                        <>
                                            <Save className="mr-2 h-4 w-4" />
                                            Save Changes
                                        </>
                                    )}
                                </Button>
                            </>
                        )}
                    </CardFooter>
                </form>
            </Card>

            {/* Change Password Card */}
            <Card className="w-full max-w-2xl mx-auto">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Lock className="h-5 w-5" />
                        Change Password
                    </CardTitle>
                    <p className="text-sm text-muted-foreground">Update your password to keep your account secure</p>
                </CardHeader>
                <form onSubmit={handlePasswordSave}>
                    <CardContent className="space-y-6">
                        {!isChangingPassword ? (
                            <div className="text-center py-4">
                                <Button 
                                    type="button" 
                                    variant="outline"
                                    onClick={() => setIsChangingPassword(true)}
                                    className="w-full sm:w-auto"
                                >
                                    Change Password
                                </Button>
                            </div>
                        ) : (
                            <>
                                <div className="space-y-2">
                                    <Label htmlFor="currentPassword">Current Password</Label>
                                    <div className="relative">
                                        <Input
                                            id="currentPassword"
                                            type={showPasswords.current ? "text" : "password"}
                                            value={passwordData.currentPassword}
                                            onChange={(e) => handlePasswordChange("currentPassword", e.target.value)}
                                            required
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => togglePasswordVisibility('current')}
                                        >
                                            {showPasswords.current ? (
                                                <EyeOff className="h-4 w-4" />
                                            ) : (
                                                <Eye className="h-4 w-4" />
                                            )}
                                        </Button>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="newPassword">New Password</Label>
                                    <div className="relative">
                                        <Input
                                            id="newPassword"
                                            type={showPasswords.new ? "text" : "password"}
                                            value={passwordData.newPassword}
                                            onChange={(e) => handlePasswordChange("newPassword", e.target.value)}
                                            required
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => togglePasswordVisibility('new')}
                                        >
                                            {showPasswords.new ? (
                                                <EyeOff className="h-4 w-4" />
                                            ) : (
                                                <Eye className="h-4 w-4" />
                                            )}
                                        </Button>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="confirmPassword">Confirm New Password</Label>
                                    <div className="relative">
                                        <Input
                                            id="confirmPassword"
                                            type={showPasswords.confirm ? "text" : "password"}
                                            value={passwordData.confirmPassword}
                                            onChange={(e) => handlePasswordChange("confirmPassword", e.target.value)}
                                            required
                                        />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                            onClick={() => togglePasswordVisibility('confirm')}
                                        >
                                            {showPasswords.confirm ? (
                                                <EyeOff className="h-4 w-4" />
                                            ) : (
                                                <Eye className="h-4 w-4" />
                                            )}
                                        </Button>
                                    </div>
                                </div>
                            </>
                        )}
                    </CardContent>
                    {isChangingPassword && (
                        <CardFooter className="flex justify-end gap-2">
                            <Button 
                                type="button" 
                                variant="outline" 
                                onClick={handlePasswordCancel}
                                className="w-full sm:w-auto"
                            >
                                Cancel
                            </Button>
                            <Button 
                                type="submit" 
                                disabled={isLoading}
                                className="w-full sm:w-auto"
                            >
                                {isLoading ? (
                                    "Changing Password"
                                ) : (
                                    <>
                                        <Lock className="mr-2 h-4 w-4" />
                                        Change Password
                                    </>
                                )}
                            </Button>
                        </CardFooter>
                    )}
                </form>
            </Card>
        </div>
    )
} 