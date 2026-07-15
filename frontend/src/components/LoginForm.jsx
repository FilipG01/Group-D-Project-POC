import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth.js";
import "../styles/authForm.css";

function LoginForm(){
    const navigate = useNavigate();
    const { loginUser } = useAuth();
    const [formData, setFormData] = useState({
        email: "",
        password: "",
    });
    const [error, setError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function handleChange(e){
        const {name, value } = e.target;
        setFormData((current) => ({ ...current, [name]: value}));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            const loggedInUser = await loginUser(formData);

            if (loggedInUser.role === "ADMIN") {
                navigate("/admin");
            } else if (loggedInUser.role === "THERAPIST") {
                navigate("/therapist");
            } else {
                navigate("/dashboard");
            }

        } catch (err) {
            setError(err.message || "Error Trying to Login!");
        } finally {
            setSubmitting(false);
        }
    }

  return (
  
            <section className="auth-section" aria-labelledby="login-title">

                <div className="auth-heading">
                    <h1 id="login-title"> Welcome Back</h1>
                    <span className="auth-icon" aria-hidden="true"></span>
                    <p>Sign in to access your account.</p>
                <br />
                </div>



                <form className="auth-card" onSubmit={handleSubmit}>

                    <h2>Login</h2>

                    <div className="auth-fields">
                        <label className="auth-field">
                            <span>
                                Email Address
                            </span>

                          
                             <input type="email" name="email" value={formData.email} onChange={handleChange} required />
                            
                        </label>

                        <label className="auth-field">
                            <span>Password</span>

                            <input type="password" name="password" value={formData.password} onChange={handleChange} required />
                        </label>
                        </div>

                    {error && (
                        <p className="auth-error">
                            {error}</p>
                            )}


                <div className="auth-actions">
                    <button type="submit" disabled={submitting}>
                        {submitting ? "Logging in..." : "Login"}
                    </button>
                    </div>


                <p className="auth-switch">

                Don't have an account?{" "}<Link to="/register">Register Here</Link>
                </p>

                </form>
            </section>
        
    );
}

export default LoginForm;