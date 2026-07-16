const API_URL = "http://localhost:8080";

async function readErrorMessage(response) {
    const contentType =
        response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        try {
            const body = await response.json();

            return (
                body.message ||
                body.detail ||
                body.error ||
                `Request failed with status ${response.status}`
            );
        } catch {
            return `Request failed with status ${response.status}`;
        }
    }

    const text = await response.text();

    return (
        text ||
        `Request failed with status ${response.status}`
    );
}

export async function apiRequest(
    path,
    options = {}
) {
    const headers = {
        ...(options.headers || {}),
    };

    /*
     * Do not force JSON headers for FormData uploads.
     * The browser must generate the multipart boundary.
     */
    if (
        options.body &&
        !(options.body instanceof FormData) &&
        !headers["Content-Type"]
    ) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(
        `${API_URL}${path}`,
        {
            ...options,
            credentials: "include",
            headers,
        }
    );

    if (!response.ok) {
        const error = new Error(
            await readErrorMessage(response)
        );

        error.status = response.status;

        throw error;
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}