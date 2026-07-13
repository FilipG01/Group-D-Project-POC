import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createService } from "../../api/servicesApi.js";
import ServiceForm from "../../components/admin/ServiceForm.jsx";
import "../../styles/adminServiceForm.css";

function AdminServiceCreate() {
    const navigate = useNavigate();

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");

    async function handleCreate(serviceData) {
        setIsSubmitting(true);
        setError("");

        try {
            await createService(serviceData);
            navigate("/admin/services");
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className="admin-service-form-page">
            <div className="admin-form-page-header">
                <p className="section-label">Admin dashboard</p>
                <h1>Add Service</h1>
            </div>

            {error && (
                <p className="admin-services-error">
                    {error}
                </p>
            )}

            <ServiceForm
                onSubmit={handleCreate}
                submitLabel="Create Service"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default AdminServiceCreate;