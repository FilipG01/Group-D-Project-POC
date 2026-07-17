import { Link } from "react-router-dom";

import { getImageUrl } from "../../utils/imageUrl.js";

function ServicesFeatureSection({ service, index }) {
    const isReversed = index % 2 !== 0;
    const imageUrl = getImageUrl(service.imageUrl);
    const points = service.points || [];

    return (
        <article
            className={`service-feature ${
                isReversed
                    ? "service-feature-reverse"
                    : ""
            }`}
        >
            <div className="service-feature-image">
                {imageUrl ? (
                    <img
                        src={imageUrl}
                        alt={service.title}
                    />
                ) : (
                    <div
                        className="service-feature-image-placeholder"
                        aria-hidden="true"
                    >
                        Root Therapy
                    </div>
                )}
            </div>

            <div className="service-feature-content">
                {service.category && (
                    <p className="section-label">
                        {service.category}
                    </p>
                )}

                <h2>{service.title}</h2>

                {service.shortDescription && (
                    <p>{service.shortDescription}</p>
                )}

                {points.length > 0 && (
                    <ul>
                        {points.map((point, pointIndex) => (
                            <li
                                key={`${service.id}-point-${pointIndex}`}
                            >
                                {point}
                            </li>
                        ))}
                    </ul>
                )}

                <Link
                    to={`/services/${service.slug}`}
                    className="services-cta-button"
                >
                    Learn More →
                </Link>
            </div>
        </article>
    );
}

export default ServicesFeatureSection;