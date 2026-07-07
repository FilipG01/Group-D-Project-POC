import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerClient } from "../api/apiAuth";

function Register() {
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
        <main>
            <section>
                <h2>Register</h2>
                <form onSubmit={handleSubmit}>
                    <label>First name
                        <input name="firstName" value={formData.firstName} onChange={handleChange} required />
                    </label>

                    <label>Last name
                        <input name="lastName" value={formData.lastName} onChange={handleChange} required />
                    </label>

                    <label>Email
                        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
                    </label>

                    <label>Phone number
                        <input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} required />
                    </label>

                    <label>Password
                        <input type="password" name="password" value={formData.password} onChange={handleChange} required />
                    </label>

                    {error && <p>{error}</p>}

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Creating account..." : "Create account"}
                    </button>
                </form>

                <p>Already have an account? <Link to="/login">Login</Link></p>
            </section>
        </main>
    );
}

export default Register;