import { useEffect, useState } from "react";
import { uploadServiceImage } from "../../api/servicesApi.js";

const emptyForm = {
    title: "",
    slug: "",
    category: "",
    shortDescription: "",
    fullDescription: [""],
    points: [""],
    imageUrl: "",
    displayOrder: 0,
    published: false,
    metaTitle: "",
    metaDescription: "",
    keywords: [""],
};

function ServiceForm({
                         initialData,
                         onSubmit,
                         submitLabel,
                         isSubmitting,
                     }) {
    const [formData, setFormData] = useState(emptyForm);
    const [uploadingImage, setUploadingImage] = useState(false);
    const [uploadError, setUploadError] = useState("");

    useEffect(() => {
        if (!initialData) {
            setFormData(emptyForm);
            return;
        }

        setFormData({
            title: initialData.title || "",
            slug: initialData.slug || "",
            category: initialData.category || "",
            shortDescription: initialData.shortDescription || "",
            fullDescription:
                initialData.fullDescription?.length > 0
                    ? initialData.fullDescription
                    : [""],
            points:
                initialData.points?.length > 0
                    ? initialData.points
                    : [""],
            imageUrl: initialData.imageUrl || "",
            displayOrder: initialData.displayOrder ?? 0,
            published: Boolean(initialData.published),
            metaTitle: initialData.metaTitle || "",
            metaDescription: initialData.metaDescription || "",
            keywords:
                initialData.keywords?.length > 0
                    ? initialData.keywords
                    : [""],
        });
    }, [initialData]);

    function handleFieldChange(event) {
        const { name, value, type, checked } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: type === "checkbox" ? checked : value,
        }));
    }

    function updateArrayField(field, index, value) {
        setFormData((current) => ({
            ...current,
            [field]: current[field].map((item, itemIndex) =>
                itemIndex === index ? value : item
            ),
        }));
    }

    function addArrayItem(field) {
        setFormData((current) => ({
            ...current,
            [field]: [...current[field], ""],
        }));
    }

    function removeArrayItem(field, index) {
        setFormData((current) => {
            const remaining = current[field].filter(
                (_, itemIndex) => itemIndex !== index
            );

            return {
                ...current,
                [field]: remaining.length > 0 ? remaining : [""],
            };
        });
    }

    async function handleImageUpload(event) {
        const file = event.target.files?.[0];

        if (!file) {
            return;
        }

        setUploadingImage(true);
        setUploadError("");

        try {
            const result = await uploadServiceImage(file);

            setFormData((current) => ({
                ...current,
                imageUrl: result.url,
            }));
        } catch (error) {
            console.error(error);
            setUploadError(error.message || "Image upload failed.");
        } finally {
            setUploadingImage(false);
        }
    }

    function handleSubmit(event) {
        event.preventDefault();

        const cleanedData = {
            ...formData,
            displayOrder: Number(formData.displayOrder),
            fullDescription: formData.fullDescription.filter(
                (paragraph) => paragraph.trim() !== ""
            ),
            points: formData.points.filter(
                (point) => point.trim() !== ""
            ),
            keywords: formData.keywords.filter(
                (keyword) => keyword.trim() !== ""
            ),
        };

        onSubmit(cleanedData);
    }

    return (
        <form className="admin-service-form" onSubmit={handleSubmit}>
            <div className="admin-form-grid">
                <label>
                    <span>Title</span>
                    <input
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleFieldChange}
                        required
                    />
                </label>

                <label>
                    <span>Slug</span>
                    <input
                        type="text"
                        name="slug"
                        value={formData.slug}
                        onChange={handleFieldChange}
                        placeholder="anxiety-intrusive-thoughts"
                        required
                    />
                </label>

                <label>
                    <span>Category</span>
                    <input
                        type="text"
                        name="category"
                        value={formData.category}
                        onChange={handleFieldChange}
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
                <span>Short description</span>
                <textarea
                    name="shortDescription"
                    rows="4"
                    value={formData.shortDescription}
                    onChange={handleFieldChange}
                    required
                />
            </label>

            <div className="admin-array-field">
                <div className="admin-array-heading">
                    <h2>Full description</h2>

                    <button
                        type="button"
                        onClick={() => addArrayItem("fullDescription")}
                    >
                        Add paragraph
                    </button>
                </div>

                {formData.fullDescription.map((paragraph, index) => (
                    <div
                        className="admin-array-row"
                        key={`paragraph-${index}`}
                    >
                        <textarea
                            rows="4"
                            value={paragraph}
                            onChange={(event) =>
                                updateArrayField(
                                    "fullDescription",
                                    index,
                                    event.target.value
                                )
                            }
                        />

                        <button
                            type="button"
                            onClick={() =>
                                removeArrayItem("fullDescription", index)
                            }
                        >
                            Remove
                        </button>
                    </div>
                ))}
            </div>

            <div className="admin-array-field">
                <div className="admin-array-heading">
                    <h2>Checklist points</h2>

                    <button
                        type="button"
                        onClick={() => addArrayItem("points")}
                    >
                        Add point
                    </button>
                </div>

                {formData.points.map((point, index) => (
                    <div
                        className="admin-array-row"
                        key={`point-${index}`}
                    >
                        <input
                            type="text"
                            value={point}
                            onChange={(event) =>
                                updateArrayField(
                                    "points",
                                    index,
                                    event.target.value
                                )
                            }
                        />

                        <button
                            type="button"
                            onClick={() =>
                                removeArrayItem("points", index)
                            }
                        >
                            Remove
                        </button>
                    </div>
                ))}
            </div>

            <div className="admin-image-field">
                <label>
                    <span>Upload image</span>
                    <input
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        onChange={handleImageUpload}
                        disabled={uploadingImage}
                    />
                </label>

                {uploadingImage && <p>Uploading image...</p>}

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
                        placeholder="/uploads/services/image.jpg"
                    />
                </label>

                {formData.imageUrl && (
                    <img
                        className="admin-service-image-preview"
                        src={
                            formData.imageUrl.startsWith("/uploads/")
                                ? `http://localhost:8080${formData.imageUrl}`
                                : formData.imageUrl
                        }
                        alt="Service preview"
                    />
                )}
            </div>

            <div className="admin-array-field">
                <div className="admin-array-heading">
                    <h2>SEO keywords</h2>

                    <button
                        type="button"
                        onClick={() => addArrayItem("keywords")}
                    >
                        Add keyword
                    </button>
                </div>

                {formData.keywords.map((keyword, index) => (
                    <div
                        className="admin-array-row"
                        key={`keyword-${index}`}
                    >
                        <input
                            type="text"
                            value={keyword}
                            onChange={(event) =>
                                updateArrayField(
                                    "keywords",
                                    index,
                                    event.target.value
                                )
                            }
                        />

                        <button
                            type="button"
                            onClick={() =>
                                removeArrayItem("keywords", index)
                            }
                        >
                            Remove
                        </button>
                    </div>
                ))}
            </div>

            <label>
                <span>Meta title</span>
                <input
                    type="text"
                    name="metaTitle"
                    maxLength="255"
                    value={formData.metaTitle}
                    onChange={handleFieldChange}
                />
            </label>

            <label>
                <span>Meta description</span>
                <textarea
                    name="metaDescription"
                    rows="4"
                    value={formData.metaDescription}
                    onChange={handleFieldChange}
                />
            </label>

            <label className="admin-checkbox-field">
                <input
                    type="checkbox"
                    name="published"
                    checked={formData.published}
                    onChange={handleFieldChange}
                />

                <span>Publish this service</span>
            </label>

            <button
                className="admin-primary-button"
                type="submit"
                disabled={isSubmitting || uploadingImage}
            >
                {isSubmitting ? "Saving..." : submitLabel}
            </button>
        </form>
    );
}

export default ServiceForm;