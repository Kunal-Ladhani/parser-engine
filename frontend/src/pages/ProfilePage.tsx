import { ProfileForm } from "@/components/dashboard/ProfileForm"

export default function ProfilePage() {
    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-bold">Profile</h1>
                <p className="text-muted-foreground">Manage your account information and preferences</p>
            </div>
            <ProfileForm />
        </div>
    )
} 