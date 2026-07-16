import { apiRequest } from "./apiClient.js";

export function getAdminBlogPosts({
                                      status = "",
                                      page = 0,
                                      size = 20,
                                  } = {}) {
    const searchParams = new URLSearchParams({
        page: String(page),
        size: String(size),
    });

    if (status) {
        searchParams.set("status", status);
    }

    return apiRequest(
        `/api/admin/blog?${searchParams.toString()}`
    );
}

export function getAdminBlogPost(postId) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}`
    );
}

export function publishAdminBlogPost(
    postId,
    version
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/publish`,
        {
            method: "POST",
            body: JSON.stringify({
                version,
            }),
        }
    );
}

export function requestChangesForAdminBlogPost(
    postId,
    note,
    version
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/request-changes`,
        {
            method: "POST",
            body: JSON.stringify({
                note,
                version,
            }),
        }
    );
}

export function rejectAdminBlogPost(
    postId,
    note,
    version
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/reject`,
        {
            method: "POST",
            body: JSON.stringify({
                note,
                version,
            }),
        }
    );
}

export function createAdminBlogPost(payload) {
    return apiRequest("/api/admin/blog", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export function updateAdminBlogPost(
    postId,
    payload
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}`,
        {
            method: "PUT",
            body: JSON.stringify(payload),
        }
    );
}

export function uploadAdminBlogImage(file) {
    const formData = new FormData();

    formData.append("file", file);

    return apiRequest(
        "/api/admin/uploads/blog",
        {
            method: "POST",
            body: formData,
        }
    );
}

export function unpublishAdminBlogPost(
    postId,
    version,
    note = ""
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/unpublish`,
        {
            method: "POST",
            body: JSON.stringify({
                version,
                note: note || null,
            }),
        }
    );
}

export function archiveAdminBlogPost(
    postId,
    version,
    note = ""
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/archive`,
        {
            method: "POST",
            body: JSON.stringify({
                version,
                note: note || null,
            }),
        }
    );
}

export function restoreAdminBlogPost(
    postId,
    version,
    note = ""
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/restore`,
        {
            method: "POST",
            body: JSON.stringify({
                version,
                note: note || null,
            }),
        }
    );
}

export function setAdminBlogPostFeatured(
    postId,
    featured,
    version
) {
    return apiRequest(
        `/api/admin/blog/${encodeURIComponent(postId)}/feature`,
        {
            method: "PATCH",
            body: JSON.stringify({
                featured,
                version,
            }),
        }
    );
}

export function reorderAdminBlogPosts(posts) {
    return apiRequest(
        "/api/admin/blog/reorder",
        {
            method: "PATCH",
            body: JSON.stringify({
                posts,
            }),
        }
    );
}