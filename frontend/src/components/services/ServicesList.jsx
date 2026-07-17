import ServicesFeatureSection from "./ServicesFeatureSection.jsx";
import usePublishedServices from "../../hooks/services/usePublishedServices.js";

function ServicesList() {
    const {
        services,
        isLoading,
        error,
        reload,
    } = usePublishedServices();

    if (isLoading) {
        return (
            <section className="services-feature-list">
                <p className="services-status-message">
                    Loading services...
                </p>
            </section>
        );
    }

    if (error) {
        return (
            <section className="services-feature-list">
                <div className="services-status-message services-error-message">
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={reload}
                    >
                        Try again
                    </button>
                </div>
            </section>
        );
    }

    if (services.length === 0) {
        return (
            <section className="services-feature-list">
                <p className="services-status-message">
                    No services are currently available.
                </p>
            </section>
        );
    }

    return (
        <section className="services-feature-list">
            {services.map((service, index) => (
                <ServicesFeatureSection
                    key={service.id}
                    service={service}
                    index={index}
                />
            ))}
        </section>
    );
}

export default ServicesList;