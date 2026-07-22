import { apiRequest } from "./apiClient.js";

/*
 * Public gallery
 */

export function getPublicGalleryImages() {
    return apiRequest("/api/gallery");
}

/*
 * Admin gallery
 */

export function getAdminGalleryImages() {
    return apiRequest("/api/admin/gallery");
}

export function getAdminGalleryImageById(id) {
    return apiRequest(
        `/api/admin/gallery/${encodeURIComponent(id)}`
    );
}

export function createGalleryImage(imageData) {
    return apiRequest(
        "/api/admin/gallery",
        {
            method: "POST",
            body: JSON.stringify(imageData),
        }
    );
}

export function updateGalleryImage(
    id,
    imageData
) {
    return apiRequest(
        `/api/admin/gallery/${encodeURIComponent(id)}`,
        {
            method: "PUT",
            body: JSON.stringify(imageData),
        }
    );
}

export function setGalleryImageVisible(
    id,
    visible
) {
    return apiRequest(
        `/api/admin/gallery/${encodeURIComponent(id)}/visibility`,
        {
            method: "PATCH",
            body: JSON.stringify({
                visible,
            }),
        }
    );
}

export function setGalleryImageArchived(
    id,
    archived
) {
    return apiRequest(
        `/api/admin/gallery/${encodeURIComponent(id)}/archive`,
        {
            method: "PATCH",
            body: JSON.stringify({
                archived,
            }),
        }
    );
}

export function uploadGalleryImage(file) {
    const formData = new FormData();

    formData.append("file", file);

    return apiRequest(
        "/api/admin/uploads/gallery",
        {
            method: "POST",
            body: formData,
        }
    );
}

export function reorderGalleryImages(images) {
    return apiRequest(
        "/api/admin/gallery/reorder",
        {
            method: "PATCH",
            body: JSON.stringify({
                images: images.map(
                    (image, index) => ({
                        id: image.id,
                        displayOrder: index + 1,
                    })
                ),
            }),
        }
    );
}