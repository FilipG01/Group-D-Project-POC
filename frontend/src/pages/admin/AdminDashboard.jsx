import { Link } from "react-router-dom";
import "../../styles/adminDashboard.css";

function AdminDashboard() {
    return (
        <main className="admin-dashboard-page">
            <header className="admin-dashboard-header">
                <p className="section-label">Admin dashboard</p>
                <h1>Website Management</h1>
                <p>
                    Manage the public content displayed across the Root Therapy
                    website.
                </p>
            </header>

            <section
                className="admin-dashboard-grid"
                aria-label="Website management tools"
            >
                <article className="admin-dashboard-card">
                    <div>
                        <p className="admin-dashboard-card-label">
                            Public content
                        </p>

                        <h2>Services</h2>

                        <p>
                            Create, edit, publish, archive and reorder the
                            services displayed on the website.
                        </p>
                    </div>

                    <Link
                        to="/admin/services"
                        className="admin-dashboard-link"
                    >
                        Manage Services →
                    </Link>
                </article>

                <article className="admin-dashboard-card">
                    <div>
                        <p className="admin-dashboard-card-label">
                            Public content
                        </p>

                        <h2>Therapists</h2>

                        <p>
                            Create therapist accounts and manage the profiles
                            displayed on the About page.
                        </p>
                    </div>

                    <Link
                        to="/admin/therapists"
                        className="admin-dashboard-link"
                    >
                        Manage Therapists →
                    </Link>
                </article>

                <article className="admin-dashboard-card">
                    <div>
                        <p className="admin-dashboard-card-label">
                            Content
                        </p>

                        <h2>Blog Management</h2>

                        <p>
                            Review therapist submissions, publish posts
                            and manage featured blog content.
                        </p>
                    </div>

                    <Link
                        to="/admin/blog"
                        className="admin-dashboard-link"
                    >
                        Manage Blog →
                    </Link>
                </article>
            </section>
        </main>
    );
}

export default AdminDashboard;