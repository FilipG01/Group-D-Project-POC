import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerClient } from "../../api/apiAuth.js";
import "../../styles/authForm.css";

function RegisterForm() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
    });

    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            await registerClient({
                ...formData,
                email: formData.email.trim(),
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                phoneNumber: formData.phoneNumber.trim(),
            });

            navigate("/login");
        } catch (err) {
            setError(err.message || "Registration failed");
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <section
            className="auth-section"
            aria-labelledby="register-title"
        >
            <div className="auth-heading">
                <h1 id="register-title"> Create Your Account</h1>
                <span
                    className="auth-icon"
                    aria-hidden="true"
                ></span>
                <p>Begin your journey with us.</p>
                <br />
            </div>

            <form
                className="auth-card"
                onSubmit={handleSubmit}
            >
                <h2>Create Account</h2>

                <div className="auth-fields">
                    <label className="auth-field">
                        <span> First Name</span>

                        <input
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            autoComplete="given-name"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span> Last Name</span>

                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            autoComplete="family-name"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span> Email</span>

                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            autoComplete="email"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span> Phone Number</span>

                        <input
                            type="tel"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            autoComplete="tel"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span> Password</span>

                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            autoComplete="new-password"
                            minLength="8"
                            required
                        />
                    </label>
                </div>

                {error && (
                    <p className="auth-error">
                        {error}
                    </p>
                )}

                <div className="auth-actions">
                    <button
                        type="submit"
                        disabled={submitting}
                    >
                        {submitting
                            ? "Creating account..."
                            : "Create account"}
                    </button>
                </div>

                <p className="auth-switch">
                    Already have an account?{" "}
                    <Link to="/login">Login Here</Link>
                </p>
            </form>
        </section>
    );
}

export default RegisterForm;