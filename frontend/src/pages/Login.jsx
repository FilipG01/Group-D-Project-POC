import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

function Login(){
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

    async function handleSubmit(e){
        e.preventDefault();
        setError("");
        setSubmitting(true);
        try{
            await loginUser(formData);
            navigate("/dashboard");
        }catch(err){
            setError(err.message || "Error Trying to Login!");
        } finally{
            setSubmitting(false);
        }
    }

  return (
        <main>
            <section>
                <h2>Login</h2>
                <form onSubmit={handleSubmit}>
                    <label>Email
                        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
                    </label>
                    <label>Password
                        <input type="password" name="password" value={formData.password} onChange={handleChange} required />
                    </label>

                    {error && <p className="auth-error">{error}</p>}

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Logging in..." : "Login"}
                    </button>
                </form>

                <p>Need an account? <Link to="/register">Register</Link></p>
            </section>
        </main>
    );
}

export default Login;