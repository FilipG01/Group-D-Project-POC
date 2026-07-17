import { getImageUrl } from "../../utils/imageUrl.js";

/*
 * Displays a single therapist profile on the
 * public About page.
 */
function TherapistCard({ therapist }) {
    const imageUrl = getImageUrl(
        therapist.profileImageUrl
    );

    const biography =
        therapist.publicBio || [];

    const languages =
        therapist.languages || [];

    const specialisms =
        therapist.specialisms || [];

    return (
        <article className="therapist-container">
            <div className="therapist-image">
                {imageUrl && (
                    <img
                        src={imageUrl}
                        alt={therapist.fullName}
                    />
                )}
            </div>

            <div className="therapist-text-card">
                <div className="therapist-content">
                    <div className="therapist-biography">
                        <h2>
                            {therapist.fullName}
                        </h2>

                        {biography.map(
                            (
                                paragraph,
                                paragraphIndex
                            ) => (
                                <p
                                    key={`${therapist.userId}-bio-${paragraphIndex}`}
                                >
                                    {paragraph}
                                </p>
                            )
                        )}
                    </div>

                    <aside className="therapist-sidebar">

                        <div className="therapist-detail-group">
                            <h3>
                                Qualifications
                            </h3>

                            <p>
                                {
                                    therapist.qualifications
                                }
                            </p>
                        </div>

                        <div className="therapist-detail-group">
                            <h3>
                                Experience
                            </h3>

                            <p>
                                {
                                    therapist.yearsExperience
                                }{" "}
                                years
                            </p>
                        </div>

                        {languages.length > 0 && (
                            <div className="therapist-detail-group">
                                <h3>
                                    Languages
                                </h3>

                                <ul>
                                    {languages.map(
                                        (
                                            language
                                        ) => (
                                            <li
                                                key={
                                                    language
                                                }
                                            >
                                                {
                                                    language
                                                }
                                            </li>
                                        )
                                    )}
                                </ul>
                            </div>
                        )}

                        {specialisms.length >
                            0 && (
                                <div className="therapist-detail-group">
                                    <h3>
                                        Specialisms
                                    </h3>

                                    <ul>
                                        {specialisms.map(
                                            (
                                                specialism
                                            ) => (
                                                <li
                                                    key={
                                                        specialism
                                                    }
                                                >
                                                    {
                                                        specialism
                                                    }
                                                </li>
                                            )
                                        )}
                                    </ul>
                                </div>
                            )}

                        <div className="therapist-detail-group">
                            <h3>
                                Availability
                            </h3>

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
    );
}

export default TherapistCard;