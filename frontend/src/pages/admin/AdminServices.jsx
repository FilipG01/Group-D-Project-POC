import { Link } from "react-router-dom";

import { useEffect, useState } from "react";
import {
    getAdminServices,
    setServicePublished,
    setServiceArchived,
    reorderServices,
} from "../../api/servicesApi.js";
import "../../styles/adminServices.css";


function AdminServices() {
    const [services, setServices] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [busyServiceId, setBusyServiceId] = useState(null);

    async function loadServices({ showLoading = true } = {}) {
        if (showLoading) {
            setIsLoading(true);
        }

        setError("");

        try {
            const data = await getAdminServices();
            setServices(data);
        } catch (requestError) {
            console.error(requestError);
            setError(
                "The services could not be loaded. Make sure you are logged in as an admin."
            );
        } finally {
            if (showLoading) {
                setIsLoading(false);
            }
        }
    }

    useEffect(() => {
        let isCancelled = false;

        async function loadInitialServices() {
            try {
                const data = await getAdminServices();

                if (!isCancelled) {
                    setServices(data);
                }
            } catch (requestError) {
                console.error(requestError);

                if (!isCancelled) {
                    setError(
                        "The services could not be loaded. Make sure you are logged in as an admin."
                    );
                }
            } finally {
                if (!isCancelled) {
                    setIsLoading(false);
                }
            }
        }

        loadInitialServices();

        return () => {
            isCancelled = true;
        };
    }, []);

    async function handlePublishToggle(service) {
        setBusyServiceId(service.id);
        setError("");

        try {
            const updatedService = await setServicePublished(
                service.id,
                !service.published
            );

            setServices((currentServices) =>
                currentServices.map((currentService) =>
                    currentService.id === updatedService.id
                        ? updatedService
                        : currentService
                )
            );
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);
        } finally {
            setBusyServiceId(null);
        }
    }

    async function handleArchiveToggle(service) {
        const action = service.archived ? "restore" : "archive";

        const confirmed = window.confirm(
            `Are you sure you want to ${action} "${service.title}"?`
        );

        if (!confirmed) {
            return;
        }

        setBusyServiceId(service.id);
        setError("");

        try {
            const updatedService = await setServiceArchived(
                service.id,
                !service.archived
            );

            setServices((currentServices) =>
                currentServices.map((currentService) =>
                    currentService.id === updatedService.id
                        ? updatedService
                        : currentService
                )
            );
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);
        } finally {
            setBusyServiceId(null);
        }
    }

    async function handleMove(serviceIndex, direction) {
        const targetIndex =
            direction === "up"
                ? serviceIndex - 1
                : serviceIndex + 1;

        if (targetIndex < 0 || targetIndex >= services.length) {
            return;
        }

        const reordered = [...services];

        [reordered[serviceIndex], reordered[targetIndex]] = [
            reordered[targetIndex],
            reordered[serviceIndex],
        ];

        setBusyServiceId(services[serviceIndex].id);
        setError("");

        try {
            const updatedServices = await reorderServices(reordered);
            setServices(updatedServices);
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);

            await loadServices({ showLoading: false });
        } finally {
            setBusyServiceId(null);
        }
    }

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
                <p className="admin-services-message admin-services-error">
                    {error}
                </p>
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
                                            handlePublishToggle(service)
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
                                            handleArchiveToggle(service)
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
                                        onClick={() => handleMove(index, "up")}
                                        disabled={isBusy || index === 0}
                                    >
                                        Move Up
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() => handleMove(index, "down")}
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