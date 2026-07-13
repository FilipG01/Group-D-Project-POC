import SEO from "../components/shared/SEO.jsx";
import { pageMeta } from "../data/pageMetaData.js";
import "../styles/legal.css";
import { Link } from "react-router-dom";

function Terms() {
    return (
        <>
            <SEO {...pageMeta.terms} />

            <main className="legal-page">
                <section className="legal-hero">
                    <div className="legal-container">
                        <p className="section-label">Legal</p>
                        <h1>Terms & Conditions</h1>
                        <p className="legal-updated">
                            Last updated: July 2026
                        </p>
                    </div>
                </section>

                <section className="legal-content">
                    <div className="legal-container">
                        <p>
                            These Terms and Conditions govern your use of the Root
                            Therapy website. By accessing or using this website, you
                            agree to these terms. If you do not agree with them, please
                            do not use the website.
                        </p>

                        <section className="legal-section">
                            <h2>1. About this website</h2>

                            <p>
                                This website provides general information about Root
                                Therapy, the psychotherapy services offered, the
                                therapist, and ways to contact the practice.
                            </p>

                            <p>
                                Website content is provided for general information only.
                                It does not constitute medical advice, diagnosis,
                                emergency support, or a substitute for professional
                                assessment and treatment.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>2. No therapist–client relationship</h2>

                            <p>
                                Viewing this website, submitting an enquiry, sending an
                                email, or leaving a message does not by itself create a
                                therapist–client relationship.
                            </p>

                            <p>
                                A therapeutic relationship begins only after availability,
                                suitability, consent, fees, confidentiality, scheduling,
                                and any applicable practice terms have been discussed and
                                agreed directly.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>3. Enquiries and appointments</h2>

                            <p>
                                Contacting Root Therapy does not guarantee that an
                                appointment will be available or that a particular service
                                will be suitable for your needs.
                            </p>

                            <p>
                                Any appointment arrangements, fees, cancellation terms,
                                payment requirements, or clinical agreements will be
                                communicated separately before services begin.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>4. Emergency and crisis situations</h2>

                            <p>
                                This website and its contact methods are not emergency or
                                crisis-response services. Messages may not be read or
                                answered immediately.
                            </p>

                            <p>
                                If you believe that you or another person is in immediate
                                danger, contact the emergency services or an appropriate
                                crisis-support service without waiting for a response from
                                Root Therapy.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>5. Accuracy of information</h2>

                            <p>
                                We aim to keep the information on this website accurate and
                                current. However, we do not guarantee that every page will
                                always be complete, error-free, or fully up to date.
                            </p>

                            <p>
                                Services, availability, qualifications, contact details,
                                prices, and website content may be updated when required.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>6. Acceptable use</h2>

                            <p>You must not use this website to:</p>

                            <ul>
                                <li>Break any applicable law or regulation</li>
                                <li>
                                    Submit unlawful, threatening, abusive, or fraudulent
                                    material
                                </li>
                                <li>
                                    Attempt to gain unauthorised access to the website,
                                    server, or connected systems
                                </li>
                                <li>
                                    Introduce malware, malicious code, or harmful material
                                </li>
                                <li>
                                    Interfere with the security or normal operation of the
                                    website
                                </li>
                                <li>
                                    Misrepresent your identity or impersonate another person
                                </li>
                            </ul>
                        </section>

                        <section className="legal-section">
                            <h2>7. Intellectual property</h2>

                            <p>
                                Unless otherwise stated, the website design, written
                                content, branding, logo, graphics, and original materials
                                are owned by or licensed to Root Therapy.
                            </p>

                            <p>
                                You may view the website for personal, non-commercial use.
                                You may not reproduce, republish, distribute, sell, modify,
                                or commercially exploit its content without prior written
                                permission.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>8. Third-party links and services</h2>

                            <p>
                                The website may contain links to external services,
                                websites, maps, social-media platforms, or professional
                                profiles.
                            </p>

                            <p>
                                Root Therapy does not control these third-party services
                                and is not responsible for their availability, content,
                                security, terms, or privacy practices.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>9. Website availability</h2>

                            <p>
                                We may update, suspend, restrict, or withdraw parts of the
                                website without notice for maintenance, security, technical,
                                legal, or operational reasons.
                            </p>

                            <p>
                                We do not guarantee that the website will always be
                                available or free from interruptions, defects, viruses, or
                                other harmful components.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>10. Limitation of liability</h2>

                            <p>
                                To the fullest extent permitted by law, Root Therapy will
                                not be liable for loss or damage arising solely from
                                reliance on general website information, temporary website
                                unavailability, or the use of third-party links.
                            </p>

                            <p>
                                Nothing in these terms excludes or limits any liability or
                                consumer right that cannot lawfully be excluded or limited.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>11. Privacy</h2>

                            <p>
                                Personal information submitted through the website is
                                handled in accordance with our Privacy Policy.
                            </p>

                            <p>
                                <Link to="/privacy">
                                    Read the Root Therapy Privacy Policy
                                </Link>
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>12. Changes to these terms</h2>

                            <p>
                                These Terms and Conditions may be updated to reflect
                                changes to the website, services, business practices, or
                                applicable legal requirements.
                            </p>

                            <p>
                                The latest version will be published on this page with an
                                updated revision date.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>13. Governing law</h2>

                            <p>
                                These terms are governed by the laws of Ireland. Any
                                disputes will be subject to the jurisdiction of the Irish
                                courts, subject to any mandatory rights available under
                                applicable consumer law.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>14. Contact</h2>

                            <p>
                                Questions about these Terms and Conditions can be sent to:
                            </p>

                            <address className="legal-contact">
                                <strong>Root Therapy</strong>

                                <a href="mailto:marlenatherapy@gmail.com">
                                    marlenatherapy@gmail.com
                                </a>

                                <a href="tel:+353833036099">
                                    +353 83 303 6099
                                </a>
                            </address>
                        </section>
                    </div>
                </section>
            </main>
        </>
    );
}

export default Terms;