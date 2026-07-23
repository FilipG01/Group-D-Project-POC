import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import {
    getOwnTherapistProfileDraft,
    saveOwnTherapistProfileDraft,
    submitOwnTherapistProfileDraft,
    uploadOwnTherapistImage,
} from "../../api/therapistsApi.js";

import TherapistForm from "../../components/therapists/TherapistForm.jsx";
import "../../styles/adminTherapistForm.css";
import "../../styles/therapists/adminTherapistSubmissions.css";

function formatStatus(status) {
    return (status || "DRAFT").replaceAll("_", " ").toLowerCase()
        .replace(/^./, (letter) => letter.toUpperCase());
}

function TherapistProfileEdit() {
    const [draft, setDraft] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
        let cancelled = false;
        getOwnTherapistProfileDraft()
            .then((data) => !cancelled && setDraft(data))
            .catch((requestError) => !cancelled && setError(requestError.message))
            .finally(() => !cancelled && setIsLoading(false));
        return () => { cancelled = true; };
    }, []);

    async function handleSave(profileData) {
        setIsSubmitting(true);
        setError("");
        setSuccessMessage("");
        try {
            const saved = await saveOwnTherapistProfileDraft(profileData, draft.version);
            setDraft(saved);
            setSuccessMessage("Your private profile draft has been saved. The public profile has not changed.");
        } catch (requestError) {
            setError(requestError.message || "Your draft could not be saved.");
        } finally {
            setIsSubmitting(false);
        }
    }

    async function handleSubmitForReview() {
        setIsSubmitting(true);
        setError("");
        setSuccessMessage("");
        try {
            const submitted = await submitOwnTherapistProfileDraft(draft.version);
            setDraft(submitted);
            setSuccessMessage("Your profile changes have been submitted for administrator review.");
        } catch (requestError) {
            setError(requestError.message || "Your draft could not be submitted.");
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isLoading) return <main className="admin-therapist-form-page"><p>Loading your profile draft...</p></main>;
    if (!draft) return <main className="admin-therapist-form-page"><h1>Draft not found</h1><Link to="/therapist">Back to Therapist Dashboard</Link></main>;

    const canEdit = ["DRAFT", "CHANGES_REQUESTED", "REJECTED"].includes(draft.status);

    return (
        <main className="admin-therapist-form-page">
            <header className="admin-form-page-header">
                <Link to="/therapist" className="admin-back-link">← Back to Therapist Dashboard</Link>
                <p className="section-label">Therapist dashboard</p>
                <h1>Edit My Profile Draft</h1>
                <p>Your changes remain private until an administrator approves them. The current About page profile stays unchanged during review.</p>
                <p><strong>Status:</strong> {formatStatus(draft.status)}</p>
            </header>

            {draft.reviewNotes && (
                <section className="therapist-profile-review-note">
                    <strong>Administrator feedback</strong>
                    <p>{draft.reviewNotes}</p>
                </section>
            )}
            {error && <p className="admin-form-error">{error}</p>}
            {successMessage && <p className="therapist-profile-success">{successMessage}</p>}

            {canEdit ? (
                <>
                    <TherapistForm
                        key={`${draft.submissionId}-${draft.version}`}
                        mode="edit"
                        initialData={draft}
                        onSubmit={handleSave}
                        onUploadImage={uploadOwnTherapistImage}
                        submitLabel="Save Private Draft"
                        isSubmitting={isSubmitting}
                    />
                    <div className="therapist-profile-submit-row">
                        <p>Save any form changes first, then submit the latest saved draft.</p>
                        <button type="button" className="admin-primary-button" onClick={handleSubmitForReview} disabled={isSubmitting}>
                            {isSubmitting ? "Working..." : "Submit Draft for Review"}
                        </button>
                    </div>
                </>
            ) : (
                <section className="therapist-profile-pending-panel">
                    <h2>Awaiting administrator review</h2>
                    <p>This submitted draft is locked while it is being reviewed. You can edit it again if the administrator requests changes or rejects it.</p>
                </section>
            )}
        </main>
    );
}

export default TherapistProfileEdit;
