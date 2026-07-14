import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import {
    getAdminTherapist,
    updateAdminTherapist,
    uploadTherapistImage,
} from "../../api/therapistsApi.js";

import TherapistForm from "../../components/admin/TherapistForm.jsx";

import "../../styles/adminTherapistForm.css";

function AdminEditTherapist() {

    const { userId } = useParams();

    const [therapist, setTherapist] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {

        let cancelled = false;

        async function loadTherapist() {

            try {

                const data =
                    await getAdminTherapist(userId);

                if (!cancelled) {
                    setTherapist(data);
                }

            } catch (err) {

                console.error(err);

                if (!cancelled) {
                    setError(
                        err.message ||
                        "Unable to load therapist."
                    );
                }

            } finally {

                if (!cancelled) {
                    setLoading(false);
                }

            }

        }

        loadTherapist();

        return () => {
            cancelled = true;
        };

    }, [userId]);

    async function handleUpdate(formData) {

        setSaving(true);
        setError("");

        try {

            const updated =
                await updateAdminTherapist(
                    userId,
                    formData
                );

            setTherapist(updated);

        } catch (err) {

            console.error(err);

            setError(
                err.message ||
                "Unable to save therapist."
            );

        } finally {

            setSaving(false);

        }

    }

    if (loading) {
        return (
            <main className="admin-therapist-form-page">
                <p>Loading therapist...</p>
            </main>
        );
    }

    if (!therapist) {
        return (
            <main className="admin-therapist-form-page">

                <h1>Therapist not found</h1>

                <Link to="/admin/therapists">
                    Back
                </Link>

            </main>
        );
    }

    return (

        <main className="admin-therapist-form-page">

            <header className="admin-form-page-header">

                <Link
                    className="admin-back-link"
                    to="/admin/therapists"
                >
                    ← Back to Therapists
                </Link>

                <p className="section-label">
                    Admin Dashboard
                </p>

                <h1>
                    Edit Therapist
                </h1>

                <p>
                    Update this therapist's public
                    profile and availability.
                </p>

            </header>

            {error && (
                <p className="admin-form-error">
                    {error}
                </p>
            )}

            <TherapistForm

                key={therapist.userId}

                mode="edit"

                isAdmin

                initialData={therapist}

                onSubmit={handleUpdate}

                onUploadImage={uploadTherapistImage}

                submitLabel="Save Changes"

                isSubmitting={saving}

            />

        </main>

    );

}

export default AdminEditTherapist;