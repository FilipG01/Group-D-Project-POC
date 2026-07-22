import {
    useEffect,
    useState,
} from "react";

import { uploadGalleryImage } from "../../../api/galleryApi.js";
import { getImageUrl } from "../../../utils/imageUrl.js";

const emptyForm = {
    imageUrl: "",
    caption: "",
    altText: "",
    displayOrder: 0,
    visible: false,
};

function GalleryImageForm({
                              initialData,
                              onSubmit,
                              submitLabel,
                              isSubmitting,
                          }) {
    const [formData, setFormData] =
        useState(emptyForm);

    const [uploadingImage, setUploadingImage] =
        useState(false);

    const [uploadError, setUploadError] =
        useState("");

    useEffect(() => {
        if (!initialData) {
            setFormData(emptyForm);
            return;
        }

        setFormData({
            imageUrl: initialData.imageUrl || "",
            caption: initialData.caption || "",
            altText: initialData.altText || "",
            displayOrder:
                initialData.displayOrder ?? 0,
            visible: Boolean(initialData.visible),
        });
    }, [initialData]);

    function handleFieldChange(event) {
        const {
            name,
            value,
            type,
            checked,
        } = event.target;

        setFormData((current) => ({
            ...current,
            [name]:
                type === "checkbox"
                    ? checked
                    : value,
        }));
    }

    async function handleImageUpload(event) {
        const file = event.target.files?.[0];

        if (!file) {
            return;
        }

        setUploadingImage(true);
        setUploadError("");

        try {
            const result =
                await uploadGalleryImage(file);

            setFormData((current) => ({
                ...current,
                imageUrl: result.url,
            }));
        } catch (error) {
            console.error(error);

            setUploadError(
                error.message ||
                "Image upload failed."
            );
        } finally {
            setUploadingImage(false);

            // Allows the same file to be selected again.
            event.target.value = "";
        }
    }

    function handleSubmit(event) {
        event.preventDefault();

        onSubmit({
            ...formData,
            displayOrder: Number(
                formData.displayOrder
            ),
        });
    }

    const previewUrl =
        getImageUrl(formData.imageUrl);

    return (
        <form
            className="admin-service-form"
            onSubmit={handleSubmit}
        >
            <div className="admin-image-field">
                <label>
                    <span>Upload gallery image</span>

                    <input
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        onChange={handleImageUpload}
                        disabled={
                            uploadingImage ||
                            isSubmitting
                        }
                    />
                </label>

                {uploadingImage && (
                    <p>Uploading image...</p>
                )}

                {uploadError && (
                    <p className="admin-services-error">
                        {uploadError}
                    </p>
                )}

                <label>
                    <span>Image URL</span>

                    <input
                        type="text"
                        name="imageUrl"
                        value={formData.imageUrl}
                        onChange={handleFieldChange}
                        placeholder="/uploads/gallery/example.jpg"
                        required
                    />
                </label>

                {previewUrl && (
                    <img
                        src={previewUrl}
                        alt="Gallery image preview"
                        className="admin-service-image-preview"
                    />
                )}
            </div>

            <div className="admin-form-grid">
                <label>
                    <span>Caption</span>

                    <input
                        type="text"
                        name="caption"
                        value={formData.caption}
                        onChange={handleFieldChange}
                        placeholder="A calm and welcoming therapy room"
                    />
                </label>

                <label>
                    <span>Display order</span>

                    <input
                        type="number"
                        name="displayOrder"
                        min="0"
                        value={formData.displayOrder}
                        onChange={handleFieldChange}
                        required
                    />
                </label>
            </div>

            <label>
                <span>Alt text</span>

                <input
                    type="text"
                    name="altText"
                    value={formData.altText}
                    onChange={handleFieldChange}
                    maxLength="255"
                    placeholder="Describe what can be seen in the image"
                    required
                />

                <small>
                    Alt text should describe the image
                    for screen-reader users.
                </small>
            </label>

            <label className="admin-checkbox-field">
                <input
                    type="checkbox"
                    name="visible"
                    checked={formData.visible}
                    onChange={handleFieldChange}
                />

                <span>
                    Show this image on the public website
                </span>
            </label>

            <button
                type="submit"
                className="admin-primary-button"
                disabled={
                    isSubmitting ||
                    uploadingImage
                }
            >
                {isSubmitting
                    ? "Saving..."
                    : submitLabel}
            </button>
        </form>
    );
}

export default GalleryImageForm;