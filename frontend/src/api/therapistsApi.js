const API_BASE_URL = "http://localhost:8080";

async function handleResponse(response) {
    if (!response.ok) {
        let message = `Request failed with status ${response.status}`;

        try {
            const body = await response.json();
            message =
                body.message ||
                body.detail ||
                body.error ||
                message;
        } catch {
            const body = await response.text();

            if (body) {
                message = body;
            }
        }

        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

/*
 * Public therapist requests
 */

export async function getPublicTherapists() {
    const response = await fetch(
        `${API_BASE_URL}/api/therapists/public`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function getPublicTherapist(userId) {
    const response = await fetch(
        `${API_BASE_URL}/api/therapists/public/${userId}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

/*
 * Therapist self-management
 */

export async function getOwnTherapistProfile() {
    const response = await fetch(
        `${API_BASE_URL}/api/therapist-profile/me`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function updateOwnTherapistProfile(profileData) {
    const response = await fetch(
        `${API_BASE_URL}/api/therapist-profile/me`,
        {
            method: "PUT",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(profileData),
        }
    );

    return handleResponse(response);
}

export async function uploadOwnTherapistImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(
        `${API_BASE_URL}/api/therapist/uploads/profile-image`,
        {
            method: "POST",
            credentials: "include",
            body: formData,
        }
    );

    return handleResponse(response);
}

/*
 * Admin therapist management
 */

export async function getAdminTherapists() {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/therapists`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function getAdminTherapist(userId) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/therapists/${userId}`,
        {
            method: "GET",
            credentials: "include",
        }
    );

    return handleResponse(response);
}

export async function createAdminTherapist(therapistData) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/therapists`,
        {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(therapistData),
        }
    );

    return handleResponse(response);
}

export async function updateAdminTherapist(userId, profileData) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/therapists/${userId}`,
        {
            method: "PUT",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(profileData),
        }
    );

    return handleResponse(response);
}

export async function reorderTherapists(therapists) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/therapists/reorder`,
        {
            method: "PATCH",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                therapists: therapists.map((therapist, index) => ({
                    userId: therapist.userId,
                    displayOrder: index + 1,
                })),
            }),
        }
    );

    return handleResponse(response);
}

/*
 * Shared therapist image upload
 */

export async function uploadTherapistImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(
        `${API_BASE_URL}/api/admin/uploads/therapists`,
        {
            method: "POST",
            credentials: "include",
            body: formData,
        }
    );

    return handleResponse(response);
}