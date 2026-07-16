import { apiRequest } from "./apiClient.js";

export function getPublishedServices() {
    return apiRequest("/api/services");
}

export function getServiceBySlug(slug) {
    return apiRequest(
        `/api/services/${encodeURIComponent(slug)}`
    );
}

export function getAdminServices() {
    return apiRequest("/api/admin/services");
}

export function getAdminServiceById(id) {
    return apiRequest(
        `/api/admin/services/${encodeURIComponent(id)}`
    );
}

export function createService(serviceData) {
    return apiRequest(
        "/api/admin/services",
        {
            method: "POST",
            body: JSON.stringify(serviceData),
        }
    );
}

export function updateService(id, serviceData) {
    return apiRequest(
        `/api/admin/services/${encodeURIComponent(id)}`,
        {
            method: "PUT",
            body: JSON.stringify(serviceData),
        }
    );
}

export function setServicePublished(
    id,
    published
) {
    return apiRequest(
        `/api/admin/services/${encodeURIComponent(id)}/publication`,
        {
            method: "PATCH",
            body: JSON.stringify({
                published,
            }),
        }
    );
}

export function setServiceArchived(
    id,
    archived
) {
    return apiRequest(
        `/api/admin/services/${encodeURIComponent(id)}/archive`,
        {
            method: "PATCH",
            body: JSON.stringify({
                archived,
            }),
        }
    );
}

export function uploadServiceImage(file) {
    const formData = new FormData();

    formData.append("file", file);

    return apiRequest(
        "/api/admin/uploads/services",
        {
            method: "POST",
            body: formData,
        }
    );
}

export function reorderServices(services) {
    return apiRequest(
        "/api/admin/services/reorder",
        {
            method: "PATCH",
            body: JSON.stringify({
                services: services.map(
                    (service, index) => ({
                        id: service.id,
                        displayOrder: index + 1,
                    })
                ),
            }),
        }
    );
}