import { Link } from "react-router-dom";

import useAdminServices from "../../hooks/services/useAdminServices.js";

import "../../styles/services/adminServices.css";


function AdminServices() {

    const {
        services,
        isLoading,
        error,
        busyServiceId,
        loadServices,
        togglePublished,
        toggleArchived,
        moveService,
    } = useAdminServices();

    if (isLoading) {
        return (
            <main className="admin-services-page">
                <p className="admin-services-message">
                    Loading services...
                </p>
            </main>
        );
    }

    return (
        <main className="admin-services-page">
            <div className="admin-services-header">
                <div>
                    <Link to="/admin" className="admin-back-link">
                        ← Back to Admin Dashboard
                    </Link>

                    <p className="section-label">Admin dashboard</p>
                    <h1>Manage Services</h1>
                    <p>
                        View, publish, unpublish, archive and restore the
                        services shown on the public website.
                    </p>
                </div>

                <Link
                    to="/admin/services/new"
                    className="admin-primary-button"
                >
                    Add Service
                </Link>
            </div>

            {error && (
                <div className="admin-services-message admin-services-error">
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={() => loadServices()}
                    >
                        Try again
                    </button>
                </div>
            )}

            {services.length === 0 ? (
                <p className="admin-services-message">
                    No services were found.
                </p>
            ) : (
                <div className="admin-services-list">
                    {services.map((service, index) => {
                        const isBusy = busyServiceId === service.id;

                        return (
                            <article
                                key={service.id}
                                className={`admin-service-card ${
                                    service.archived
                                        ? "is-archived"
                                        : ""
                                }`}
                            >
                                <div className="admin-service-summary">
                                    <div>
                                        <p className="admin-service-order">
                                            Order: {service.displayOrder}
                                        </p>

                                        <h2>{service.title}</h2>

                                        <p>{service.shortDescription}</p>
                                    </div>

                                    <div className="admin-service-statuses">
                                        <span
                                            className={
                                                service.published
                                                    ? "status-badge is-published"
                                                    : "status-badge is-draft"
                                            }
                                        >
                                            {service.published
                                                ? "Published"
                                                : "Draft"}
                                        </span>

                                        {service.archived && (
                                            <span className="status-badge is-archived">
                                                Archived
                                            </span>
                                        )}
                                    </div>
                                </div>

                                <div className="admin-service-actions">
                                    <Link
                                        to={`/admin/services/${service.id}/edit`}
                                        className="admin-secondary-button"
                                    >
                                        Edit
                                    </Link>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            togglePublished(service)
                                        }
                                        disabled={
                                            isBusy || service.archived
                                        }
                                    >
                                        {service.published
                                            ? "Unpublish"
                                            : "Publish"}
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-danger-button"
                                        onClick={() =>
                                            toggleArchived(service)
                                        }
                                        disabled={isBusy}
                                    >
                                        {service.archived
                                            ? "Restore"
                                            : "Archive"}
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            moveService(index, "up")
                                        }
                                        disabled={isBusy || index === 0}
                                    >
                                        Move Up
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            moveService(index, "down")
                                        }
                                        disabled={
                                            isBusy ||
                                            index === services.length - 1
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

export default AdminServices;