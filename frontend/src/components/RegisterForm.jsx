import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerClient } from "../api/apiAuth";
import "../styles/authForm.css";

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
        setFormData((current) => ({ ...current, [name]: value }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            await registerClient(formData);
            navigate("/login");
        } catch (err) {
            setError(err.message || "Registration failed");
        } finally {
            setSubmitting(false);
        }
    }

    return (
        
            <section className="auth-section" aria-labelledby="login-title">

                <div className="auth-heading">
                    <h1 id="login-title"> Create Your Account</h1>
                    <span className="auth-icon" aria-hidden="true"></span>
                    <p>Begin your journey with us.</p>
                <br />
                </div>

                <form className="auth-card" onSubmit={handleSubmit}>
                    <h2>Create Account</h2>
                    <div className="auth-fields">
                    <label className="auth-field">
                        <span> First Name</span>

                        <input name="firstName" value={formData.firstName} onChange={handleChange} required />
                    </label>

                    <label className="auth-field">
                        <span> Last Name</span>
                        <input name="lastName" value={formData.lastName} onChange={handleChange} required />
                    </label>

                    <label className="auth-field">
                        <span> Email</span>
                        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
                    </label>

                    <label className="auth-field">
                        <span> Phone Number</span>
                        <input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} required />
                    </label>

                    <label className="auth-field">
                        <span> Password</span>
                        <input type="password" name="password" value={formData.password} onChange={handleChange} required />
                    </label>
                </div>

                    {error && (
                        <p className="auth-error">
                            {error}</p>
                            )}
                    
                    <div className="auth-actions">
                    <button type="submit" disabled={submitting}>
                        {submitting ? "Creating account..." : "Create account"}
                    </button>
                    </div>          

                <p className="auth-switch">

                Already have an account?{" "}<Link to="/login">Login Here</Link>
                </p>
                </form>
               
            </section>
        
    );
}

export default RegisterForm;