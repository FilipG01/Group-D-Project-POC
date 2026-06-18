import { Link } from "react-router-dom";
import "../../styles/home.css";
import ServicesTree from "../components/ServicesTree.jsx";
import LocationSection from "../components/LocationSection.jsx";
import AboutPreview from '../components/AboutPreview'

function Home() {
    return (
        <main className="home-page">
            <section className="home-hero">
                <div className="home-hero-overlay"></div>

                <div className="home-hero-content">

                    <h1>Support that meets you where you are.</h1>

                    <p className="home-blurb">
                        A calm, confidential space for therapy, reflection, and growth.
                        Root Therapy offers compassionate support for anxiety, stress,
                        relationships, grief, identity, and emotional well-being.
                    </p>

                    <div className="home-hero-buttons">
                        <Link to="/contact" className="home-primary-button">
                            Book a Session →
                        </Link>

                        <Link to="/services" className="home-secondary-button">
                            Learn More
                        </Link>
                    </div>
                </div>
            </section>
            <ServicesTree />
            <AboutPreview />
            <LocationSection />
        </main>
    )
}

export default Home