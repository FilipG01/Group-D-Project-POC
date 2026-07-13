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