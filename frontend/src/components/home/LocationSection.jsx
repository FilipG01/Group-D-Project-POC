import '../../styles/home/locationSection.css'

function LocationSection() {
    return (
        <section className="location-section">
            <div className="location-content">
                <p className="location-label">Find Us</p>

                <h2>Visit the clinic</h2>

                <p>
                    Root Therapy offers a calm and confidential space for support,
                    reflection, and growth.
                </p>

                <div className="location-details">
                    <p>
                        <strong>Address</strong>
                        <br />
                        307 Swords Road, Whitehall, Dublin 9, D09 H9F5, Ireland
                        Santry Psychotherapy Consultancy Centre
                    </p>

                    <p>
                        <strong>Opening Hours</strong>
                        <br />
                        Monday - Friday: 8am - 7pm
                    </p>

                    <p>
                        <strong>Contact</strong>
                        <br />
                        <a
                            href="mailto:marlenatherapy@gmail.com"
                            className="location-email-link"
                            aria-label="Email Root Therapy"
                        >
                            marlenatherapy@gmail.com
                        </a>
                        <br />
                        <a
                            href="tel:+353833036099"
                            className="location-phone-link"
                            aria-label="Call Root Therapy"
                        >
                            +353 083 303 6099
                        </a>
                    </p>
                </div>
            </div>

            <div className="location-map">
                <iframe
                    title="Root Therapy location map"
                    src="https://www.google.com/maps/embed/v1/place?key=AIzaSyB2NIWI3Tv9iDPrlnowr_0ZqZWoAQydKJU&q=Marlena%20Nowak%20Gnitecka&maptype=roadmap"
                    loading="lazy"
                    referrerPolicy="no-referrer-when-downgrade"
                ></iframe>
            </div>
        </section>
    )
}

export default LocationSection