import { Routes, Route } from 'react-router-dom'

import Header from './components/Header.jsx'
import Footer from './components/Footer.jsx'

import Home from './pages/Home.jsx'
import About from './pages/About.jsx'
import Services from './pages/Services.jsx'
import Contact from './pages/Contact.jsx'
import ServiceDetail from "./pages/ServiceDetail";

import Privacy from "./pages/Privacy.jsx";
import Terms from "./pages/Terms.jsx";

import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import ClientDashboard from "./pages/ClientDashboard.jsx";
import ProtectedRoute from "./auth/ProtectedRoute.jsx";

import AdminServices from "./pages/admin/AdminServices.jsx";
import AdminServiceCreate from "./pages/admin/AdminServiceCreate.jsx";
import AdminServiceEdit from "./pages/admin/AdminServiceEdit.jsx";
import AdminDashboard from "./pages/admin/AdminDashboard.jsx";

import ScrollToTop from "./components/shared/ScrollToTop";

import FloatingCallButton from "./components/shared/FloatingCallButton";

function App() {
    return (
        <>
            <ScrollToTop />
            <Header />

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/about" element={<About />} />
                <Route path="/services" element={<Services />} />
                <Route path="/contact" element={<Contact />} />
                <Route path="/services/:serviceSlug" element={<ServiceDetail />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/privacy" element={<Privacy />} />
                <Route path="/terms" element={<Terms />} />
                <Route path="/dashboard" element={
                        <ProtectedRoute>
                            <ClientDashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services"
                    element={
                        <ProtectedRoute>
                            <AdminServices />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services/new"
                    element={
                        <ProtectedRoute>
                            <AdminServiceCreate />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services/:serviceId/edit"
                    element={
                        <ProtectedRoute>
                            <AdminServiceEdit />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute>
                            <AdminDashboard />
                        </ProtectedRoute>
                    }
                />
            </Routes>


            <FloatingCallButton />

            <Footer />
        </>
    )
}

export default App