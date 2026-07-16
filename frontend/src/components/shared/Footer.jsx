import { Link } from "react-router-dom";
import "../../styles/shared/footer.css";
import { FaFacebookF, FaInstagram, FaWhatsapp} from "react-icons/fa";

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
                    <h3>Socials</h3>
                    <a
                        href="https://www.facebook.com/profile.php?id=61591362725769"
                        target="_blank"
                        rel="noopener noreferrer"
                        aria-label="Facebook"
                    >
                        <FaFacebookF />
                    </a>

                    <a
                        href="https://www.instagram.com/marlenatherapy?igsh=MWxvdDN2aHIwaGxkaQ=="
                        target="_blank"
                        rel="noopener noreferrer"
                        aria-label="Instagram"
                    >
                        <FaInstagram />
                    </a>

                    <a
                        href="https://wa.me/3530833036099"
                        target="_blank"
                        rel="noopener noreferrer"
                        aria-label="WhatsApp"
                    >
                        <FaWhatsapp />
                    </a>

                </section>

                <section className="footer-column">
                    <h3>Practice</h3>
                    <Link to="/">Home</Link>
                    <Link to="/about">About</Link>
                    <Link to="/services">Services</Link>
                    <Link to="/contact">Contact</Link>
                </section>

                <section className="footer-column footer-contact">
                    <h3>Contact</h3>
                    <a
                        href="mailto:marlenatherapy@gmail.com"
                        className="footer-email-link"
                        aria-label="Email Root Therapy"
                    >
                        marlenatherapy@gmail.com
                    </a>
                    <a
                        href="tel:+353833036099"
                        className="footer-phone-link"
                        aria-label="Call Root Therapy"
                    >
                        +353 083 303 6099
                    </a>
                    <p>307 Swords Road, Whitehall, Dublin 9, D09 H9F5, Ireland
                        Santry Psychotherapy Consultancy Centre</p>
                    <p>Mon - Fri: 8am - 7pm</p>
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