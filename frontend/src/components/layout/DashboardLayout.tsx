import { Outlet } from "react-router-dom"
import { DashboardSidebar } from "./DashboardSidebar"
import { UserProfile } from "./UserProfile"
import { JwtDebugInfo } from "../JwtDebugInfo"

export default function DashboardLayout() {
  return (
    <div className="flex h-screen flex-col md:flex-row">
      <DashboardSidebar />
      <div className="flex-1 flex flex-col min-h-0">
        <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
          <div className="flex h-14 items-center justify-end px-6">
            <UserProfile />
          </div>
        </header>
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
      
      {/* JWT Debug Info - Remove in production */}
      {import.meta.env.DEV && <JwtDebugInfo />}
    </div>
  )
}
