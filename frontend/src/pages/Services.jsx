import ServicesHero from "../components/services/ServicesHero.jsx"
import ServicesIntro from "../components/services/ServicesIntro.jsx"
import ContactCTA from "../components/shared/ContactCTA.jsx"
import ServicesList from "../components/services/ServicesList";
import "../styles/services.css";

function Services() {
    return (
        <main className="services-page">
            <ServicesHero />
            <ServicesIntro />
            <ServicesList />
            <ContactCTA />
        </main>
    )
}

export default Services