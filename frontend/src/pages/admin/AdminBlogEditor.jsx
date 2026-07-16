import { Link } from "react-router-dom";

import BlogImageUpload from "../../components/blog/shared/BlogImageUpload.jsx";
import BlogSeoFields from "../../components/blog/shared/BlogSeoFields.jsx";

import useAdminBlogEditor from "../../hooks/blog/useAdminBlogEditor.js";

import { formatBlogStatus } from "../../utils/blog/blogFormatters.js";

import "../../styles/blog/adminBlogEditor.css";

function AdminBlogEditor() {
    const {
        form,
        post,

        isCreating,
        isLoading,
        isSaving,
        isUploading,
        isPublishing,

        error,
        successMessage,

        handleChange,
        handleSave,
        handleImageUpload,
        handlePublish,
        removeImage,
    } = useAdminBlogEditor();

    if (isLoading) {
        return (
            <main className="admin-blog-editor-page">
                <div className="admin-blog-editor-status">
                    Loading blog post...
                </div>
            </main>
        );
    }

    if (error && !isCreating && !post) {
        return (
            <main className="admin-blog-editor-page">
                <div className="admin-blog-editor-status">
                    <h1>Unable to load post</h1>

                    <p>{error}</p>

                    <Link
                        to="/admin/blog"
                        className="admin-editor-secondary-button"
                    >
                        Return to Blog Management
                    </Link>
                </div>
            </main>
        );
    }

    return (
        <main className="admin-blog-editor-page">
            <header className="admin-blog-editor-header">
                <div>
                    <Link
                        to="/admin/blog"
                        className="admin-blog-editor-back"
                    >
                        ← Blog Management
                    </Link>

                    <p className="section-label">
                        Admin dashboard
                    </p>

                    <h1>
                        {isCreating
                            ? "Create Blog Post"
                            : "Edit Blog Post"}
                    </h1>

                    {!isCreating && post && (
                        <div className="admin-blog-editor-meta">
                            <span>
                                Status:{" "}
                                <strong>
                                    {formatBlogStatus(
                                        post.status
                                    )}
                                </strong>
                            </span>

                            <span>
                                Author:{" "}
                                <strong>
                                    {post.author?.name}
                                </strong>
                            </span>

                            <span>
                                Version:{" "}
                                <strong>
                                    {post.version}
                                </strong>
                            </span>
                        </div>
                    )}
                </div>

                {!isCreating &&
                    post?.status === "PUBLISHED" && (
                        <Link
                            to={`/blog/${post.slug}`}
                            className="admin-editor-secondary-button"
                        >
                            View Published Post
                        </Link>
                    )}
            </header>

            {successMessage && (
                <div
                    className="admin-blog-editor-message admin-blog-editor-success"
                    role="status"
                >
                    {successMessage}
                </div>
            )}

            {error && (
                <div
                    className="admin-blog-editor-message admin-blog-editor-error"
                    role="alert"
                >
                    {error}
                </div>
            )}

            <form
                className="admin-blog-editor-form"
                onSubmit={handleSave}
            >
                <section className="admin-blog-editor-main">
                    <div className="admin-editor-field">
                        <label htmlFor="title">
                            Title
                        </label>

                        <input
                            id="title"
                            name="title"
                            type="text"
                            value={form.title}
                            onChange={handleChange}
                            maxLength={220}
                            required
                        />

                        <span>
                            {form.title.length}/220
                        </span>
                    </div>

                    <div className="admin-editor-field">
                        <label htmlFor="slug">
                            Slug
                        </label>

                        <input
                            id="slug"
                            name="slug"
                            type="text"
                            value={form.slug}
                            onChange={handleChange}
                            maxLength={220}
                            required={!isCreating}
                            placeholder={
                                isCreating
                                    ? "Leave blank to generate from title"
                                    : ""
                            }
                        />

                        <span>
                            URL-safe formatting will be
                            applied by the backend.
                        </span>
                    </div>

                    <div className="admin-editor-field">
                        <label htmlFor="summary">
                            Summary
                        </label>

                        <textarea
                            id="summary"
                            name="summary"
                            value={form.summary}
                            onChange={handleChange}
                            maxLength={500}
                            rows={4}
                        />

                        <span>
                            {form.summary.length}/500
                        </span>
                    </div>

                    <div className="admin-editor-field">
                        <label htmlFor="body">
                            Article body
                        </label>

                        <textarea
                            id="body"
                            name="body"
                            value={form.body}
                            onChange={handleChange}
                            rows={22}
                            placeholder="Write the article here. Separate paragraphs with a blank line."
                        />
                    </div>
                </section>

                <aside className="admin-blog-editor-sidebar">
                    <BlogImageUpload
                        imageUrl={form.featuredImageUrl}
                        canEdit={true}
                        isUploading={isUploading}
                        uploadInputId="admin-blog-image"
                        uploadLabel="Upload Image"
                        previewAlt="Featured image preview"
                        onUpload={handleImageUpload}
                        onRemove={removeImage}
                        panelClassName="admin-blog-editor-panel"
                        imageClassName="admin-blog-image-preview"
                        placeholderClassName="admin-blog-image-placeholder"
                        uploadButtonClassName="admin-editor-secondary-button admin-blog-upload-button"
                        removeButtonClassName="admin-blog-remove-image"
                    />

                    <BlogSeoFields
                        form={form}
                        isEditable={true}
                        onChange={handleChange}
                        panelClassName="admin-blog-editor-panel"
                        fieldClassName="admin-editor-field"
                    />

                    <section className="admin-blog-editor-actions">
                        <button
                            type="submit"
                            className="admin-editor-primary-button"
                            disabled={
                                isSaving ||
                                isUploading ||
                                isPublishing
                            }
                        >
                            {isSaving
                                ? "Saving..."
                                : isCreating
                                    ? "Create Draft"
                                    : "Save Changes"}
                        </button>

                        {!isCreating &&
                            post?.status !== "PUBLISHED" &&
                            post?.status !== "ARCHIVED" && (
                                <button
                                    type="button"
                                    className="admin-editor-publish-button"
                                    disabled={
                                        isSaving ||
                                        isUploading ||
                                        isPublishing
                                    }
                                    onClick={handlePublish}
                                >
                                    {isPublishing
                                        ? "Publishing..."
                                        : "Publish"}
                                </button>
                            )}
                    </section>
                </aside>
            </form>
        </main>
    );
}

export default AdminBlogEditor;