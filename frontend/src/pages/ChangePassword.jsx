import { useState } from "react";
import { Link } from "react-router-dom";
import { changePassword } from "../api/apiAuth.js";
import "../styles/changePassword.css";

function ChangePassword() {
    const [formData, setFormData] = useState({
        currentPassword: "",
        newPassword: "",
        confirmNewPassword: "",
    });
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [submitting, setSubmitting] = useState(false);

    function handleChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: value,
        }));

        setError("");
        setSuccess("");
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (formData.newPassword !== formData.confirmNewPassword) {
            setError("New password and confirmation do not match.");
            return;
        }

        if (formData.newPassword === formData.currentPassword) {
            setError("New password must be different from the current password.");
            return;
        }

        setSubmitting(true);

        try {
            await changePassword(formData);

            setFormData({
                currentPassword: "",
                newPassword: "",
                confirmNewPassword: "",
            });
            setSuccess("Your password has been changed successfully.");
        } catch (err) {
            setError(err.message || "Password could not be changed.");
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <main className="change-password-page">
            <header className="change-password-header">
                <p className="section-label">Account security</p>
                <h1>Change Your Password</h1>
                <p>
                    Enter your current password, then choose and confirm a new
                    password for your account.
                </p>
            </header>

            <section className="change-password-card">
                <form onSubmit={handleSubmit}>
                    <div className="change-password-fields">
                        <label className="change-password-field">
                            <span>Current password</span>
                            <input
                                type="password"
                                name="currentPassword"
                                value={formData.currentPassword}
                                onChange={handleChange}
                                autoComplete="current-password"
                                minLength="8"
                                maxLength="72"
                                required
                            />
                        </label>

                        <label className="change-password-field">
                            <span>New password</span>
                            <input
                                type="password"
                                name="newPassword"
                                value={formData.newPassword}
                                onChange={handleChange}
                                autoComplete="new-password"
                                minLength="8"
                                maxLength="72"
                                required
                            />
                            <small>Use between 8 and 72 characters.</small>
                        </label>

                        <label className="change-password-field">
                            <span>Confirm new password</span>
                            <input
                                type="password"
                                name="confirmNewPassword"
                                value={formData.confirmNewPassword}
                                onChange={handleChange}
                                autoComplete="new-password"
                                minLength="8"
                                maxLength="72"
                                required
                            />
                        </label>
                    </div>

                    {error && (
                        <p className="change-password-message is-error" role="alert">
                            {error}
                        </p>
                    )}

                    {success && (
                        <p className="change-password-message is-success" role="status">
                            {success}
                        </p>
                    )}

                    <div className="change-password-actions">
                        <button type="submit" disabled={submitting}>
                            {submitting ? "Changing password..." : "Change password"}
                        </button>

                        <Link to="/" className="change-password-cancel">
                            Cancel
                        </Link>
                    </div>
                </form>
            </section>
        </main>
    );
}

export default ChangePassword;
