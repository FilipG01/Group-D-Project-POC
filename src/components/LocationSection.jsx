import '../../styles/locationSection.css'

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
                        Dublin, Ireland
                    </p>

                    <p>
                        <strong>Opening Hours</strong>
                        <br />
                        Monday - Friday: 9am - 6pm
                    </p>

                    <p>
                        <strong>Contact</strong>
                        <br />
                        test@roottherapy.com
                        <br />
                        +353 00 000 0000
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