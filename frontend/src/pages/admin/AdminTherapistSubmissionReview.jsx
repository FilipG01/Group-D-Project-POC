import { useEffect, useState } from "react";
import "../../styles/therapists/adminTherapists.css";
import { Link, useParams } from "react-router-dom";
import {
    approveTherapistProfileSubmission,
    getAdminTherapistProfileSubmission,
    rejectTherapistProfileSubmission,
    requestTherapistProfileChanges,
} from "../../api/therapistsApi.js";
import { getImageUrl } from "../../utils/imageUrl.js";
import "../../styles/therapists/adminTherapistSubmissions.css";

const fields = [
    ["qualifications", "Qualifications"], ["registrationNumber", "Registration number"],
    ["yearsExperience", "Years of experience"], ["bio", "Internal biography"],
    ["acceptingClients", "Accepting clients"], ["publicBio", "Public biography"],
    ["languages", "Languages"], ["specialisms", "Specialisms"],
];

function displayValue(value) {
    if (Array.isArray(value)) return value.length ? value.join(" • ") : "—";
    if (typeof value === "boolean") return value ? "Yes" : "No";
    return value === null || value === undefined || value === "" ? "—" : String(value);
}

function AdminTherapistSubmissionReview() {
    const { submissionId } = useParams();
    const [submission, setSubmission] = useState(null);
    const [note, setNote] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        getAdminTherapistProfileSubmission(submissionId)
            .then(setSubmission)
            .catch((requestError) => setError(requestError.message));
    }, [submissionId]);

    async function runAction(action) {
        if (["changes", "reject"].includes(action) && !note.trim()) {
            setError("Enter a review note before requesting changes or rejecting.");
            return;
        }
        setBusy(true); setError(""); setSuccess("");
        try {
            let updated;
            if (action === "approve") updated = await approveTherapistProfileSubmission(submissionId, submission.version, note);
            if (action === "changes") updated = await requestTherapistProfileChanges(submissionId, submission.version, note);
            if (action === "reject") updated = await rejectTherapistProfileSubmission(submissionId, submission.version, note);
            setSubmission(updated);
            setSuccess(action === "approve" ? "The proposed profile was approved and the live profile has been updated." : "The review decision was saved.");
        } catch (requestError) {
            setError(requestError.message);
        } finally { setBusy(false); }
    }

    if (!submission) return <main className="profile-review-page"><Link to="/admin/therapist-submissions">← Profile Submissions</Link><p>{error || "Loading submission..."}</p></main>;

    const current = submission.currentProfile;
    const proposed = submission.proposedProfile;
    const canReview = submission.status === "SUBMITTED";

    return (
        <main className="profile-review-page">
            <header className="profile-review-header">
                <Link to="/admin/therapist-submissions" className="admin-back-link">← Profile Submissions</Link>
                <p className="section-label">Admin review</p>
                <h1>{proposed.firstName} {proposed.lastName}</h1>
                <p>Compare the approved live profile with the therapist's proposed replacement.</p>
                <span className="status-badge is-draft">{submission.status.replaceAll("_", " ")}</span>
            </header>

            {error && <p className="admin-form-error">{error}</p>}
            {success && <p className="therapist-profile-success">{success}</p>}

            <section className="profile-image-comparison">
                <div><h2>Current image</h2>{current.profileImageUrl ? <img src={getImageUrl(current.profileImageUrl)} alt="Current therapist" /> : <p>No image</p>}</div>
                <div><h2>Proposed image</h2>{proposed.profileImageUrl ? <img src={getImageUrl(proposed.profileImageUrl)} alt="Proposed therapist" /> : <p>No image</p>}</div>
            </section>

            <section className="profile-comparison-table">
                <div className="comparison-heading">Field</div><div className="comparison-heading">Current approved profile</div><div className="comparison-heading">Proposed profile</div>
                {fields.map(([key, label]) => {
                    const changed = JSON.stringify(current[key]) !== JSON.stringify(proposed[key]);
                    return <div className="comparison-row" key={key}>
                        <strong>{label}</strong>
                        <div>{displayValue(current[key])}</div>
                        <div className={changed ? "proposed-value changed" : "proposed-value"}>{displayValue(proposed[key])}</div>
                    </div>;
                })}
            </section>

            <section className="profile-review-actions">
                <label><span>Review note</span><textarea rows="5" value={note} onChange={(event) => setNote(event.target.value)} disabled={!canReview || busy} /></label>
                {canReview ? <div className="profile-review-buttons">
                    <button className="admin-primary-button" type="button" disabled={busy} onClick={() => runAction("approve")}>Approve Changes</button>
                    <button className="admin-secondary-button" type="button" disabled={busy} onClick={() => runAction("changes")}>Request Changes</button>
                    <button className="admin-secondary-button danger" type="button" disabled={busy} onClick={() => runAction("reject")}>Reject</button>
                </div> : <p>This submission has already been reviewed and cannot be reviewed again.</p>}
            </section>
        </main>
    );
}

export default AdminTherapistSubmissionReview;
