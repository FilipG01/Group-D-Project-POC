import { Link } from "react-router-dom";
import "../../styles/errorPage.css";

function NotFound() {
    return (
        <main className="error-page">
            <section className="error-card" aria-labelledby="not-found-title">
                <p className="error-code">404</p>

                <h1 id="not-found-title">
                    Looks like you&apos;ve wandered off the path.
                </h1>

                <p className="error-message">
                    The page you&apos;re looking for does not exist or may
                    have been moved.
                </p>

                <div className="error-actions">
                    <Link to="/" className="error-primary-button">
                        Return Home
                    </Link>

                    <Link to="/services" className="error-secondary-button">
                        View Services
                    </Link>
                </div>
            </section>
        </main>
    );
}

export default NotFound;