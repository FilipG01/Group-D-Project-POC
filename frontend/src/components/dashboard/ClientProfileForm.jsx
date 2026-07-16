import { useEffect, useState } from "react";
import {
    getOwnClientProfile,
    updateOwnClientProfile,
} from "../../api/apiClientProfile";
import { useAuth } from "../../auth/useAuth.js";

const preferredContactOptions = [
    { value: "", label: "No preference" },
    { value: "EMAIL", label: "Email" },
    { value: "PHONE", label: "Phone" },
    { value: "IN_APP", label: "In-app chat" },
];

function createInitialForm(profile) {
    return {
        dateOfBirth: profile?.dateOfBirth || "",
        therapyGoalsSummary: profile?.therapyGoalsSummary || "",
        preferredContactMethod: profile?.preferredContactMethod || "",
    };
}

function ClientProfileForm() {
    const { user } = useAuth();
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState(createInitialForm(null));
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
        let isCancelled = false;

        async function loadProfile() {
            setIsLoading(true);
            setError("");

            try {
                const loadedProfile = await getOwnClientProfile();

                if (!isCancelled) {
                    setProfile(loadedProfile);
                    setFormData(createInitialForm(loadedProfile));
                }
            } catch (requestError) {
                console.error(requestError);

                if (!isCancelled) {
                    setError(
                        requestError.message ||
                        "Your client profile could not be loaded."
                    );
                }
            } finally {
                if (!isCancelled) {
                    setIsLoading(false);
                }
            }
        }

        loadProfile();

        return () => {
            isCancelled = true;
        };
    }, []);

    function handleFieldChange(event) {
        const { name, value } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: value,
        }));
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setIsSubmitting(true);
        setError("");
        setSuccessMessage("");

        try {
            const updatedProfile = await updateOwnClientProfile({
                dateOfBirth: formData.dateOfBirth || null,
                therapyGoalsSummary:
                    formData.therapyGoalsSummary.trim() || null,
                preferredContactMethod:
                    formData.preferredContactMethod || null,
            });

            setProfile(updatedProfile);
            setFormData(createInitialForm(updatedProfile));
            setSuccessMessage("Your profile has been updated.");
        } catch (requestError) {
            console.error(requestError);
            setError(
                requestError.message ||
                "Your profile could not be updated."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isLoading) {
        return <p>Loading your profile...</p>;
    }

    return (
        <section className="client-profile-section">
            <header className="admin-form-page-header">
                <p className="section-label">Client dashboard</p>
                <h1>My Profile</h1>
                <p>
                    Keep your personal therapy preferences up to date so
                    Root Therapy can better understand the support you are
                    looking for.
                </p>
            </header>

            {error && (
                <p className="admin-form-error">
                    {error}
                </p>
            )}

            {successMessage && (
                <p className="client-profile-success">
                    {successMessage}
                </p>
            )}

            <form
                className="admin-therapist-form"
                onSubmit={handleSubmit}
            >
                <section className="admin-form-section">
                    <div className="admin-form-section-heading">
                        <h2>Account information</h2>
                        <p>
                            These details come from your login account.
                        </p>
                    </div>

                    <div className="admin-form-grid">
                        <label>
                            <span>First name</span>
                            <input
                                type="text"
                                value={user?.firstName || ""}
                                disabled
                            />
                        </label>

                        <label>
                            <span>Last name</span>
                            <input
                                type="text"
                                value={user?.lastName || ""}
                                disabled
                            />
                        </label>

                        <label>
                            <span>Email address</span>
                            <input
                                type="email"
                                value={user?.email || ""}
                                disabled
                            />
                        </label>

                        <label>
                            <span>Phone number</span>
                            <input
                                type="tel"
                                value={user?.phoneNumber || ""}
                                disabled
                            />
                        </label>
                    </div>
                </section>

                <section className="admin-form-section">
                    <div className="admin-form-section-heading">
                        <h2>Therapy preferences</h2>
                        <p>
                            Share the details that are useful for your care.
                        </p>
                    </div>

                    <div className="admin-form-grid">
                        <label>
                            <span>Date of birth</span>
                            <input
                                type="date"
                                name="dateOfBirth"
                                value={formData.dateOfBirth}
                                onChange={handleFieldChange}
                            />
                        </label>

                        <label>
                            <span>Preferred contact method</span>
                            <select
                                name="preferredContactMethod"
                                value={formData.preferredContactMethod}
                                onChange={handleFieldChange}
                            >
                                {preferredContactOptions.map((option) => (
                                    <option
                                        key={option.value}
                                        value={option.value}
                                    >
                                        {option.label}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label className="admin-form-full-width">
                            <span>Therapy goals summary</span>
                            <textarea
                                name="therapyGoalsSummary"
                                value={formData.therapyGoalsSummary}
                                onChange={handleFieldChange}
                                rows="7"
                                maxLength="2000"
                                placeholder="Tell us what you would like support with..."
                            />
                            <small>
                                This information is private and should only
                                include what you are comfortable sharing.
                            </small>
                        </label>
                    </div>
                </section>

                <button
                    type="submit"
                    className="admin-primary-button"
                    disabled={isSubmitting}
                >
                    {isSubmitting ? "Saving..." : "Save My Profile"}
                </button>
            </form>
        </section>
    );
}

export default ClientProfileForm;
