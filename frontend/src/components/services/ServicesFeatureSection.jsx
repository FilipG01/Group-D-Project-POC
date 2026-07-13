import { Link } from "react-router-dom";

function ServicesFeatureSection({ service, index }) {
    const isReversed = index % 2 !== 0;

    return (
        <article
            className={`service-feature ${
                isReversed ? "service-feature-reverse" : ""
            }`}
        >
            <div className="service-feature-image">
                {service.imageUrl && (
                    <img
                        src={service.imageUrl}
                        alt={service.title}
                    />
                )}
            </div>

            <div className="service-feature-content">
                <p className="section-label">{service.category}</p>

                <h2>{service.title}</h2>

                <p>{service.shortDescription}</p>

                <ul>
                    {service.points?.map((point, index) => (
                        <li key={`${service.id}-point-${index}`}>
                            {point}
                        </li>
                    ))}
                </ul>

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