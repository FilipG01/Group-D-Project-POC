import "../styles/home.css";

import HomeHero from "../components/home/HomeHero.jsx";
import ServicesTree from "../components/home/ServicesTree.jsx";
import LocationSection from "../components/home/LocationSection.jsx";

function Home() {
    return (
        <main className="home-page">
            <HomeHero />
            <ServicesTree />
            <LocationSection />
        </main>
    );
}

export default Home;