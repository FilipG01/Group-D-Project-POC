import { services } from "../../data/servicesData";
import ServicesFeatureSection from "./ServicesFeatureSection";

function ServicesList() {
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