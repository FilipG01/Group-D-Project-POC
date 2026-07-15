import { Link } from "react-router-dom";
import "../../styles/therapistDashboard.css";

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

                <article className="therapist-dashboard-card is-disabled">
                    <div>
                        <p className="therapist-dashboard-card-label">
                            Planned feature
                        </p>

                        <h2>Blog Posts</h2>

                        <p>
                            Write and submit articles for admin review.
                        </p>
                    </div>

                    <span className="therapist-dashboard-disabled-link">
                        Coming soon
                    </span>
                </article>
            </section>
        </main>
    );
}

export default TherapistDashboard;