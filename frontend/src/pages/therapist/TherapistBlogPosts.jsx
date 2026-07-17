import {
    useCallback,
    useEffect,
    useState,
} from "react";
import { Link } from "react-router-dom";

import {
    getTherapistBlogPosts,
    submitTherapistBlogPost,
} from "../../api/therapistBlogApi.js";

import {
    formatBlogStatus,
    formatBlogDateTime,
} from "../../utils/blog/blogFormatters.js";

import "../../styles/blog/therapistBlog.css";

const EDITABLE_STATUSES = new Set([
    "DRAFT",
    "CHANGES_REQUESTED",
    "REJECTED",
]);

const SUBMITTABLE_STATUSES = new Set([
    "DRAFT",
    "CHANGES_REQUESTED",
    "REJECTED",
]);

function getStatusClassName(status) {
    return `therapist-blog-status therapist-blog-status-${status
        .toLowerCase()
        .replaceAll("_", "-")}`;
}

function TherapistBlogPosts() {
    const [posts, setPosts] = useState([]);
    const [pageData, setPageData] = useState(null);
    const [page, setPage] = useState(0);

    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] =
        useState("");

    const [submittingPostId, setSubmittingPostId] =
        useState(null);

    const loadPosts = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data =
                await getTherapistBlogPosts({
                    page,
                    size: 20,
                });

            setPosts(data.content || []);
            setPageData(data);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "Your blog posts could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, [page]);

    useEffect(() => {
        /* eslint-disable react-hooks/set-state-in-effect */
        loadPosts();
        /* eslint-enable react-hooks/set-state-in-effect */
    }, [loadPosts]);

    async function handleSubmit(post) {
        const confirmed = window.confirm(
            `Submit "${post.title}" for admin review? You will not be able to edit it until an admin requests changes or rejects it.`
        );

        if (!confirmed) {
            return;
        }

        setSubmittingPostId(post.id);
        setError("");
        setSuccessMessage("");

        try {
            await submitTherapistBlogPost(
                post.id,
                post.version
            );

            setSuccessMessage(
                `"${post.title}" was submitted for admin review.`
            );

            await loadPosts();
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The post could not be submitted."
            );
        } finally {
            setSubmittingPostId(null);
        }
    }

    return (
        <main className="therapist-blog-page">
            <header className="therapist-blog-header">
                <div>
                    <p className="section-label">
                        Therapist dashboard
                    </p>

                    <h1>My Blog Posts</h1>

                    <p>
                        Create articles, manage your drafts and
                        submit completed posts for admin review.
                    </p>
                </div>

                <div className="therapist-blog-header-actions">
                    <Link
                        to="/therapist"
                        className="therapist-blog-secondary-button"
                    >
                        ← Dashboard
                    </Link>

                    <Link
                        to="/therapist/blog/new"
                        className="therapist-blog-primary-button"
                    >
                        Create Post
                    </Link>
                </div>
            </header>

            {successMessage && (
                <div
                    className="therapist-blog-message therapist-blog-success"
                    role="status"
                >
                    {successMessage}
                </div>
            )}

            {error && (
                <div
                    className="therapist-blog-message therapist-blog-error"
                    role="alert"
                >
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={loadPosts}
                    >
                        Try again
                    </button>
                </div>
            )}

            {isLoading && (
                <div className="therapist-blog-loading">
                    Loading your posts...
                </div>
            )}

            {!isLoading &&
                !error &&
                posts.length === 0 && (
                    <section className="therapist-blog-empty">
                        <h2>You have not created any posts yet</h2>

                        <p>
                            Create your first draft and submit it
                            when it is ready for review.
                        </p>

                        <Link
                            to="/therapist/blog/new"
                            className="therapist-blog-primary-button"
                        >
                            Create Your First Post
                        </Link>
                    </section>
                )}

            {!isLoading && posts.length > 0 && (
                <section className="therapist-blog-list">
                    {posts.map((post) => {
                        const canEdit =
                            EDITABLE_STATUSES.has(
                                post.status
                            );

                        const isSubmittable =
                            SUBMITTABLE_STATUSES.has(
                                post.status
                            );

                        const isSubmitting =
                            submittingPostId === post.id;

                        return (
                            <article
                                key={post.id}
                                className="therapist-blog-card"
                            >
                                <div className="therapist-blog-card-main">
                                    <div className="therapist-blog-card-heading">
                                        <span
                                            className={getStatusClassName(
                                                post.status
                                            )}
                                        >
                                            formatBlogStatus(post.status)
                                        </span>

                                        <h2>{post.title}</h2>
                                    </div>

                                    <p className="therapist-blog-summary">
                                        {post.summary ||
                                            "No summary has been added yet."}
                                    </p>

                                    <dl className="therapist-blog-details">
                                        <div>
                                            <dt>Slug</dt>
                                            <dd>
                                                {post.slug}
                                            </dd>
                                        </div>

                                        <div>
                                            <dt>Last updated</dt>
                                            <dd>
                                                {formatBlogDateTime(
                                                    post.updatedAt
                                                )}
                                            </dd>
                                        </div>

                                        {post.submittedAt && (
                                            <div>
                                                <dt>
                                                    Submitted
                                                </dt>
                                                <dd>
                                                    {formatBlogDateTime(
                                                        post.submittedAt
                                                    )}
                                                </dd>
                                            </div>
                                        )}

                                        {post.publishedAt && (
                                            <div>
                                                <dt>
                                                    Published
                                                </dt>
                                                <dd>
                                                    {formatBlogDateTime(
                                                        post.publishedAt
                                                    )}
                                                </dd>
                                            </div>
                                        )}
                                    </dl>

                                    {post.reviewNotes && (
                                        <div className="therapist-blog-review-note">
                                            <strong>
                                                Admin feedback
                                            </strong>

                                            <p>
                                                {
                                                    post.reviewNotes
                                                }
                                            </p>
                                        </div>
                                    )}
                                </div>

                                <div className="therapist-blog-card-actions">
                                    {canEdit ? (
                                        <Link
                                            to={`/therapist/blog/${post.id}/edit`}
                                            className="therapist-blog-secondary-button"
                                        >
                                            Edit
                                        </Link>
                                    ) : (
                                        <Link
                                            to={`/therapist/blog/${post.id}/edit`}
                                            className="therapist-blog-secondary-button"
                                        >
                                            View
                                        </Link>
                                    )}

                                    {isSubmittable && (
                                        <button
                                            type="button"
                                            className="therapist-blog-primary-button"
                                            disabled={
                                                isSubmitting
                                            }
                                            onClick={() =>
                                                handleSubmit(
                                                    post
                                                )
                                            }
                                        >
                                            {isSubmitting
                                                ? "Submitting..."
                                                : "Submit for Review"}
                                        </button>
                                    )}

                                    {post.status ===
                                        "SUBMITTED" && (
                                            <span className="therapist-blog-action-note">
                                            Awaiting admin review
                                        </span>
                                        )}

                                    {post.status ===
                                        "PUBLISHED" && (
                                            <Link
                                                to={`/blog/${post.slug}`}
                                                className="therapist-blog-primary-button"
                                            >
                                                View Published Post
                                            </Link>
                                        )}

                                    {post.status ===
                                        "ARCHIVED" && (
                                            <span className="therapist-blog-action-note">
                                            Archived by admin
                                        </span>
                                        )}
                                </div>
                            </article>
                        );
                    })}
                </section>
            )}

            {!isLoading &&
                pageData &&
                pageData.totalPages > 1 && (
                    <nav
                        className="therapist-blog-pagination"
                        aria-label="Therapist blog pagination"
                    >
                        <button
                            type="button"
                            disabled={pageData.first}
                            onClick={() =>
                                setPage((current) =>
                                    Math.max(
                                        current - 1,
                                        0
                                    )
                                )
                            }
                        >
                            ← Previous
                        </button>

                        <span>
                            Page {page + 1} of{" "}
                            {pageData.totalPages}
                        </span>

                        <button
                            type="button"
                            disabled={pageData.last}
                            onClick={() =>
                                setPage(
                                    (current) =>
                                        current + 1
                                )
                            }
                        >
                            Next →
                        </button>
                    </nav>
                )}
        </main>
    );
}

export default TherapistBlogPosts;