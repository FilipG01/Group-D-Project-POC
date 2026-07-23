import { Routes, Route } from 'react-router-dom'

import Header from './components/shared/Header.jsx'
import Footer from './components/shared/Footer.jsx'

import Home from './pages/Home.jsx'
import About from './pages/About.jsx'
import Services from './pages/Services.jsx'
import Contact from './pages/Contact.jsx'
import ServiceDetail from "./pages/ServiceDetail";
import Blog from "./pages/Blog.jsx";
import BlogDetail from "./pages/BlogDetail.jsx";

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
import AdminTherapists from "./pages/admin/AdminTherapists.jsx";
import AdminTherapistSubmissions from "./pages/admin/AdminTherapistSubmissions.jsx";
import AdminTherapistSubmissionReview from "./pages/admin/AdminTherapistSubmissionReview.jsx";
import AdminCreateTherapist from "./pages/admin/AdminCreateTherapist.jsx";
import AdminEditTherapist from "./pages/admin/AdminEditTherapist.jsx";
import AdminBlogPosts from "./pages/admin/AdminBlogPosts.jsx";
import AdminBlogReview from "./pages/admin/AdminBlogReview.jsx";
import AdminBlogEditor from "./pages/admin/AdminBlogEditor.jsx";
import AdminBlogReorder from "./pages/admin/AdminBlogReorder.jsx";
import AdminGallery from "./pages/admin/AdminGallery.jsx";
import AdminGalleryCreate from "./pages/admin/AdminGalleryCreate.jsx";
import AdminGalleryEdit from "./pages/admin/AdminGalleryEdit.jsx";

import TherapistDashboard from "./pages/therapist/TherapistDashboard.jsx";
import TherapistProfileEdit from "./pages/therapist/TherapistProfileEdit.jsx";
import TherapistChat from "./pages/therapist/TherapistChat.jsx";
import TherapistBlogPosts from "./pages/therapist/TherapistBlogPosts.jsx";
import TherapistBlogEditor from "./pages/therapist/TherapistBlogEditor.jsx";

import NotFound from "./pages/errors/NotFound.jsx";

import ScrollToTop from "./components/shared/ScrollToTop.jsx";

import FloatingCallButton from "./components/shared/FloatingCallButton.jsx";

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
                <Route path="/blog" element={<Blog />} />
                <Route path="/blog/:slug" element={<BlogDetail />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/privacy" element={<Privacy />} />
                <Route path="/terms" element={<Terms />} />
                <Route path="/dashboard" element={
                        <ProtectedRoute allowedRoles={["CLIENT"]}>
                            <ClientDashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminServices />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services/new"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminServiceCreate />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/services/:serviceId/edit"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminServiceEdit />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminDashboard />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/therapists"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminTherapists />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/therapist-submissions"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminTherapistSubmissions />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/therapist-submissions/:submissionId"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminTherapistSubmissionReview />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/therapists/new"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminCreateTherapist />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/therapists/:userId/edit"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminEditTherapist />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/blog"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminBlogPosts />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin/blog/reorder"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminBlogReorder />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin/blog/:postId"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminBlogReview />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/blog/new"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminBlogEditor />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin/blog/:postId/edit"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminBlogEditor />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin/gallery"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminGallery />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin/gallery/new"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminGalleryCreate />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/admin/gallery/:imageId/edit"
                    element={
                        <ProtectedRoute allowedRoles={["ADMIN"]}>
                            <AdminGalleryEdit />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/therapist"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/therapist/profile"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistProfileEdit />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/therapist/chat"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistChat />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/therapist/blog"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistBlogPosts />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/therapist/blog/new"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistBlogEditor />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/therapist/blog/:postId/edit"
                    element={
                        <ProtectedRoute allowedRoles={["THERAPIST"]}>
                            <TherapistBlogEditor />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<NotFound />} />
            </Routes>


            <FloatingCallButton />

            <Footer />
        </>
    )
}

export default App
