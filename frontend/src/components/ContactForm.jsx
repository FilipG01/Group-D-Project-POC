import "../styles/ContactForm.css";
import emailjs from "@emailjs/browser";
import { Link } from "react-router-dom";

function ContactForm() {
    function handleSubmit(event) {
        event.preventDefault();

        emailjs.sendForm(
            "service_5lejcht",
            "template_xk3o6wn",
            event.target,
            "YDQEO17d0RSmcX8EV"
        )
            .then(() => {
                alert("Message sent!");
                event.target.reset();
            })
            .catch((error) => {
                console.error(error);
                alert("Something went wrong.");
            });
    }

    return (
        <section className="contact-form-section" aria-labelledby="contact-title">
            <div className="contact-form-heading">
                <h1 id="contact-title">Get in touch</h1>
                <span className="contact-form-icon" aria-hidden="true"></span>
                <p>
                    We'd love to hear from you.
                    <br />
                    Reach out with any questions.
                </p>
            </div>

            <form className="contact-form-card" onSubmit={handleSubmit}>
                <h2>Send us a message</h2>

                <div className="contact-form-fields">
                    <label className="contact-form-field">
                        <span>Name</span>
                        <input
                            type="text"
                            name="name"
                            placeholder="Enter Name"
                            autoComplete="name"
                            required
                        />
                    </label>

                    <label className="contact-form-field">
                        <span>Email Address</span>
                        <input
                            type="email"
                            name="email"
                            placeholder="you@example.com"
                            autoComplete="email"
                            required
                        />
                    </label>

                    <label className="contact-form-field">
                        <span>Phone Number</span>
                        <input
                            type="tel"
                            name="phone"
                            placeholder="e.g. +353 00 000 0000"
                            autoComplete="tel"
                            required
                        />
                    </label>

                    <label className="contact-form-field">
                        <span>Write a Message!</span>
                        <textarea
                            name="message"
                            placeholder="What's on your mind?"
                            rows="5"
                            required
                        ></textarea>
                    </label>
                </div>

                <div className="contact-form-actions">
                    <button type="submit">Send</button>
                </div>
            </form>

            <section className="contact-register-msg">

                <p className="section-label">
                    Online Services
                </p>

                <h2>
                    Prefer to manage everything online?
                </h2>

                <p>
                    Create an account to book appointments, manage your details, and access
                    your personal space. If you have any questions, you can still contact us using
                    the form above.
                </p>

                <Link to="/register" className="contact-register-button">
                Create an Account → </Link>
            </section>

            <aside className="contact-form-note">
                <h2>You don't need to know exactly what to say.</h2>
                <p>
                    Reaching out is a brave step. We're here to listen and help you
                    explore what support might look like for you.
                </p>
            </aside>
        </section>
    );
}

export default ContactForm;
