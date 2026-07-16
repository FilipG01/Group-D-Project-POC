import {
    useCallback,
    useEffect,
    useState,
} from "react";
import { Link } from "react-router-dom";

import {
    archiveAdminBlogPost,
    getAdminBlogPosts,
    publishAdminBlogPost,
    restoreAdminBlogPost,
    setAdminBlogPostFeatured,
    unpublishAdminBlogPost,
} from "../../api/adminBlogApi.js";

import "../../styles/blog/adminBlog.css";

const STATUS_OPTIONS = [
    {
        value: "",
        label: "All posts",
    },
    {
        value: "SUBMITTED",
        label: "Submitted",
    },
    {
        value: "DRAFT",
        label: "Drafts",
    },
    {
        value: "CHANGES_REQUESTED",
        label: "Changes Requested",
    },
    {
        value: "REJECTED",
        label: "Rejected",
    },
    {
        value: "PUBLISHED",
        label: "Published",
    },
    {
        value: "ARCHIVED",
        label: "Archived",
    },
];

function formatStatus(status) {
    if (!status) {
        return "";
    }

    return status
        .toLowerCase()
        .split("_")
        .map(
            (word) =>
                word.charAt(0).toUpperCase() +
                word.slice(1)
        )
        .join(" ");
}

function formatDate(value) {
    if (!value) {
        return "Not available";
    }

    return new Intl.DateTimeFormat("en-IE", {
        day: "numeric",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(new Date(value));
}

function getStatusClassName(status) {
    return `admin-blog-status admin-blog-status-${status
        .toLowerCase()
        .replaceAll("_", "-")}`;
}

function AdminBlogPosts() {
    const [posts, setPosts] = useState([]);
    const [pageData, setPageData] = useState(null);

    const [statusFilter, setStatusFilter] =
        useState("SUBMITTED");

    const [page, setPage] = useState(0);

    const [isLoading, setIsLoading] =
        useState(true);

    const [actionPostId, setActionPostId] =
        useState(null);

    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] =
        useState("");

    const loadPosts = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data = await getAdminBlogPosts({
                status: statusFilter,
                page,
                size: 20,
            });

            setPosts(data.content || []);
            setPageData(data);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The blog posts could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, [statusFilter, page]);

    useEffect(() => {
        /* eslint-disable react-hooks/set-state-in-effect */
        loadPosts();
        /* eslint-enable react-hooks/set-state-in-effect */
    }, [loadPosts]);

    function handleStatusChange(event) {
        setStatusFilter(event.target.value);
        setPage(0);
        setSuccessMessage("");
    }

    async function runAction(
        post,
        action,
        successText
    ) {
        setActionPostId(post.id);
        setError("");
        setSuccessMessage("");

        try {
            await action();

            setSuccessMessage(successText);

            await loadPosts();
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The action could not be completed."
            );
        } finally {
            setActionPostId(null);
        }
    }

    async function handlePublish(post) {
        const confirmed = window.confirm(
            `Publish "${post.title}" now?`
        );

        if (!confirmed) {
            return;
        }

        await runAction(
            post,
            () =>
                publishAdminBlogPost(
                    post.id,
                    post.version
                ),
            `"${post.title}" was published.`
        );
    }

    async function handleUnpublish(post) {
        const note =
            window.prompt(
                "Optional note for unpublishing:"
            ) ?? null;

        if (note === null) {
            return;
        }

        await runAction(
            post,
            () =>
                unpublishAdminBlogPost(
                    post.id,
                    post.version,
                    note
                ),
            `"${post.title}" was unpublished.`
        );
    }

    async function handleArchive(post) {
        const note =
            window.prompt(
                "Optional archive note:"
            ) ?? null;

        if (note === null) {
            return;
        }

        await runAction(
            post,
            () =>
                archiveAdminBlogPost(
                    post.id,
                    post.version,
                    note
                ),
            `"${post.title}" was archived.`
        );
    }

    async function handleRestore(post) {
        const note =
            window.prompt(
                "Optional restore note:"
            ) ?? null;

        if (note === null) {
            return;
        }

        await runAction(
            post,
            () =>
                restoreAdminBlogPost(
                    post.id,
                    post.version,
                    note
                ),
            `"${post.title}" was restored as a draft.`
        );
    }

    async function handleFeature(post) {
        const nextFeatured = !post.featured;

        await runAction(
            post,
            () =>
                setAdminBlogPostFeatured(
                    post.id,
                    nextFeatured,
                    post.version
                ),
            nextFeatured
                ? `"${post.title}" is now featured.`
                : `"${post.title}" is no longer featured.`
        );
    }

    return (
        <main className="admin-blog-page">
            <header className="admin-blog-header">
                <div>
                    <p className="section-label">
                        Admin dashboard
                    </p>

                    <h1>Blog Management</h1>

                    <p>
                        Review therapist submissions, publish
                        articles, manage visibility and control
                        featured content.
                    </p>
                </div>

                <div className="admin-blog-header-actions">
                    <Link
                        to="/admin"
                        className="admin-blog-secondary-button"
                    >
                        ← Dashboard
                    </Link>
                    <Link
                        to="/admin/blog/new"
                        className="admin-blog-primary-button"
                    >
                        Create Post
                    </Link>
                </div>
            </header>

            <section className="admin-blog-toolbar">
                <div>
                    <label htmlFor="blog-status-filter">
                        Filter by status
                    </label>

                    <select
                        id="blog-status-filter"
                        value={statusFilter}
                        onChange={handleStatusChange}
                    >
                        {STATUS_OPTIONS.map((option) => (
                            <option
                                key={
                                    option.value ||
                                    "all"
                                }
                                value={option.value}
                            >
                                {option.label}
                            </option>
                        ))}
                    </select>
                </div>

                <button
                    type="button"
                    className="admin-blog-secondary-button"
                    onClick={loadPosts}
                    disabled={isLoading}
                >
                    Refresh
                </button>
            </section>

            {successMessage && (
                <div
                    className="admin-blog-message admin-blog-success"
                    role="status"
                >
                    {successMessage}
                </div>
            )}

            {error && (
                <div
                    className="admin-blog-message admin-blog-error"
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
                <div className="admin-blog-loading">
                    Loading blog posts...
                </div>
            )}

            {!isLoading &&
                !error &&
                posts.length === 0 && (
                    <section className="admin-blog-empty">
                        <h2>No matching posts</h2>

                        <p>
                            There are currently no posts with
                            this status.
                        </p>
                    </section>
                )}

            {!isLoading && posts.length > 0 && (
                <section className="admin-blog-list">
                    {posts.map((post) => {
                        const isActionRunning =
                            actionPostId === post.id;

                        return (
                            <article
                                key={post.id}
                                className="admin-blog-card"
                            >
                                <div className="admin-blog-card-main">
                                    <div className="admin-blog-card-heading">
                                        <span
                                            className={getStatusClassName(
                                                post.status
                                            )}
                                        >
                                            {formatStatus(
                                                post.status
                                            )}
                                        </span>

                                        {post.featured && (
                                            <span className="admin-blog-featured-label">
                                                Featured
                                            </span>
                                        )}

                                        <h2>{post.title}</h2>
                                    </div>

                                    <p className="admin-blog-summary">
                                        {post.summary ||
                                            "No summary has been added."}
                                    </p>

                                    <dl className="admin-blog-details">
                                        <div>
                                            <dt>Author</dt>
                                            <dd>
                                                {
                                                    post.author
                                                        ?.name
                                                }
                                            </dd>
                                        </div>

                                        <div>
                                            <dt>Role</dt>
                                            <dd>
                                                {
                                                    post.author
                                                        ?.role
                                                }
                                            </dd>
                                        </div>

                                        <div>
                                            <dt>Last updated</dt>
                                            <dd>
                                                {formatDate(
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
                                                    {formatDate(
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
                                                    {formatDate(
                                                        post.publishedAt
                                                    )}
                                                </dd>
                                            </div>
                                        )}
                                    </dl>

                                    {post.reviewNotes && (
                                        <div className="admin-blog-review-note">
                                            <strong>
                                                Current review note
                                            </strong>

                                            <p>
                                                {
                                                    post.reviewNotes
                                                }
                                            </p>
                                        </div>
                                    )}
                                </div>

                                <div className="admin-blog-card-actions">
                                    <Link
                                        to={`/admin/blog/${post.id}`}
                                        className="admin-blog-secondary-button"
                                    >
                                        Review
                                    </Link>

                                    <Link
                                        to={`/admin/blog/${post.id}/edit`}
                                        className="admin-blog-secondary-button"
                                    >
                                        Edit
                                    </Link>

                                    {post.status !== "PUBLISHED" &&
                                        post.status !== "ARCHIVED" && (
                                            <button
                                                type="button"
                                                className="admin-blog-primary-button"
                                                disabled={isActionRunning}
                                                onClick={() =>
                                                    handlePublish(post)
                                                }
                                            >
                                                Publish
                                            </button>
                                        )}

                                    {post.status === "PUBLISHED" && (
                                        <>
                                            <button
                                                type="button"
                                                className="admin-blog-secondary-button"
                                                disabled={isActionRunning}
                                                onClick={() =>
                                                    handleFeature(post)
                                                }
                                            >
                                                {post.featured
                                                    ? "Unfeature"
                                                    : "Feature"}
                                            </button>

                                            <button
                                                type="button"
                                                className="admin-blog-warning-button"
                                                disabled={isActionRunning}
                                                onClick={() =>
                                                    handleUnpublish(post)
                                                }
                                            >
                                                Unpublish
                                            </button>

                                            <Link
                                                to={`/blog/${post.slug}`}
                                                className="admin-blog-secondary-button"
                                            >
                                                View Live
                                            </Link>
                                        </>
                                    )}

                                    {post.status !== "ARCHIVED" && (
                                        <button
                                            type="button"
                                            className="admin-blog-danger-button"
                                            disabled={isActionRunning}
                                            onClick={() =>
                                                handleArchive(post)
                                            }
                                        >
                                            Archive
                                        </button>
                                    )}

                                    {post.status === "ARCHIVED" && (
                                        <button
                                            type="button"
                                            className="admin-blog-primary-button"
                                            disabled={isActionRunning}
                                            onClick={() =>
                                                handleRestore(post)
                                            }
                                        >
                                            Restore
                                        </button>
                                    )}

                                    {isActionRunning && (
                                        <span className="admin-blog-action-note">
            Updating...
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
                        className="admin-blog-pagination"
                        aria-label="Admin blog pagination"
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

export default AdminBlogPosts;