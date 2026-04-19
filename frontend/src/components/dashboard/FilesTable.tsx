import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Download, Trash2, RotateCcw, Play, Loader2 } from "lucide-react"
import type { FileItem } from "@/services/fileApiService"
import { fileApiService } from "@/services/fileApiService"

interface FilesTableProps {
  files: FileItem[]
  isLoading?: boolean
  onDelete: (fileId: string) => void
  onReprocess?: (fileId: string) => void
  onProcess?: (fileId: string) => void
  processingIds?: string[]
  userRole?: string
  onSort?: (field: string) => void
  getSortIcon?: (field: string) => React.ReactElement | null
}

export function FilesTable({ 
  files, 
  isLoading, 
  onDelete, 
  onReprocess,
  onProcess,
  processingIds = [],
  userRole = "USER",
  onSort,
  getSortIcon
}: FilesTableProps) {
  if (isLoading) {
    return (
      <Card className="w-full p-4">
        <div className="text-center text-muted-foreground">Loading</div>
      </Card>
    )
  }

  if (files.length === 0) {
    return (
      <Card className="w-full p-4">
        <div className="text-center text-muted-foreground">No files found</div>
      </Card>
    )
  }

  const getStatusBadge = (status: string) => {
    const statusConfig = {
      PENDING: { variant: "secondary" as const, text: "Pending" },
      PROCESSING: { variant: "default" as const, text: "Processing" },
      COMPLETED: { variant: "default" as const, text: "Completed" },
      FAILED: { variant: "destructive" as const, text: "Failed" },
    }

    const config = statusConfig[status as keyof typeof statusConfig] || { variant: "secondary" as const, text: status }
    
    return (
      <Badge variant={config.variant} className="text-xs">
        {config.text}
      </Badge>
    )
  }

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const isAdmin = userRole === "ADMIN"

  const handleSort = (field: string) => {
    if (onSort) {
      onSort(field)
    }
  }

  return (
    <div className="w-full rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead 
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('fileName')}
            >
              <div className="flex items-center gap-1">
                File Name
                {getSortIcon && getSortIcon('fileName')}
              </div>
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('fileSize')}
            >
              <div className="flex items-center gap-1">
                Size
                {getSortIcon && getSortIcon('fileSize')}
              </div>
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('fileProcessingStatus')}
            >
              <div className="flex items-center gap-1">
                Processing Status
                {getSortIcon && getSortIcon('fileProcessingStatus')}
              </div>
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('uploadedBy')}
            >
              <div className="flex items-center gap-1">
                Uploaded By
                {getSortIcon && getSortIcon('uploadedBy')}
              </div>
            </TableHead>
            <TableHead 
              className="cursor-pointer hover:bg-muted/50"
              onClick={() => handleSort('uploadedAt')}
            >
              <div className="flex items-center gap-1">
                Upload Date
                {getSortIcon && getSortIcon('uploadedAt')}
              </div>
            </TableHead>
            <TableHead className="text-right">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {files.map((file) => (
            <TableRow key={file.id}>
              <TableCell className="font-medium max-w-[200px] truncate" title={file.fileName}>
                {file.fileName}
              </TableCell>
              <TableCell className="text-sm text-muted-foreground">
                {formatFileSize(file.fileSize || file.size || 0)}
              </TableCell>
              <TableCell>{getStatusBadge(file.fileProcessingStatus)}</TableCell>
              <TableCell className="text-sm">
                <div className="flex flex-col">
                  <span className="font-medium">{file.uploadedBy}</span>
                </div>
              </TableCell>
              <TableCell className="text-sm text-muted-foreground">
                {formatDate(file.uploadedAt)}
              </TableCell>
              <TableCell className="text-right">
                <div className="flex justify-end gap-2">
                  {/* Download button: visible to all for all statuses */}
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={async () => {
                      try {
                        // Use fileName as fallback if id is not available
                        const identifier = file.id || file.fileName;
                        const blob = await fileApiService.downloadFile(identifier)
                        const url = window.URL.createObjectURL(blob)
                        const a = document.createElement('a')
                        a.href = url
                        a.download = file.fileName
                        document.body.appendChild(a)
                        a.click()
                        window.URL.revokeObjectURL(url)
                        document.body.removeChild(a)
                      } catch (error) {
                        console.error('Download failed:', error)
                        alert('Download failed. Please try again.')
                      }
                    }}
                    className="h-8 w-8 p-0"
                    title="Download"
                  >
                    <Download className="h-4 w-4" />
                  </Button>
                  {/* Process button: visible to all if PENDING */}
                  {file.fileProcessingStatus === 'PENDING' && onProcess && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onProcess(file.id || file.fileName)}
                      disabled={processingIds.includes(file.id || file.fileName)}
                      className="h-8 w-8 p-0 text-blue-600 hover:text-blue-700"
                      title="Process File"
                    >
                      {processingIds.includes(file.id || file.fileName) ? (
                        <Loader2 className="h-4 w-4 animate-spin" />
                      ) : (
                        <Play className="h-4 w-4" />
                      )}
                    </Button>
                  )}
                  {/* Reprocess button: visible to all if FAILED */}
                  {file.fileProcessingStatus === 'FAILED' && onReprocess && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onReprocess(file.id || file.fileName)}
                      className="h-8 w-8 p-0 text-orange-600 hover:text-orange-700"
                      title="Reprocess"
                    >
                      <RotateCcw className="h-4 w-4" />
                    </Button>
                  )}
                  {/* Delete button: only for ADMIN */}
                  {isAdmin && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => onDelete(file.id || file.fileName)}
                      className="h-8 w-8 p-0 text-destructive hover:text-destructive"
                      title="Delete (Admin only)"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
} 