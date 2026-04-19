import { UploadForm } from "@/components/dashboard/UploadForm"

export default function UploadPage() {
    return (
        <div className="w-full max-w-4xl mx-auto px-4">
            <div className="pt-12 pb-6 text-center">
                <h1 className="text-3xl font-bold mb-1">Upload</h1>
                <p className="text-muted-foreground mb-6">Upload your files to the system</p>
            </div>
            <div className="flex justify-center">
                <UploadForm />
            </div>
        </div>
    )
}
