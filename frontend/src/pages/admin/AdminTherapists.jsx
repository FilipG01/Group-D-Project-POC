import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getAdminTherapists,
    reorderTherapists,
} from "../../api/therapistsApi.js";

import { getImageUrl } from "../../utils/imageUrl.js";
import "../../styles/therapists/adminTherapists.css";

function AdminTherapists() {
    const [therapists, setTherapists] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [busyTherapistId, setBusyTherapistId] = useState(null);

    useEffect(() => {
        let isCancelled = false;

        async function loadTherapists() {
            try {
                const data = await getAdminTherapists();

                if (!isCancelled) {
                    setTherapists(data);
                }
            } catch (requestError) {
                console.error(requestError);

                if (!isCancelled) {
                    setError(
                        "Therapists could not be loaded. Make sure you are logged in as an admin."
                    );
                }
            } finally {
                if (!isCancelled) {
                    setIsLoading(false);
                }
            }
        }

        loadTherapists();

        return () => {
            isCancelled = true;
        };
    }, []);

    async function handleMove(index, direction) {
        const targetIndex =
            direction === "up" ? index - 1 : index + 1;

        if (
            targetIndex < 0 ||
            targetIndex >= therapists.length
        ) {
            return;
        }

        const reordered = [...therapists];

        [reordered[index], reordered[targetIndex]] = [
            reordered[targetIndex],
            reordered[index],
        ];

        setBusyTherapistId(therapists[index].userId);
        setError("");

        try {
            const updated = await reorderTherapists(reordered);
            setTherapists(updated);
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);
        } finally {
            setBusyTherapistId(null);
        }
    }

    if (isLoading) {
        return (
            <main className="admin-therapists-page">
                <p className="admin-therapists-message">
                    Loading therapists...
                </p>
            </main>
        );
    }

    return (
        <main className="admin-therapists-page">
            <header className="admin-therapists-header">
                <div>
                    <Link to="/admin" className="admin-back-link">
                        ← Back to Admin Dashboard
                    </Link>

                    <p className="section-label">
                        Admin dashboard
                    </p>

                    <h1>Manage Therapists</h1>

                    <p>
                        Create therapist accounts, edit public profiles,
                        control visibility and arrange the order shown on
                        the About page.
                    </p>
                </div>

                <div className="admin-therapist-header-actions">
                    <Link to="/admin/therapist-submissions" className="admin-secondary-button">
                        Review Profile Submissions
                    </Link>
                    <Link to="/admin/therapists/new" className="admin-primary-button">
                        Add Therapist
                    </Link>
                </div>
            </header>

            {error && (
                <p className="admin-therapists-message admin-therapists-error">
                    {error}
                </p>
            )}

            {therapists.length === 0 ? (
                <p className="admin-therapists-message">
                    No therapist accounts were found.
                </p>
            ) : (
                <section className="admin-therapists-list">
                    {therapists.map((therapist, index) => {
                        const isBusy =
                            busyTherapistId === therapist.userId;

                        return (
                            <article
                                key={therapist.userId}
                                className="admin-therapist-card"
                            >
                                <div className="admin-therapist-image">
                                    {therapist.profileImageUrl ? (
                                        <img
                                            src={getImageUrl(
                                                therapist.profileImageUrl
                                            )}
                                            alt={`${therapist.firstName} ${therapist.lastName}`}
                                        />
                                    ) : (
                                        <div className="admin-therapist-placeholder">
                                            No image
                                        </div>
                                    )}
                                </div>

                                <div className="admin-therapist-info">
                                    <p className="admin-therapist-order">
                                        Order: {therapist.displayOrder}
                                    </p>

                                    <h2>
                                        {therapist.firstName}{" "}
                                        {therapist.lastName}
                                    </h2>

                                    <p>{therapist.email}</p>

                                    <p>{therapist.qualifications}</p>

                                    <div className="admin-therapist-statuses">
                                        <span
                                            className={
                                                therapist.publiclyVisible
                                                    ? "status-badge is-published"
                                                    : "status-badge is-draft"
                                            }
                                        >
                                            {therapist.publiclyVisible
                                                ? "Public"
                                                : "Hidden"}
                                        </span>

                                        <span
                                            className={
                                                therapist.acceptingClients
                                                    ? "status-badge is-published"
                                                    : "status-badge is-draft"
                                            }
                                        >
                                            {therapist.acceptingClients
                                                ? "Accepting clients"
                                                : "Not accepting clients"}
                                        </span>
                                    </div>
                                </div>

                                <div className="admin-therapist-actions">
                                    <Link
                                        to={`/admin/therapists/${therapist.userId}/edit`}
                                        className="admin-secondary-button"
                                    >
                                        Edit
                                    </Link>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            handleMove(index, "up")
                                        }
                                        disabled={isBusy || index === 0}
                                    >
                                        Move Up
                                    </button>

                                    <button
                                        type="button"
                                        className="admin-secondary-button"
                                        onClick={() =>
                                            handleMove(index, "down")
                                        }
                                        disabled={
                                            isBusy ||
                                            index ===
                                            therapists.length - 1
                                        }
                                    >
                                        Move Down
                                    </button>
                                </div>
                            </article>
                        );
                    })}
                </section>
            )}
        </main>
    );
}

export default AdminTherapists;