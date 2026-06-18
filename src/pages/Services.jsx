import { Link } from "react-router-dom";

const services = [
    {
        id: 1,
        title: "Addiction Recovery",
        description:
            "Get the necessary support and guidance to overcome addiction and reclaim control of your life.",
    },
    {
        id: 2,
        title: "Relationship Counselling",
        description:
            "Resolve conflicts, improve communication, and build stronger and more fulfilling relationships.",
    },
    {
        id: 3,
        title: "Stress Management",
        description:
            "Learn effective strategies to manage and reduce stress levels, promoting a healthier lifestyle.",
    },
    {
        id: 4,
        title: "Anxiety & Intrusive Thoughts",
        description:
            "Overcome the burden of anxiety by learning to tame intrusive thoughts. Our service offers tools and techniques grounded in cognitive-behavioral therapy to help you reframe and disempower anxious thoughts. Take charge of your mental well-being and discover a renewed sense of peace and clarity in your everyday life.",
    },
    {
        id: 5,
        title: "Bereavement",
        description:
            "In my bereavement therapy sessions, I offer specialized support to individuals coping with the loss of a loved one. Through tailored therapeutic intervention, I guide clients through the emotional complexities of grief, fostering healing and aiding the grieving process.",
    },
    {
        id: 6,
        title: "Depression",
        description:
            "As a therapist, I approach depression with compassionate curiosity, delving into the underlying causes and guiding clients towards resolution. I navigate alongside them, offering support and tools to discover hope, empowering them until they're ready to stride confidently on their independent path.",
    },
    {
        id: 7,
        title: "LGBTQ+ Support",
        description:
            "We understand the importance of culturally competent psychotherapy for individuals in the LGBT community. Our therapists are knowledgeable about the specific challenges and experiences faced by LGBT individuals, and offer a compassionate and understanding environment to address these issues.",
    },
    {
        id: 8,
        title: "ADHD, Autism & Neurodiversity",
        description:
            "Through my psychotherapy services tailored for ADHD, autism, and neurodivergent individuals, I provide a safe space for clients who think divergently. Drawing from my own lived experiences, I foster acceptance, tolerance, and adaptation, modeling self-compassion and healthy coping mechanisms. I empower clients to courageously embrace their true selves and prioritize self-care.",
    },
    {
        id: 9,
        title: "General Stress Support",
        description:
            "Are you tired of feeling overwhelmed by stress? Look no further than Stress, a transformative service that empowers individuals to take control of their stress levels and regain balance in their lives. Our holistic approach combines evidence-based strategies with personalized coaching, providing you with the support you need to thrive and flourish in the face of stress.",
    },
];

function Services() {
    return (
        <main className="services-page">
            <section className="services-hero">
                <div className="services-hero-content">
                    <p className="section-label">Services</p>
                    <h1>Support that meets you where you are.</h1>
                    <p>
                        Rooted in empathy and professional care, our services are designed
                        to offer a safe, grounded space for healing, reflection, and growth.
                    </p>
                </div>
            </section>

            <section className="services-intro">
                <p className="section-label">How we can help</p>
                <h2>Therapeutic support for a range of needs.</h2>
                <p>
                    Whether you are navigating anxiety, grief, stress, relationships,
                    identity, or neurodiversity, each service is approached with care,
                    respect, and confidentiality.
                </p>
            </section>

            <section className="services-list-section">
                <div className="services-grid">
                    {services.map((service) => (
                        <article className="service-card" key={service.id}>
              <span className="service-number">
                {String(service.id).padStart(2, "0")}
              </span>

                            <h3>{service.title}</h3>

                            <p>{service.description}</p>
                        </article>
                    ))}
                </div>
            </section>

            <section className="services-contact-cta">
                <div>
                    <p className="section-label">Not sure where to begin?</p>
                    <h2>We can help you find the right support.</h2>
                    <p>
                        If you are unsure which service best fits your needs, you can get in
                        touch and ask a question before booking.
                    </p>
                </div>

                <Link to="/contact" className="services-cta-button">
                    Contact Us →
                </Link>
            </section>
        </main>
    );
}

export default Services;