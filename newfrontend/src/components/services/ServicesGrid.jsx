import ServiceCard from "./ServiceCard.jsx"
import { services } from "../../../data/servicesData.js"

function ServicesGrid() {
    return (
        <section className="services-list-section">
            <div className="services-grid">
                {services.map((service) => (
                    <ServiceCard key={service.id} service={service} />
                ))}
            </div>
        </section>
    )
}

export default ServicesGrid