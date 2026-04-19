"use client"

import { Link, useLocation } from "react-router-dom"
import { Search, Upload, FolderOpen, User, ArrowUp } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/utils/cn"

export function DashboardSidebar() {
  const location = useLocation()

  const navItems = [
    {
      name: "Property Search",
      href: "/dashboard",
      icon: Search,
    },
    {
      name: "Upload",
      href: "/dashboard/upload",
      icon: Upload,
    },
    {
      name: "Files",
      href: "/dashboard/files",
      icon: FolderOpen,
    },
    {
      name: "Profile",
      href: "/dashboard/profile",
      icon: User,
    },
  ]

  return (
    <div className="flex h-full w-64 flex-col border-r bg-muted/40">
      <div className="p-6">
        <h2 className="text-lg font-semibold">Dashboard</h2>
      </div>
      <nav className="flex-1 space-y-4 p-4">
        {navItems.map((item) => (
          <Link key={item.href} to={item.href}>
            <Button
              variant="ghost"
              className={cn(
                "w-full justify-start",
                (location.pathname === item.href ||
                  (item.href === "/dashboard" && location.pathname === "/dashboard")) &&
                "bg-muted",
              )}
            >
              <item.icon className="mr-2 h-4 w-4" />
              {item.name}
            </Button>
          </Link>
        ))}
      </nav>
      <div className="p-4 border-t">
        <Button 
          variant="outline" 
          className="w-full justify-start"
          onClick={() => {
            const mainContent = document.querySelector('main');
            if (mainContent) {
              mainContent.scrollTo({ top: 0, behavior: 'smooth' });
            }
          }}
        >
          <ArrowUp className="mr-2 h-4 w-4" />
          Back to Top
        </Button>
      </div>
    </div>
  )
}
