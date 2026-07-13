import { useEffect, useState } from "react";
import { getPublishedServices } from "../../api/servicesApi.js";
import ServicesFeatureSection from "./ServicesFeatureSection.jsx";

function ServicesList() {
    const [services, setServices] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadServices() {
            try {
                const data = await getPublishedServices();
                setServices(data);
            } catch (requestError) {
                console.error(requestError);
                setError("Services could not be loaded. Please try again later.");
            } finally {
                setIsLoading(false);
            }
        }

        loadServices();
    }, []);

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
                <p className="services-status-message services-error-message">
                    {error}
                </p>
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