import ContactCTA from "../components/shared/ContactCTA.jsx";
import SEO from "../components/shared/SEO.jsx";
import ServicesHero from "../components/services/ServicesHero.jsx";
import ServicesIntro from "../components/services/ServicesIntro.jsx";
import ServicesList from "../components/services/ServicesList.jsx";
import { pageMeta } from "../data/pageMetaData.js";

import "../styles/services/services.css";

function Services() {
    return (
        <>
            <SEO {...pageMeta.services} />

            <main className="services-page">
                <ServicesHero />
                <ServicesIntro />
                <ServicesList />
                <ContactCTA />
            </main>
        </>
    );
}

export default Services;