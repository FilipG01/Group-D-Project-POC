import { getImageUrl } from "../../../utils/imageUrl.js";

/*
 * Reusable image upload panel for blog editors.
 * The parent controls the image value and upload actions.
 */
function BlogImageUpload({
                             imageUrl,
                             canEdit,
                             isUploading,
                             uploadLabel = "Upload Image",
                             previewAlt = "Blog featured image preview",
                             uploadInputId = "blog-image-upload",

                             panelClassName = "therapist-blog-editor-panel",
                             imageClassName = "therapist-blog-image-preview",
                             placeholderClassName = "therapist-blog-image-placeholder",
                             uploadButtonClassName =
                             "therapist-editor-secondary-button therapist-blog-upload-button",
                             removeButtonClassName =
                             "therapist-blog-remove-image",

                             onUpload,
                             onRemove,
                         }) {
    const previewImage = getImageUrl(imageUrl);

    return (
        <section className={panelClassName}>
            <h2>Featured image</h2>

            {/* Show the uploaded image when one has been selected. */}
            {previewImage ? (
                <img
                    src={previewImage}
                    alt={previewAlt}
                    className={imageClassName}
                />
            ) : (
                <div className={placeholderClassName}>
                    No image selected
                </div>
            )}

            {canEdit && (
                <>
                    <label
                        htmlFor={uploadInputId}
                        className={uploadButtonClassName}
                    >
                        {isUploading
                            ? "Uploading..."
                            : uploadLabel}
                    </label>

                    <input
                        id={uploadInputId}
                        type="file"
                        accept="image/jpeg,image/png,image/webp"
                        onChange={onUpload}
                        disabled={isUploading}
                        hidden
                    />

                    {imageUrl && (
                        <button
                            type="button"
                            className={removeButtonClassName}
                            onClick={onRemove}
                        >
                            Remove image
                        </button>
                    )}
                </>
            )}
        </section>
    );
}

export default BlogImageUpload;