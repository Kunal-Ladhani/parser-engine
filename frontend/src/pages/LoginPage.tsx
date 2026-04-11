"use client"

import type React from "react"

import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Eye, EyeOff } from "lucide-react"
import { AuthApiService, type LoginRequest } from "@/services/authApiService"

export default function LoginPage() {
    const navigate = useNavigate()
    const [emailOrUsername, setEmailOrUsername] = useState("")
    const [password, setPassword] = useState("")
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const validateForm = (): boolean => {
        if (!emailOrUsername.trim()) {
            setError("Email or username is required")
            return false
        }
        if (!password.trim()) {
            setError("Password is required")
            return false
        }
        if (password.length < 8) {
            setError("Password must be at least 8 characters long")
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
            // Determine if input is email or username
            const isEmail = emailOrUsername.includes('@')
            const loginData: LoginRequest = {
                password,
                ...(isEmail ? { email: emailOrUsername } : { username: emailOrUsername })
            };
            
            const response = await AuthApiService.login(loginData);

            // Navigate to dashboard after successful login
            navigate("/dashboard")
        } catch (err) {
            // Show the actual backend error message
            const errorMessage = err instanceof Error ? err.message : "Login failed. Please check your credentials and try again."
            setError(errorMessage)
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="flex h-screen w-full items-center justify-center bg-gray-50">
            <div className="w-full max-w-md space-y-8 p-8">
                <div className="text-center">
                    <h1 className="text-4xl font-bold text-gray-900">Welcome</h1>
                    <p className="text-sm text-muted-foreground mt-2">Sign in to your account</p>
                </div>

                <div className="bg-white shadow-md rounded-lg p-6">
                    <form className="space-y-6" onSubmit={handleSubmit}>
                        {error && (
                            <div className="rounded-md bg-red-50 border border-red-200 p-3">
                                <div className="text-sm text-red-700 font-medium">{error}</div>
                            </div>
                        )}

                        <div>
                            <Label htmlFor="emailOrUsername" className="block text-sm font-medium text-gray-700">
                                Email or Username
                            </Label>
                            <div className="mt-1">
                                <Input
                                    id="emailOrUsername"
                                    name="emailOrUsername"
                                    type="text"
                                    autoComplete="username"
                                    required
                                    placeholder="Enter your email or username"
                                    value={emailOrUsername}
                                    onChange={(e) => setEmailOrUsername(e.target.value)}
                                    className="w-full"
                                />
                            </div>
                        </div>

                        <div>
                            <Label htmlFor="password" className="block text-sm font-medium text-gray-700">
                                Password
                            </Label>
                            <div className="mt-1 relative">
                                <Input
                                    id="password"
                                    name="password"
                                    type={showPassword ? "text" : "password"}
                                    autoComplete="current-password"
                                    required
                                    placeholder="Enter your password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pr-10"
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
                        </div>

                        <div>
                            <Button
                                type="submit"
                                disabled={isLoading}
                                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {isLoading ? "Signing in" : "Sign in"}
                            </Button>
                        </div>

                        <div className="text-center">
                            <span className="text-sm text-gray-600">
                                Don't have an account?{" "}
                                <Link
                                    to="/signup"
                                    className="font-medium text-primary hover:text-primary/90 hover:underline"
                                >
                                    Sign up
                                </Link>
                            </span>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}
