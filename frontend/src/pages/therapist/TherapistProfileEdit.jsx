import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getOwnTherapistProfile,
    updateOwnTherapistProfile,
    uploadOwnTherapistImage,
} from "../../api/therapistsApi.js";

import TherapistForm from "../../components/admin/TherapistForm.jsx";

import "../../styles/adminTherapistForm.css";

function TherapistProfileEdit() {
    const [therapist, setTherapist] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
        let isCancelled = false;

        async function loadProfile() {
            try {
                const data = await getOwnTherapistProfile();

                if (!isCancelled) {
                    setTherapist(data);
                }
            } catch (requestError) {
                console.error(requestError);

                if (!isCancelled) {
                    setError(
                        requestError.message ||
                        "Your therapist profile could not be loaded."
                    );
                }
            } finally {
                if (!isCancelled) {
                    setIsLoading(false);
                }
            }
        }

        loadProfile();

        return () => {
            isCancelled = true;
        };
    }, []);

    async function handleUpdate(profileData) {
        setIsSubmitting(true);
        setError("");
        setSuccessMessage("");

        try {
            const updatedProfile =
                await updateOwnTherapistProfile(profileData);

            setTherapist(updatedProfile);
            setSuccessMessage("Your profile has been updated.");
        } catch (requestError) {
            console.error(requestError);
            setError(
                requestError.message ||
                "Your profile could not be updated."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isLoading) {
        return (
            <main className="admin-therapist-form-page">
                <p>Loading your profile...</p>
            </main>
        );
    }

    if (!therapist) {
        return (
            <main className="admin-therapist-form-page">
                <h1>Profile not found</h1>

                <Link to="/therapist">
                    Back to Therapist Dashboard
                </Link>
            </main>
        );
    }

    return (
        <main className="admin-therapist-form-page">
            <header className="admin-form-page-header">
                <Link
                    to="/therapist"
                    className="admin-back-link"
                >
                    ← Back to Therapist Dashboard
                </Link>

                <p className="section-label">
                    Therapist dashboard
                </p>

                <h1>Edit My Profile</h1>

                <p>
                    Changes to your professional profile will be saved
                    immediately. Public visibility and display order are
                    controlled by the site administrator.
                </p>
            </header>

            {error && (
                <p className="admin-form-error">
                    {error}
                </p>
            )}

            {successMessage && (
                <p className="therapist-profile-success">
                    {successMessage}
                </p>
            )}

            <TherapistForm
                key={therapist.userId}
                mode="edit"
                initialData={therapist}
                onSubmit={handleUpdate}
                onUploadImage={uploadOwnTherapistImage}
                submitLabel="Save My Profile"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default TherapistProfileEdit;