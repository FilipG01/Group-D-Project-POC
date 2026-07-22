import { Link } from "react-router-dom";

import useAdminGallery from "../../hooks/gallery/useAdminGallery.js";
import { getImageUrl } from "../../utils/imageUrl.js";

import "../../styles/services/adminServices.css";
import "../../styles/gallery/adminGallery.css";

function AdminGallery() {
    const {
        images,
        isLoading,
        error,
        busyImageId,
        loadImages,
        toggleVisible,
        toggleArchived,
        moveImage,
    } = useAdminGallery();

    if (isLoading) {
        return (
            <main className="admin-services-page">
                <p className="admin-services-message">
                    Loading gallery images...
                </p>
            </main>
        );
    }

    return (
        <main className="admin-services-page">
            <div className="admin-services-header">
                <div>
                    <Link
                        to="/admin"
                        className="admin-back-link"
                    >
                        ← Back to Admin Dashboard
                    </Link>

                    <p className="section-label">
                        Admin dashboard
                    </p>

                    <h1>Manage Gallery</h1>

                    <p>
                        Add, edit, show, hide, archive and
                        reorder the images displayed on the
                        Home and About pages.
                    </p>
                </div>

                <Link
                    to="/admin/gallery/new"
                    className="admin-primary-button"
                >
                    Add Gallery Image
                </Link>
            </div>

            {error && (
                <div className="admin-services-message admin-services-error">
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={() => loadImages()}
                    >
                        Try again
                    </button>
                </div>
            )}

            {images.length === 0 ? (
                <p className="admin-services-message">
                    No gallery images were found.
                </p>
            ) : (
                <div className="admin-services-list">
                    {images.map((image, index) => {
                        const isBusy =
                            busyImageId === image.id;

                        const previewUrl =
                            getImageUrl(image.imageUrl);

                        return (
                            <article
                                key={image.id}
                                className={`admin-service-card ${
                                    image.archived
                                        ? "is-archived"
                                        : ""
                                }`}
                            >
                                <div className="admin-gallery-summary">
                                    <div className="admin-gallery-preview">
                                        {previewUrl ? (
                                            <img
                                                src={previewUrl}
                                                alt=""
                                            />
                                        ) : (
                                            <span>
                                                No image
                                            </span>
                                        )}
                                    </div>

                                    <div className="admin-gallery-details">
                                        <p className="admin-service-order">
                                            Order:{" "}
                                            {image.displayOrder}
                                        </p>

                                        <h2>
                                            {image.caption ||
                                                "Gallery image"}
                                        </h2>

                                        <p>
                                            <strong>
                                                Alt text:
                                            </strong>{" "}
                                            {image.altText}
                                        </p>
                                    </div>

                                    <div className="admin-service-statuses">
                                        <span
                                            className={
                                                image.visible
                                                    ? "status-badge is-published"
                                                    : "status-badge is-draft"
                                            }
                                        >
                                            {image.visible
                                                ? "Visible"
                                                : "Hidden"}
                                        </span>

                                        {image.archived && (
                                            <span className="status-badge is-archived">
                                                Archived
                                            </span>
                                        )}
                                    </div>
                                </div>

                                <div className="admin-service-actions">
                                    <Link
                                        to={`/admin/gallery/${image.id}/edit`}
                                        className="admin-secondary-button"
                                    >
                                        Edit
                                    </Link>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            toggleVisible(image)
                                        }
                                        disabled={
                                            isBusy ||
                                            image.archived
                                        }
                                    >
                                        {image.visible
                                            ? "Hide"
                                            : "Show"}
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-danger-button"
                                        onClick={() =>
                                            toggleArchived(image)
                                        }
                                        disabled={isBusy}
                                    >
                                        {image.archived
                                            ? "Restore"
                                            : "Archive"}
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            moveImage(index, "up")
                                        }
                                        disabled={
                                            isBusy ||
                                            index === 0
                                        }
                                    >
                                        Move Up
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            moveImage(index, "down")
                                        }
                                        disabled={
                                            isBusy ||
                                            index ===
                                            images.length - 1
                                        }
                                    >
                                        Move Down
                                    </button>
                                </div>
                            </article>
                        );
                    })}
                </div>
            )}
        </main>
    );
}

export default AdminGallery;