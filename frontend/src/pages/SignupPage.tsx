"use client"

import type React from "react"

import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Eye, EyeOff } from "lucide-react"
import { AuthApiService, type SignupRequest } from "@/services/authApiService"

// Type for signup form data (includes confirmPassword for validation)
type SignupFormData = SignupRequest & {
    confirmPassword: string;
};

export default function SignupPage() {
    const navigate = useNavigate()
    const [formData, setFormData] = useState<SignupFormData>({
        firstName: "",
        lastName: "",
        email: "",
        username: "",
        password: "",
        confirmPassword: ""
    })
    const [showPassword, setShowPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleInputChange = (field: keyof SignupFormData, value: string) => {
        setFormData(prev => ({ ...prev, [field]: value }))
        setError(null) // Clear error when user starts typing
    }

    const validateForm = (): boolean => {
        // First name validation
        if (!formData.firstName.trim()) {
            setError("First name is required")
            return false
        }
        if (formData.firstName.length < 2 || formData.firstName.length > 50) {
            setError("First name must be between 2 and 50 characters")
            return false
        }

        // Last name validation
        if (!formData.lastName.trim()) {
            setError("Last name is required")
            return false
        }
        if (formData.lastName.length < 2 || formData.lastName.length > 50) {
            setError("Last name must be between 2 and 50 characters")
            return false
        }

        // Email validation
        if (!formData.email.trim()) {
            setError("Email is required")
            return false
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(formData.email)) {
            setError("Email should be valid")
            return false
        }
        if (formData.email.length > 100) {
            setError("Email must not exceed 100 characters")
            return false
        }

        // Username validation
        if (!formData.username.trim()) {
            setError("Username is required")
            return false
        }
        if (formData.username.length < 3 || formData.username.length > 30) {
            setError("Username must be between 3 and 30 characters")
            return false
        }
        const usernameRegex = /^[a-zA-Z0-9_-]+$/
        if (!usernameRegex.test(formData.username)) {
            setError("Username can only contain letters, numbers, underscores, and hyphens")
            return false
        }

        // Password validation
        if (!formData.password.trim()) {
            setError("Password is required")
            return false
        }
        if (formData.password.length < 8 || formData.password.length > 100) {
            setError("Password must be between 8 and 100 characters")
            return false
        }
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/
        if (!passwordRegex.test(formData.password)) {
            setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
            return false
        }

        // Confirm password validation
        if (!formData.confirmPassword.trim()) {
            setError("Please confirm your password")
            return false
        }
        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match")
            return false
        }

        return true
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError(null)
        
        if (!validateForm()) {
            return
        }

        setIsLoading(true)

        try {
            // Prepare data for API (exclude confirmPassword)
            const signupData: SignupRequest = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                username: formData.username,
                password: formData.password
            };

            // Call the real signup API
            await AuthApiService.signup(signupData);

            // Navigate to dashboard after successful signup
            navigate("/dashboard")
        } catch (err) {
            // Show the actual backend error message
            const errorMessage = err instanceof Error ? err.message : "Failed to create account. Please try again."
            setError(errorMessage)
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="flex h-screen w-full items-center justify-center bg-gray-50">
            <div className="w-full max-w-md space-y-8 p-8">
                <div className="text-center">
                    <h1 className="text-4xl font-bold text-gray-900">Create Account</h1>
                    <p className="text-sm text-muted-foreground mt-2">Join us and start managing your files</p>
                </div>

                <Card>
                    <CardHeader>
                        <CardTitle>Sign Up</CardTitle>
                    </CardHeader>
                    <form onSubmit={handleSubmit}>
                        <CardContent className="space-y-4">
                            {error && (
                                <div className="rounded-md bg-red-50 border border-red-200 p-3">
                                    <div className="text-sm text-red-700 font-medium">{error}</div>
                                </div>
                            )}

                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label htmlFor="firstName">First Name</Label>
                                    <Input
                                        id="firstName"
                                        type="text"
                                        placeholder="First name"
                                        value={formData.firstName}
                                        onChange={(e) => handleInputChange("firstName", e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="lastName">Last Name</Label>
                                    <Input
                                        id="lastName"
                                        type="text"
                                        placeholder="Last name"
                                        value={formData.lastName}
                                        onChange={(e) => handleInputChange("lastName", e.target.value)}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="Enter your email"
                                    value={formData.email}
                                    onChange={(e) => handleInputChange("email", e.target.value)}
                                    required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="username">Username</Label>
                                <Input
                                    id="username"
                                    type="text"
                                    placeholder="Choose a username"
                                    value={formData.username}
                                    onChange={(e) => handleInputChange("username", e.target.value)}
                                    required
                                />
                                {formData.username && (
                                    <div className="text-xs text-gray-500">
                                        {formData.username.length < 3 ? (
                                            <span className="text-red-600">Username must be at least 3 characters</span>
                                        ) : formData.username.length > 30 ? (
                                            <span className="text-red-600">Username must be no more than 30 characters</span>
                                        ) : !/^[a-zA-Z0-9_-]+$/.test(formData.username) ? (
                                            <span className="text-red-600">Only letters, numbers, underscores, and hyphens allowed</span>
                                        ) : (
                                            <span className="text-green-600">✓ Username looks good</span>
                                        )}
                                    </div>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="password">Password</Label>
                                <div className="relative">
                                    <Input
                                        id="password"
                                        type={showPassword ? "text" : "password"}
                                        placeholder="Enter your password"
                                        value={formData.password}
                                        onChange={(e) => handleInputChange("password", e.target.value)}
                                        className="pr-10"
                                        required
                                    />
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="sm"
                                        className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                        onClick={() => setShowPassword(!showPassword)}
                                    >
                                        {showPassword ? (
                                            <EyeOff className="h-4 w-4 text-gray-400" />
                                        ) : (
                                            <Eye className="h-4 w-4 text-gray-400" />
                                        )}
                                    </Button>
                                </div>
                                {formData.password && (
                                    <div className="text-xs text-gray-500">
                                        {formData.password.length < 8 ? (
                                            <span className="text-red-600">Password must be at least 8 characters</span>
                                        ) : formData.password.length > 100 ? (
                                            <span className="text-red-600">Password must not exceed 100 characters</span>
                                        ) : !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/.test(formData.password) ? (
                                            <span className="text-red-600">Password must contain uppercase, lowercase, digit, and special character</span>
                                        ) : (
                                            <span className="text-green-600">✓ Password meets requirements</span>
                                        )}
                                    </div>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="confirmPassword">Confirm Password</Label>
                                <div className="relative">
                                    <Input
                                        id="confirmPassword"
                                        type={showConfirmPassword ? "text" : "password"}
                                        placeholder="Confirm your password"
                                        value={formData.confirmPassword}
                                        onChange={(e) => handleInputChange("confirmPassword", e.target.value)}
                                        className="pr-10"
                                        required
                                    />
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="sm"
                                        className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    >
                                        {showConfirmPassword ? (
                                            <EyeOff className="h-4 w-4 text-gray-400" />
                                        ) : (
                                            <Eye className="h-4 w-4 text-gray-400" />
                                        )}
                                    </Button>
                                </div>
                                {formData.confirmPassword && (
                                    <div className="text-xs text-gray-500">
                                        {formData.password !== formData.confirmPassword ? (
                                            <span className="text-red-600">Passwords do not match</span>
                                        ) : formData.confirmPassword.length >= 8 ? (
                                            <span className="text-green-600">✓ Passwords match</span>
                                        ) : null}
                                    </div>
                                )}
                            </div>
                        </CardContent>

                        <CardFooter className="flex flex-col space-y-4">
                            <Button type="submit" className="w-full" disabled={isLoading}>
                                {isLoading ? "Creating Account" : "Create Account"}
                            </Button>
                            <div className="text-center text-sm text-muted-foreground">
                                Already have an account?{" "}
                                <Link to="/login" className="text-primary hover:underline">
                                    Sign in
                                </Link>
                            </div>
                        </CardFooter>
                    </form>
                </Card>
            </div>
        </div>
    )
} 