import { Link } from "react-router-dom";
import "./Footer.css";

function Footer() {
    return (
        <footer className="footer-main">
            <div className="footer-inner">
                <section className="footer-brand">
                    <Link to="/" className="footer-logo-container" aria-label="Root Therapy home">
                        <span className="footer-logo" aria-hidden="true"></span>
                        <h2 className="footer-title">Root Therapy</h2>
                    </Link>

                    <p>
                        Gentle, confidential support for anxiety, stress, relationships,
                        and emotional well-being.
                    </p>

                    <Link to="/contact" className="footer-booking-button">
                        Book a Session →
                    </Link>
                </section>

                <section className="footer-column">
                    <h3>Services</h3>
                    <Link to="/services">Addiction Recovery</Link>
                    <Link to="/services">Relationship Counseling</Link>
                    <Link to="/services">Stress Management</Link>
                    <Link to="/services">Bereavement</Link>
                    <Link to="/services">Depression</Link>
                </section>

                <section className="footer-column">
                    <h3>Practice</h3>
                    <Link to="/">Home</Link>
                    <Link to="/about">About</Link>
                    <Link to="/services">Fees</Link>
                    <Link to="/blog">Blog</Link>
                    <Link to="/contact">Contact</Link>
                </section>

                <section className="footer-column footer-contact">
                    <h3>Contact</h3>
                    <p>test@roottherapy.com</p>
                    <p>+353 00 000 0000</p>
                    <p>Dublin, Ireland</p>
                    <p>Mon - Fri: 9am - 6pm</p>
                </section>
            </div>

            <div className="footer-bottom">
                <p>© 2026 Root Therapy. All rights reserved.</p>

                <div>
                    <Link to="/privacy">Privacy Policy</Link>
                    <span>|</span>
                    <Link to="/terms">Terms & Conditions</Link>
                </div>
            </div>
        </footer>
    );
}

export default Footer;