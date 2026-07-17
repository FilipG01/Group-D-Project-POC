import { apiRequest } from "./apiClient.js";

export function getTherapistBlogPosts({
                                          page = 0,
                                          size = 20,
                                      } = {}) {
    const searchParams = new URLSearchParams({
        page: String(page),
        size: String(size),
    });

    return apiRequest(
        `/api/therapist/blog?${searchParams.toString()}`
    );
}

export function getTherapistBlogPost(postId) {
    return apiRequest(
        `/api/therapist/blog/${encodeURIComponent(postId)}`
    );
}

export function createTherapistBlogPost(payload) {
    return apiRequest("/api/therapist/blog", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export function updateTherapistBlogPost(
    postId,
    payload
) {
    return apiRequest(
        `/api/therapist/blog/${encodeURIComponent(postId)}`,
        {
            method: "PUT",
            body: JSON.stringify(payload),
        }
    );
}

export function submitTherapistBlogPost(
    postId,
    version
) {
    return apiRequest(
        `/api/therapist/blog/${encodeURIComponent(postId)}/submit`,
        {
            method: "POST",
            body: JSON.stringify({
                version,
            }),
        }
    );
}

export function uploadTherapistBlogImage(file) {
    const formData = new FormData();

    formData.append("file", file);

    return apiRequest(
        "/api/therapist/uploads/blog",
        {
            method: "POST",
            body: formData,
        }
    );
}