import { Routes, Route, Navigate } from "react-router-dom"
import LoginPage from "./pages/LoginPage"
import SignupPage from "./pages/SignupPage"
import DashboardLayout from "./components/layout/DashboardLayout"
import SearchPage from "./pages/SearchPage"
import UploadPage from "./pages/UploadPage"
import FilesPage from "./pages/FilesPage"
import ProfilePage from "./pages/ProfilePage"
import PropertyDetailsPage from "./pages/PropertyDetailsPage"
import AddPropertyPage from "./pages/AddPropertyPage"
import { ThemeProvider } from "./components/ThemeProvider"
import { ProtectedRoute } from "./components/ProtectedRoute"
import { AuthRedirect } from "./components/AuthRedirect"

function App() {
    return (
        <ThemeProvider defaultTheme="light">
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route 
                    path="/login" 
                    element={
                        <AuthRedirect>
                            <LoginPage />
                        </AuthRedirect>
                    } 
                />
                <Route 
                    path="/signup" 
                    element={
                        <AuthRedirect>
                            <SignupPage />
                        </AuthRedirect>
                    } 
                />
                <Route 
                    path="/dashboard" 
                    element={
                        <ProtectedRoute>
                            <DashboardLayout />
                        </ProtectedRoute>
                    }
                >
                    <Route index element={<SearchPage />} />
                    <Route path="upload" element={<UploadPage />} />
                    <Route path="files" element={<FilesPage />} />
                    <Route path="profile" element={<ProfilePage />} />
                    <Route path="property/:id" element={<PropertyDetailsPage />} />
                    <Route path="add-property" element={<AddPropertyPage />} />
                </Route>
            </Routes>
        </ThemeProvider>
    )
}

export default App
