"use client"

import type React from "react"

import { useEffect, useState, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Upload, AlertCircle, CheckCircle } from "lucide-react"
import api from "@/services/api"
import { FILE_UPLOAD_ENDPOINT } from "@/config/default"

export function UploadForm() {
  const [file, setFile] = useState<File | null>(null)
  const [isUploading, setIsUploading] = useState(false)
  const [success, setSuccess] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (success || error) {
      const timer = setTimeout(() => {
        setSuccess(null)
        setError(null)
      }, 2500)
      return () => clearTimeout(timer)
    }
  }, [success, error])

  const clearForm = () => {
    setFile(null)
    setError(null)
    setSuccess(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0])
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsUploading(true)
    setSuccess(null)
    setError(null)

    if (!file) {
      setError("Please select a file to upload.")
      setIsUploading(false)
      return
    }

    try {
      const response = await api.uploadFile(FILE_UPLOAD_ENDPOINT, file)
      if (response.data) {
        setSuccess(`File uploaded successfully!`)
        setFile(null)
        // Reset the file input element
        if (fileInputRef.current) {
          fileInputRef.current.value = ''
        }
      } else {
        // Try to show backend error message if available
        setError(response.error || "Failed to upload file.")
      }
    } catch (err) {
      console.error('Upload error:', err)
      setError("An unexpected error occurred during upload. Please try again.")
    } finally {
      setIsUploading(false)
    }
  }

  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader />
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
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
          <div>
            <Input
              ref={fileInputRef}
              id="file"
              type="file"
              onChange={handleFileChange}
              required
              className="border border-input rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
            />
            {file && (
              <div className="mt-2 rounded-md border border-muted bg-muted/30 px-3 py-2 text-sm text-muted-foreground flex items-center gap-2">
                <span className="truncate">{file.name}</span>
                <span className="text-xs text-gray-400">({(file.size / 1024).toFixed(2)} KB)</span>
              </div>
            )}
          </div>
        </CardContent>
        <CardFooter className="flex gap-2">
          <Button 
            type="button" 
            variant="outline" 
            onClick={clearForm}
            disabled={isUploading}
            className="flex-1"
          >
            Clear
          </Button>
          <Button type="submit" className="flex-1" disabled={isUploading || !file}>
            {isUploading ? "Uploading..." : (
              <>
                <Upload className="mr-2 h-4 w-4" />
                Upload File
              </>
            )}
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
}
