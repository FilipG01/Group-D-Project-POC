const API_BASE_URL = "http://localhost:8080";

export function getImageUrl(imageUrl) {
    if (!imageUrl) {
        return "";
    }

    if (
        imageUrl.startsWith("http://") ||
        imageUrl.startsWith("https://") ||
        imageUrl.startsWith("data:")
    ) {
        return imageUrl;
    }

    if (imageUrl.startsWith("/uploads/")) {
        return `${API_BASE_URL}${imageUrl}`;
    }

    // Images stored in frontend/public continue using their existing path.
    return imageUrl;
}