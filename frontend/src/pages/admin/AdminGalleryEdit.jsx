import {
    useEffect,
    useState,
} from "react";

import {
    Link,
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    getAdminGalleryImageById,
    updateGalleryImage,
} from "../../api/galleryApi.js";

import GalleryImageForm from "../../components/gallery/admin/GalleryImageForm.jsx";

import "../../styles/services/adminServiceForm.css";

function AdminGalleryEdit() {
    const { imageId } = useParams();
    const navigate = useNavigate();

    const [image, setImage] = useState(null);

    const [isLoading, setIsLoading] =
        useState(true);

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const [error, setError] = useState("");

    useEffect(() => {
        async function loadImage() {
            try {
                const data =
                    await getAdminGalleryImageById(
                        imageId
                    );

                setImage(data);
            } catch (requestError) {
                console.error(requestError);

                setError(
                    requestError.message ||
                    "The gallery image could not be loaded."
                );
            } finally {
                setIsLoading(false);
            }
        }

        loadImage();
    }, [imageId]);

    async function handleUpdate(imageData) {
        setIsSubmitting(true);
        setError("");

        try {
            await updateGalleryImage(
                imageId,
                imageData
            );

            navigate("/admin/gallery");
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The gallery image could not be updated."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isLoading) {
        return (
            <main className="admin-service-form-page">
                <p>Loading gallery image...</p>
            </main>
        );
    }

    if (!image) {
        return (
            <main className="admin-service-form-page">
                <h1>Gallery image not found</h1>

                <Link to="/admin/gallery">
                    Back to gallery
                </Link>
            </main>
        );
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

                <h1>Edit Gallery Image</h1>
            </div>

            {error && (
                <p className="admin-services-error">
                    {error}
                </p>
            )}

            <GalleryImageForm
                initialData={image}
                onSubmit={handleUpdate}
                submitLabel="Save Changes"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default AdminGalleryEdit;