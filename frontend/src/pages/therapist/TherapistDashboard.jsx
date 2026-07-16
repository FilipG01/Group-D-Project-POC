import { Link } from "react-router-dom";
import "../../styles/therapists/therapistDashboard.css";

function TherapistDashboard() {
    return (
        <main className="therapist-dashboard-page">
            <header className="therapist-dashboard-header">
                <p className="section-label">Therapist dashboard</p>
                <h1>Manage Your Profile</h1>
                <p>
                    Update the professional details and public information
                    shown on the website.
                </p>
            </header>

            <section className="therapist-dashboard-grid">
                <article className="therapist-dashboard-card">
                    <div>
                        <p className="therapist-dashboard-card-label">
                            Profile
                        </p>

                        <h2>My Therapist Profile</h2>

                        <p>
                            Update your qualifications, biography, languages,
                            specialisms, profile image and availability.
                        </p>
                    </div>

                    <Link
                        to="/therapist/profile"
                        className="therapist-dashboard-link"
                    >
                        Edit My Profile →
                    </Link>
                </article>

                <article className="therapist-dashboard-card">
                    <div>
                        <p className="therapist-dashboard-card-label">
                            Content
                        </p>

                        <h2>My Blog Posts</h2>

                        <p>
                            Write articles, manage your drafts and
                            submit completed posts for admin review.
                        </p>
                    </div>

                    <Link
                        to="/therapist/blog"
                        className="therapist-dashboard-link"
                    >
                        Manage My Posts →
                    </Link>
                </article>
            </section>
        </main>
    );
}

export default TherapistDashboard;