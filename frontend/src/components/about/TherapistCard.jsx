function TherapistCard({ therapist }) {
    return (
        <article className="therapist-container">

            <div className="therapist-image">
                <img
                    src={therapist.image}
                    alt={therapist.imageAlt}
                />
            </div>

            <div className="therapist-content">

                <div className="therapist-biography">

                    <h2>{therapist.name}</h2>

                    {therapist.biography.map((paragraph, index) => (
                        <p key={`${therapist.id}-${index}`}>
                            {paragraph}
                        </p>
                    ))}

                </div>

                <aside className="therapist-sidebar">

                    <div className="therapist-detail-group">
                        <h3>Qualifications</h3>

                        <ul>
                            {therapist.qualifications.map((qualification) => (
                                <li key={qualification}>{qualification}</li>
                            ))}
                        </ul>
                    </div>

                    <div className="therapist-detail-group">
                        <h3>Languages</h3>

                        <ul>
                            {therapist.languages.map((language) => (
                                <li key={language}>{language}</li>
                            ))}
                        </ul>
                    </div>

                    <div className="therapist-detail-group">
                        <h3>Areas of Support</h3>

                        <ul>
                            {therapist.specialisms.map((specialism) => (
                                <li key={specialism}>{specialism}</li>
                            ))}
                        </ul>
                    </div>

                </aside>

            </div>

        </article>
    );
}

export default TherapistCard;