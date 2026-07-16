import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import {
    getAdminServiceById,
    updateService,
} from "../../api/servicesApi.js";

import ServiceForm from "../../components/services/admin/ServiceForm.jsx";
import "../../styles/services/adminServiceForm.css";

function AdminServiceEdit() {
    const { serviceId } = useParams();
    const navigate = useNavigate();

    const [service, setService] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadService() {
            try {
                const data = await getAdminServiceById(serviceId);
                setService(data);
            } catch (requestError) {
                console.error(requestError);
                setError(requestError.message);
            } finally {
                setIsLoading(false);
            }
        }

        loadService();
    }, [serviceId]);

    async function handleUpdate(serviceData) {
        setIsSubmitting(true);
        setError("");

        try {
            await updateService(serviceId, serviceData);
            navigate("/admin/services");
        } catch (requestError) {
            console.error(requestError);
            setError(requestError.message);
        } finally {
            setIsSubmitting(false);
        }
    }

    if (isLoading) {
        return (
            <main className="admin-service-form-page">
                <p>Loading service...</p>
            </main>
        );
    }

    if (!service) {
        return (
            <main className="admin-service-form-page">
                <h1>Service not found</h1>
                <Link to="/admin/services">Back to services</Link>
            </main>
        );
    }

    return (
        <main className="admin-service-form-page">
            <div className="admin-form-page-header">
                <p className="section-label">Admin dashboard</p>
                <h1>Edit {service.title}</h1>
            </div>

            {error && (
                <p className="admin-services-error">
                    {error}
                </p>
            )}

            <ServiceForm
                initialData={service}
                onSubmit={handleUpdate}
                submitLabel="Save Changes"
                isSubmitting={isSubmitting}
            />
        </main>
    );
}

export default AdminServiceEdit;