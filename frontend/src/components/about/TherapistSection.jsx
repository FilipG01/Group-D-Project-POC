import { useEffect, useState } from "react";
import { getPublicTherapists } from "../../api/therapistsApi.js";
import { getImageUrl } from "../../utils/imageUrl.js";

function TherapistSection() {
    const [therapists, setTherapists] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let isCancelled = false;

        async function loadTherapists() {
            try {
                const data = await getPublicTherapists();

                if (!isCancelled) {
                    setTherapists(data);
                }
            } catch (requestError) {
                console.error(requestError);

                if (!isCancelled) {
                    setError(
                        "Therapist profiles could not be loaded."
                    );
                }
            } finally {
                if (!isCancelled) {
                    setIsLoading(false);
                }
            }
        }

        loadTherapists();

        return () => {
            isCancelled = true;
        };
    }, []);

    if (isLoading) {
        return (
            <section className="therapist-section">
                <p>Loading therapist profiles...</p>
            </section>
        );
    }

    if (error) {
        return (
            <section className="therapist-section">
                <p>{error}</p>
            </section>
        );
    }

    if (therapists.length === 0) {
        return null;
    }

    return (
        <section className="therapist-section">
            {therapists.map((therapist) => (
                <article
                    key={therapist.userId}
                    className="therapist-container"
                >
                    <div className="therapist-image">
                        {therapist.profileImageUrl && (
                            <img
                                src={getImageUrl(
                                    therapist.profileImageUrl
                                )}
                                alt={therapist.fullName}
                            />
                        )}
                    </div>

                    <div className="therapist-text-card">
                        <div className="therapist-content">
                            <div className="therapist-biography">
                                <h2>{therapist.fullName}</h2>

                                {therapist.publicBio?.map(
                                    (paragraph, index) => (
                                        <p
                                            key={`${therapist.userId}-bio-${index}`}
                                        >
                                            {paragraph}
                                        </p>
                                    )
                                )}
                            </div>

                            <aside className="therapist-sidebar">
                                <div className="therapist-detail-group">
                                    <h3>Qualifications</h3>
                                    <p>{therapist.qualifications}</p>
                                </div>

                                <div className="therapist-detail-group">
                                    <h3>Experience</h3>
                                    <p>
                                        {therapist.yearsExperience} years
                                    </p>
                                </div>

                                {therapist.languages?.length > 0 && (
                                    <div className="therapist-detail-group">
                                        <h3>Languages</h3>
                                        <ul>
                                            {therapist.languages.map(
                                                (language) => (
                                                    <li key={language}>
                                                        {language}
                                                    </li>
                                                )
                                            )}
                                        </ul>
                                    </div>
                                )}

                                {therapist.specialisms?.length > 0 && (
                                    <div className="therapist-detail-group">
                                        <h3>Specialisms</h3>
                                        <ul>
                                            {therapist.specialisms.map(
                                                (specialism) => (
                                                    <li key={specialism}>
                                                        {specialism}
                                                    </li>
                                                )
                                            )}
                                        </ul>
                                    </div>
                                )}

                                <div className="therapist-detail-group">
                                    <h3>Availability</h3>
                                    <p>
                                        {therapist.acceptingClients
                                            ? "Currently accepting clients"
                                            : "Not currently accepting new clients"}
                                    </p>
                                </div>
                            </aside>
                        </div>
                    </div>
                </article>
            ))}
        </section>
    );
}

export default TherapistSection;