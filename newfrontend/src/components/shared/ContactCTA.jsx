import { Link } from "react-router-dom"

function ContactCTA() {
    return (
        <section className="services-contact-cta">
            <div>
                <p className="section-label">Not sure where to begin?</p>
                <h2>We can help you find the right support.</h2>
                <p>
                    If you are unsure which service best fits your needs, you can get in
                    touch and ask a question before booking.
                </p>
            </div>

            <Link to="/contact" className="services-cta-button">
                Contact Us →
            </Link>
        </section>
    )
}

export default ContactCTA