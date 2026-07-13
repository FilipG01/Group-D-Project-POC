import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import { getServiceBySlug } from "../api/servicesApi.js";
import ContactCTA from "../components/shared/ContactCTA.jsx";
import SEO from "../components/shared/SEO.jsx";
import { SITE_URL } from "../data/pageMetaData.js";

import "../styles/serviceDetail.css";

function ServiceDetail() {
    const { serviceSlug } = useParams();

    const [service, setService] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadService() {
            setIsLoading(true);
            setError("");

            try {
                const data = await getServiceBySlug(serviceSlug);
                setService(data);
            } catch (requestError) {
                console.error(requestError);
                setError("Service not found.");
            } finally {
                setIsLoading(false);
            }
        }

        loadService();
    }, [serviceSlug]);

    if (isLoading) {
        return (
            <main className="service-detail-page service-detail-status">
                <p>Loading service...</p>
            </main>
        );
    }

    if (error || !service) {
        return (
            <main className="service-detail-page service-detail-status">
                <h1>Service not found</h1>

                <Link to="/services">
                    Back to Services
                </Link>
            </main>
        );
    }

    const canonicalUrl = `${SITE_URL}/services/${service.slug}`;

    const shareImage = service.imageUrl?.startsWith("http")
        ? service.imageUrl
        : `${SITE_URL}${service.imageUrl || ""}`;

    return (
        <>
            <SEO
                title={service.metaTitle || service.title}
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
                        backgroundImage: service.imageUrl
                            ? `url(${service.imageUrl})`
                            : "none",
                    }}
                >
                    <div className="service-detail-overlay"></div>

                    <div className="service-detail-hero-content">
                        <p className="section-label">
                            {service.category}
                        </p>

                        <h1>{service.title}</h1>

                        <p>{service.shortDescription}</p>
                    </div>
                </section>

                <section className="service-detail-content">
                    <div className="service-detail-text">
                        <h2>About this service</h2>

                        {service.fullDescription?.map(
                            (paragraph, index) => (
                                <p key={`${service.id}-paragraph-${index}`}>
                                    {paragraph}
                                </p>
                            )
                        )}
                    </div>

                    <aside className="service-detail-card">
                        <h2>Therapy can help with</h2>

                        <ul>
                            {service.points?.map((point) => (
                                <li key={point}>{point}</li>
                            ))}
                        </ul>

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