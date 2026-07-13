import SEO from "../components/shared/SEO.jsx";
import { pageMeta } from "../data/pageMetaData.js";
import "../styles/legal.css";

function Privacy() {
    return (
        <>
            <SEO {...pageMeta.privacy} />

            <main className="legal-page">
                <section className="legal-hero">
                    <div className="legal-container">
                        <p className="section-label">Legal</p>
                        <h1>Privacy Policy</h1>
                        <p className="legal-updated">
                            Last updated: July 2026
                        </p>
                    </div>
                </section>

                <section className="legal-content">
                    <div className="legal-container">
                        <p>
                            Root Therapy is committed to protecting your privacy and
                            handling your personal information responsibly. This Privacy
                            Policy explains what information we collect through this
                            website, why we collect it, and how it is used.
                        </p>

                        <section className="legal-section">
                            <h2>1. Information we collect</h2>

                            <p>
                                When you use this website, we may collect information that
                                you voluntarily provide, including:
                            </p>

                            <ul>
                                <li>Your name</li>
                                <li>Your email address</li>
                                <li>Your telephone number</li>
                                <li>
                                    Information included in your enquiry or contact form
                                </li>
                            </ul>

                            <p>
                                Please avoid including highly sensitive personal or medical
                                information in your initial enquiry.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>2. How we use your information</h2>

                            <p>Your information may be used to:</p>

                            <ul>
                                <li>Respond to your enquiry</li>
                                <li>Arrange an appointment</li>
                                <li>
                                    Communicate with you regarding our services
                                </li>
                                <li>
                                    Improve our website and user experience
                                </li>
                                <li>Maintain website security</li>
                            </ul>

                            <p>Your information is never sold to third parties.</p>
                        </section>

                        <section className="legal-section">
                            <h2>3. Website analytics</h2>

                            <p>
                                This website uses Google Analytics to understand how
                                visitors interact with the website. Google Analytics may
                                collect information such as pages visited, time spent on
                                the website, device type, browser information, approximate
                                location and referral source.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>4. Google Ads</h2>

                            <p>
                                We use Google Ads and conversion tracking to measure the
                                effectiveness of advertising campaigns. Google may record
                                whether visitors complete actions such as submitting an
                                enquiry, clicking a phone number, clicking an email link or
                                opening WhatsApp after interacting with an advertisement.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>5. Microsoft Clarity</h2>

                            <p>
                                We may use Microsoft Clarity to understand how visitors
                                interact with the website. Clarity may record anonymous
                                information such as clicks, scrolling behaviour, device
                                information and browser information.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>6. Cookies</h2>

                            <p>
                                This website may use cookies and similar technologies to
                                measure website performance, analyse visitor behaviour and
                                improve advertising effectiveness. You can manage or disable
                                cookies through your browser settings.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>7. Third-party services</h2>

                            <p>
                                This website may use third-party services including Google
                                Analytics, Google Tag Manager, Google Ads, Microsoft Clarity
                                and Google Maps. These providers process information under
                                their own privacy policies.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>8. Data security</h2>

                            <p>
                                Reasonable technical and organisational measures are used
                                to help protect information submitted through this website.
                                However, no method of transmitting information over the
                                internet can be guaranteed to be completely secure.
                            </p>
                        </section>

                        <section className="legal-section">
                            <h2>9. Your rights</h2>

                            <p>
                                If you are located in Ireland or elsewhere in the European
                                Economic Area, you may have rights under the General Data
                                Protection Regulation, including the right to:
                            </p>

                            <ul>
                                <li>Request access to your personal information</li>
                                <li>Request correction of inaccurate information</li>
                                <li>Request deletion of your personal information</li>
                                <li>Object to certain types of processing</li>
                                <li>Request restriction of processing</li>
                                <li>Request data portability where applicable</li>
                                <li>
                                    Lodge a complaint with the Irish Data Protection
                                    Commission
                                </li>
                            </ul>
                        </section>

                        <section className="legal-section">
                            <h2>10. Contact</h2>

                            <p>
                                Questions about this Privacy Policy or your personal
                                information can be sent to:
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

                        <section className="legal-section">
                            <h2>11. Changes to this policy</h2>

                            <p>
                                This Privacy Policy may be updated from time to time to
                                reflect changes to legal requirements or website
                                functionality. The latest version will always be published
                                on this page.
                            </p>
                        </section>
                    </div>
                </section>
            </main>
        </>
    );
}

export default Privacy;