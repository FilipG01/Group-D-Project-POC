const API_BASE_URL = "http://localhost:8080";

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Request failed with status ${response.status}`;

        try {
            const errorBody = await response.json();

            message =
                errorBody.message ||
                errorBody.detail ||
                errorBody.error ||
                message;
        } catch {
            const text = await response.text();

            if (text) {
                message = text;
            }
        }

        const error = new Error(message);
        error.status = response.status;

        throw error;
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export async function getPublishedBlogPosts({
                                                page = 0,
                                                size = 9,
                                            } = {}) {
    const searchParams = new URLSearchParams({
        page: String(page),
        size: String(size),
    });

    const response = await fetch(
        `${API_BASE_URL}/api/blog?${searchParams}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function getPublishedBlogPostBySlug(slug) {
    const response = await fetch(
        `${API_BASE_URL}/api/blog/${encodeURIComponent(slug)}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}