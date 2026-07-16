import {
    Link,
    Navigate,
    useParams,
} from "react-router-dom";

import ContactCTA from "../components/shared/ContactCTA.jsx";
import SEO from "../components/shared/SEO.jsx";
import { SITE_URL } from "../data/pageMetaData.js";
import usePublishedService from "../hooks/services/usePublishedService.js";
import { getImageUrl } from "../utils/imageUrl.js";

import "../styles/services/serviceDetail.css";

function ServiceDetail() {
    const { serviceSlug } = useParams();

    const {
        service,
        isLoading,
        notFound,
        error,
        reload,
    } = usePublishedService(serviceSlug);

    if (isLoading) {
        return (
            <main className="service-detail-page service-detail-status">
                <p>Loading service...</p>
            </main>
        );
    }

    if (notFound) {
        return <Navigate to="/404" replace />;
    }

    if (error || !service) {
        return (
            <main className="service-detail-page service-detail-status">
                <h1>Unable to load service</h1>

                <p>{error}</p>

                <button
                    type="button"
                    onClick={reload}
                >
                    Try again
                </button>

                <Link
                    to="/services"
                    className="services-cta-button"
                >
                    Return to services
                </Link>
            </main>
        );
    }

    const canonicalUrl =
        `${SITE_URL}/services/${service.slug}`;

    const shareImage = service.imageUrl
        ? service.imageUrl.startsWith("http")
            ? service.imageUrl
            : `${SITE_URL}${service.imageUrl}`
        : undefined;

    const displayImageUrl =
        getImageUrl(service.imageUrl);

    return (
        <>
            <SEO
                title={
                    service.metaTitle ||
                    service.title
                }
                description={
                    service.metaDescription ||
                    service.shortDescription
                }
                keywords={service.keywords}
                canonical={canonicalUrl}
                image={shareImage}
                type="article"
            />

            <main className="service-detail-page">
                <section
                    className="service-detail-hero"
                    style={{
                        backgroundImage: displayImageUrl
                            ? `url(${displayImageUrl})`
                            : "none",
                    }}
                >
                    <div
                        className="service-detail-overlay"
                        aria-hidden="true"
                    />

                    <div className="service-detail-hero-content">
                        {service.category && (
                            <p className="section-label">
                                {service.category}
                            </p>
                        )}

                        <h1>{service.title}</h1>

                        {service.shortDescription && (
                            <p>
                                {service.shortDescription}
                            </p>
                        )}
                    </div>
                </section>

                <section className="service-detail-content">
                    <div className="service-detail-text">
                        <h2>About this service</h2>

                        {service.fullDescription?.map(
                            (
                                paragraph,
                                paragraphIndex
                            ) => (
                                <p
                                    key={`${service.id}-paragraph-${paragraphIndex}`}
                                >
                                    {paragraph}
                                </p>
                            )
                        )}
                    </div>

                    <aside className="service-detail-card">
                        <h2>Therapy can help with</h2>

                        {service.points?.length > 0 && (
                            <ul>
                                {service.points.map(
                                    (point) => (
                                        <li key={point}>
                                            {point}
                                        </li>
                                    )
                                )}
                            </ul>
                        )}

                        <Link
                            to="/contact"
                            className="services-cta-button"
                        >
                            Contact Us →
                        </Link>
                    </aside>
                </section>

                <ContactCTA />
            </main>
        </>
    );
}

export default ServiceDetail;