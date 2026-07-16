import SEO from "../components/shared/SEO.jsx";
import { pageMeta } from "../data/pageMetaData.js";

import ContactForm from "../components/contact/ContactForm.jsx";
import LocationSection from "../components/home/LocationSection";

function Contact() {
    return (
        <>
            <SEO {...pageMeta.contact} />

            <main className="contact-page">
                <ContactForm />
                <LocationSection />
            </main>
        </>
    )
}

export default Contact
