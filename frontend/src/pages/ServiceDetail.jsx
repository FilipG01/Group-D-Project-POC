import { useParams, Link } from "react-router-dom";
import { services } from "../data/servicesData";
import ContactCTA from "../components/shared/ContactCTA";
import "../styles/serviceDetail.css";

function ServiceDetail() {
    const { serviceSlug } = useParams();

    const service = services.find(
        (item) => item.path === `/services/${serviceSlug}`
    );

    if (!service) {
        return (
            <main className="service-detail-page">
                <h1>Service not found</h1>
                <Link to="/services">Back to Services</Link>
            </main>
        );
    }

    return (
        <main className="service-detail-page">
            <section
                className="service-detail-hero"
                style={{ backgroundImage: `url(${service.image})` }}
            >
                <div className="service-detail-overlay"></div>

                <div className="service-detail-hero-content">
                    <p className="section-label">{service.category}</p>
                    <h1>{service.title}</h1>
                    <p>{service.shortDescription}</p>
                </div>
            </section>

            <section className="service-detail-content">
                <div className="service-detail-text">
                    <h2>About this service</h2>

                    {service.fullDescription?.map((paragraph) => (
                        <p key={paragraph}>{paragraph}</p>
                    ))}
                </div>

                <aside className="service-detail-card">
                    <h2>Therapy can help with</h2>

                    <ul>
                        {service.points.map((point) => (
                            <li key={point}>{point}</li>
                        ))}
                    </ul>

                    <Link to="/contact" className="services-cta-button">
                        Contact Us →
                    </Link>
                </aside>
            </section>

            <ContactCTA />
        </main>
    );
}

export default ServiceDetail;