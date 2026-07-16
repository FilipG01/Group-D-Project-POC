import { Link } from "react-router-dom";

import useTherapistBlogEditor from "../../hooks/blog/useTherapistBlogEditor.js";

import { formatBlogStatus } from "../../utils/blog/blogFormatters.js";

import BlogImageUpload from "../../components/blog/shared/BlogImageUpload.jsx";
import BlogSeoFields from "../../components/blog/shared/BlogSeoFields.jsx";
import BlogEditorFields from "../../components/blog/shared/BlogEditorFields.jsx";

import "../../styles/blog/therapistBlogEditor.css";



function TherapistBlogEditor() {
    const {
        form,
        post,

        isCreating,
        isEditable,
        isLoading,
        isSaving,
        isUploading,
        isSubmitting,

        error,
        successMessage,

        handleChange,
        handleSave,
        handleImageUpload,
        handleSubmitForReview,
        removeImage,
    } = useTherapistBlogEditor();

    if (isLoading) {
        return (
            <main className="therapist-blog-editor-page">
                <div className="therapist-blog-editor-status">
                    Loading post...
                </div>
            </main>
        );
    }

    if (error && !isCreating && !post) {
        return (
            <main className="therapist-blog-editor-page">
                <div className="therapist-blog-editor-status">
                    <h1>Unable to load post</h1>
                    <p>{error}</p>

                    <Link
                        to="/therapist/blog"
                        className="therapist-editor-secondary-button"
                    >
                        Return to Blog Posts
                    </Link>
                </div>
            </main>
        );
    }

    return (
        <main className="therapist-blog-editor-page">
            <header className="therapist-blog-editor-header">
                <div>
                    <Link
                        to="/therapist/blog"
                        className="therapist-blog-editor-back"
                    >
                        ← My Blog Posts
                    </Link>

                    <p className="section-label">
                        Therapist dashboard
                    </p>

                    <h1>
                        {isCreating
                            ? "Create Blog Post"
                            : isEditable
                                ? "Edit Blog Post"
                                : "View Blog Post"}
                    </h1>

                    {!isCreating && post && (
                        <div className="therapist-blog-editor-meta">
                            <span>
                                Status:{" "}
                                <strong>
                                    {formatBlogStatus(
                                        post.status
                                    )}
                                </strong>
                            </span>

                            <span>
                                Slug:{" "}
                                <strong>
                                    {post.slug}
                                </strong>
                            </span>
                        </div>
                    )}
                </div>

                {!isCreating &&
                    post?.status === "PUBLISHED" && (
                        <Link
                            to={`/blog/${post.slug}`}
                            className="therapist-editor-primary-button"
                        >
                            View Published Post
                        </Link>
                    )}
            </header>

            {post?.reviewNotes && (
                <section className="therapist-blog-editor-feedback">
                    <h2>Admin feedback</h2>
                    <p>{post.reviewNotes}</p>
                </section>
            )}

            {!isEditable && (
                <div className="therapist-blog-editor-locked">
                    {post?.status === "SUBMITTED" &&
                        "This post is awaiting admin review and cannot currently be edited."}

                    {post?.status === "PUBLISHED" &&
                        "Published posts can only be edited by an administrator."}

                    {post?.status === "ARCHIVED" &&
                        "This post has been archived by an administrator."}
                </div>
            )}

            {successMessage && (
                <div
                    className="therapist-blog-editor-message therapist-blog-editor-success"
                    role="status"
                >
                    {successMessage}
                </div>
            )}

            {error && (
                <div
                    className="therapist-blog-editor-message therapist-blog-editor-error"
                    role="alert"
                >
                    {error}
                </div>
            )}

            <form
                className="therapist-blog-editor-form"
                onSubmit={handleSave}
            >
                <BlogEditorFields
                    form={form}
                    isEditable={isEditable}
                    onChange={handleChange}
                />

                <aside className="therapist-blog-editor-sidebar">
                    <BlogImageUpload
                        imageUrl={form.featuredImageUrl}
                        canEdit={isEditable}
                        isUploading={isUploading}
                        uploadInputId="therapist-blog-image"
                        onUpload={handleImageUpload}
                        onRemove={removeImage}
                    />

                    <BlogSeoFields
                        form={form}
                        isEditable={isEditable}
                        onChange={handleChange}
                    />

                    {isEditable && (
                        <section className="therapist-blog-editor-actions">
                            <button
                                type="submit"
                                className="therapist-editor-primary-button"
                                disabled={
                                    isSaving ||
                                    isUploading ||
                                    isSubmitting
                                }
                            >
                                {isSaving
                                    ? "Saving..."
                                    : isCreating
                                        ? "Create Draft"
                                        : "Save Draft"}
                            </button>

                            {!isCreating && (
                                <button
                                    type="button"
                                    className="therapist-editor-submit-button"
                                    disabled={
                                        isSaving ||
                                        isUploading ||
                                        isSubmitting
                                    }
                                    onClick={handleSubmitForReview}
                                >
                                    {isSubmitting
                                        ? "Submitting..."
                                        : "Submit for Review"}
                                </button>
                            )}
                        </section>
                    )}
                </aside>
            </form>
        </main>
    );
}

export default TherapistBlogEditor;