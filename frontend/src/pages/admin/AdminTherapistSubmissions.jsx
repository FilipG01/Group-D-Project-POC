import { useEffect, useState } from "react";
import "../../styles/therapists/adminTherapists.css";
import { Link } from "react-router-dom";
import { getAdminTherapistProfileSubmissions } from "../../api/therapistsApi.js";
import "../../styles/therapists/adminTherapistSubmissions.css";

function AdminTherapistSubmissions() {
    const [status, setStatus] = useState("SUBMITTED");
    const [submissions, setSubmissions] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let cancelled = false;
        setIsLoading(true);
        setError("");
        getAdminTherapistProfileSubmissions(status)
            .then((data) => !cancelled && setSubmissions(data))
            .catch((requestError) => !cancelled && setError(requestError.message))
            .finally(() => !cancelled && setIsLoading(false));
        return () => { cancelled = true; };
    }, [status]);

    return (
        <main className="admin-therapists-page">
            <header className="admin-therapists-header">
                <div>
                    <Link to="/admin/therapists" className="admin-back-link">← Manage Therapists</Link>
                    <p className="section-label">Admin review</p>
                    <h1>Profile Submissions</h1>
                    <p>Review therapist-proposed changes before they replace the approved public profile.</p>
                </div>
                <select className="submission-status-filter" value={status} onChange={(event) => setStatus(event.target.value)}>
                    <option value="SUBMITTED">Pending review</option>
                    <option value="CHANGES_REQUESTED">Changes requested</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="APPROVED">Approved</option>
                    <option value="DRAFT">Drafts</option>
                </select>
            </header>

            {error && <p className="admin-therapists-message admin-therapists-error">{error}</p>}
            {isLoading ? <p className="admin-therapists-message">Loading submissions...</p> : submissions.length === 0 ? (
                <p className="admin-therapists-message">No profile submissions have this status.</p>
            ) : (
                <section className="admin-therapists-list">
                    {submissions.map((submission) => (
                        <article key={submission.submissionId} className="admin-therapist-card">
                            <div className="admin-therapist-info">
                                <p className="admin-therapist-order">{submission.status.replaceAll("_", " ")}</p>
                                <h2>{submission.proposedProfile.firstName} {submission.proposedProfile.lastName}</h2>
                                <p>{submission.proposedProfile.email}</p>
                                <p>Submitted: {submission.submittedAt ? new Date(submission.submittedAt).toLocaleString() : "Not submitted"}</p>
                            </div>
                            <div className="admin-therapist-actions">
                                <Link className="admin-primary-button" to={`/admin/therapist-submissions/${submission.submissionId}`}>Review Submission</Link>
                            </div>
                        </article>
                    ))}
                </section>
            )}
        </main>
    );
}

export default AdminTherapistSubmissions;
