const API_BASE_URL = "http://localhost:8080";

async function handleResponse(response) {
    if (!response.ok) {
        const message = await response.text();

        throw new Error(
            message || `Request failed with status ${response.status}`
        );
    }

    return response.json();
}

export async function getPublishedServices() {
    const response = await fetch(`${API_BASE_URL}/api/services`, {
        method: "GET",
        credentials: "include",
    });

    return handleResponse(response);
}

export async function getServiceBySlug(slug) {
    const response = await fetch(
        `${API_BASE_URL}/api/services/${encodeURIComponent(slug)}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function getAdminServices() {
    const response = await fetch(
        "http://localhost:8080/api/admin/services",
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function getAdminServiceById(id) {
    const response = await fetch(
        `http://localhost:8080/api/admin/services/${id}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function createService(serviceData) {
    const response = await fetch(
        "http://localhost:8080/api/admin/services",
        {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(serviceData),
        }
    );

    return handleResponse(response);
}

export async function updateService(id, serviceData) {
    const response = await fetch(
        `http://localhost:8080/api/admin/services/${id}`,
        {
            method: "PUT",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(serviceData),
        }
    );

    return handleResponse(response);
}

export async function setServicePublished(id, published) {
    const response = await fetch(
        `http://localhost:8080/api/admin/services/${id}/publication`,
        {
            method: "PATCH",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ published }),
        }
    );

    return handleResponse(response);
}

export async function setServiceArchived(id, archived) {
    const response = await fetch(
        `http://localhost:8080/api/admin/services/${id}/archive`,
        {
            method: "PATCH",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ archived }),
        }
    );

    return handleResponse(response);
}

export async function uploadServiceImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(
        "http://localhost:8080/api/admin/uploads/services",
        {
            method: "POST",
            credentials: "include",
            body: formData,
        }
    );

    return handleResponse(response);
}

export async function reorderServices(services) {
    const response = await fetch(
        "http://localhost:8080/api/admin/services/reorder",
        {
            method: "PATCH",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                services: services.map((service, index) => ({
                    id: service.id,
                    displayOrder: index + 1,
                })),
            }),
        }
    );

    return handleResponse(response);
}