import "../styles/home.css";
import SEO from "../components/shared/SEO.jsx";
import { businessSchema } from "../data/businessSchema.js";

import { pageMeta } from "../data/pageMetaData.js";
import LocationGallery from "../components/about/LocationGallery.jsx";
import HomeHero from "../components/home/HomeHero.jsx";
import ServicesTree from "../components/home/ServicesTree.jsx";
import LocationSection from "../components/home/LocationSection.jsx";

function Home() {
    return (
        <>
            <SEO {...pageMeta.home}
                 schema={businessSchema}
            />

            <main className="home-page">
                <HomeHero />
                <ServicesTree />
                <LocationGallery />
                <LocationSection />
            </main>
        </>
    );
}

export default Home;