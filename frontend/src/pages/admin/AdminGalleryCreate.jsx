import { useState } from "react";
import {
    Link,
    useNavigate,
} from "react-router-dom";

import { createGalleryImage } from "../../api/galleryApi.js";
import GalleryImageForm from "../../components/gallery/admin/GalleryImageForm.jsx";

import "../../styles/services/adminServiceForm.css";

function AdminGalleryCreate() {
    const navigate = useNavigate();

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const [error, setError] = useState("");

    async function handleCreate(imageData) {
        setIsSubmitting(true);
        setError("");

        try {
            await createGalleryImage(imageData);
            navigate("/admin/gallery");
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The gallery image could not be created."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className="admin-service-form-page">
            <div className="admin-form-page-header">
                <Link
                    to="/admin/gallery"
                    className="admin-back-link"
                >
                    ← Back to Gallery
                </Link>

                <p className="section-label">
                    Admin dashboard
                </p>

                <h1>Add Gallery Image</h1>
            </div>

            {error && (
                <p className="admin-services-error">
                    {error}
                </p>
            )}

            <GalleryImageForm
                onSubmit={handleCreate}
                submitLabel="Create Gallery Image"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default AdminGalleryCreate;