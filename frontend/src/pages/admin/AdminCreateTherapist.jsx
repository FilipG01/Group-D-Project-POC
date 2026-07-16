import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { createAdminTherapist } from "../../api/therapistsApi.js";
import TherapistForm from "../../components/therapists/TherapistForm.jsx";

import "../../styles/adminTherapistForm.css";

function AdminCreateTherapist() {
    const navigate = useNavigate();

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");

    async function handleCreate(therapistData) {
        setIsSubmitting(true);
        setError("");

        try {
            const createdTherapist =
                await createAdminTherapist(therapistData);

            navigate(
                `/admin/therapists/${createdTherapist.userId}/edit`
            );
        } catch (requestError) {
            console.error(requestError);
            setError(
                requestError.message ||
                "The therapist account could not be created."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className="admin-therapist-form-page">
            <header className="admin-form-page-header">
                <Link
                    to="/admin/therapists"
                    className="admin-back-link"
                >
                    ← Back to Manage Therapists
                </Link>

                <p className="section-label">Admin dashboard</p>
                <h1>Add Therapist</h1>

                <p>
                    Create a therapist login account. You can complete
                    their public profile immediately afterwards.
                </p>
            </header>

            {error && (
                <p className="admin-form-error">
                    {error}
                </p>
            )}

            <TherapistForm
                mode="create"
                isAdmin
                onSubmit={handleCreate}
                submitLabel="Create Therapist"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default AdminCreateTherapist;