import ServicesHero from "../components/services/ServicesHero.jsx"
import ServicesIntro from "../components/services/ServicesIntro.jsx"
import ServicesGrid from "../components/services/ServicesGrid.jsx"
import ContactCTA from "../components/shared/ContactCTA.jsx"
import "../styles/services.css";

function Services() {
    return (
        <main className="services-page">
            <ServicesHero />
            <ServicesIntro />
            <ServicesGrid />
            <ContactCTA />
        </main>
    )
}

export default Services