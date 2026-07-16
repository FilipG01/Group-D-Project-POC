import {
    useEffect,
    useMemo,
    useState,
} from "react";
import {
    Link,
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    getAdminBlogPost,
    publishAdminBlogPost,
    rejectAdminBlogPost,
    requestChangesForAdminBlogPost,
} from "../../api/adminBlogApi.js";

import { getImageUrl } from "../../utils/imageUrl.js";

import "../../styles/adminBlogReview.css";

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

function splitBodyIntoParagraphs(body) {
    if (!body) {
        return [];
    }

    return body
        .split(/\n\s*\n/)
        .map((paragraph) => paragraph.trim())
        .filter(Boolean);
}

function AdminBlogReview() {
    const { postId } = useParams();
    const navigate = useNavigate();

    const [post, setPost] = useState(null);
    const [reviewNote, setReviewNote] =
        useState("");

    const [isLoading, setIsLoading] =
        useState(true);

    const [isWorking, setIsWorking] =
        useState(false);

    const [error, setError] = useState("");

    useEffect(() => {
        let isCurrent = true;

        async function loadPost() {
            setIsLoading(true);
            setError("");

            try {
                const data =
                    await getAdminBlogPost(postId);

                if (!isCurrent) {
                    return;
                }

                setPost(data);
                setReviewNote(
                    data.reviewNotes || ""
                );
            } catch (requestError) {
                console.error(requestError);

                if (isCurrent) {
                    setError(
                        requestError.message ||
                        "The blog post could not be loaded."
                    );
                }
            } finally {
                if (isCurrent) {
                    setIsLoading(false);
                }
            }
        }

        loadPost();

        return () => {
            isCurrent = false;
        };
    }, [postId]);

    const paragraphs = useMemo(
        () => splitBodyIntoParagraphs(post?.body),
        [post?.body]
    );

    async function handlePublish() {
        const confirmed = window.confirm(
            `Publish "${post.title}" now?`
        );

        if (!confirmed) {
            return;
        }

        setIsWorking(true);
        setError("");

        try {
            await publishAdminBlogPost(
                post.id,
                post.version
            );

            navigate("/admin/blog", {
                replace: true,
            });
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The post could not be published."
            );
        } finally {
            setIsWorking(false);
        }
    }

    async function handleRequestChanges() {
        const note = reviewNote.trim();

        if (!note) {
            setError(
                "Enter review feedback before requesting changes."
            );

            return;
        }

        setIsWorking(true);
        setError("");

        try {
            await requestChangesForAdminBlogPost(
                post.id,
                note,
                post.version
            );

            navigate("/admin/blog", {
                replace: true,
            });
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "Changes could not be requested."
            );
        } finally {
            setIsWorking(false);
        }
    }

    async function handleReject() {
        const note = reviewNote.trim();

        if (!note) {
            setError(
                "Enter a reason before rejecting this post."
            );

            return;
        }

        const confirmed = window.confirm(
            `Reject "${post.title}"?`
        );

        if (!confirmed) {
            return;
        }

        setIsWorking(true);
        setError("");

        try {
            await rejectAdminBlogPost(
                post.id,
                note,
                post.version
            );

            navigate("/admin/blog", {
                replace: true,
            });
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The post could not be rejected."
            );
        } finally {
            setIsWorking(false);
        }
    }

    if (isLoading) {
        return (
            <main className="admin-blog-review-page">
                <div className="admin-blog-review-status">
                    Loading post...
                </div>
            </main>
        );
    }

    if (error && !post) {
        return (
            <main className="admin-blog-review-page">
                <div className="admin-blog-review-status">
                    <h1>Unable to load post</h1>
                    <p>{error}</p>

                    <Link
                        to="/admin/blog"
                        className="admin-blog-review-secondary-button"
                    >
                        Return to Blog Management
                    </Link>
                </div>
            </main>
        );
    }

    const imageUrl = getImageUrl(
        post.featuredImageUrl
    );

    const canReview =
        post.status === "SUBMITTED";

    const canPublish =
        !["PUBLISHED", "ARCHIVED"].includes(
            post.status
        );

    return (
        <main className="admin-blog-review-page">
            <header className="admin-blog-review-header">
                <div>
                    <Link
                        to="/admin/blog"
                        className="admin-blog-review-back"
                    >
                        ← Blog Management
                    </Link>

                    <p className="section-label">
                        Admin review
                    </p>

                    <h1>{post.title}</h1>

                    <div className="admin-blog-review-meta">
                        <span>
                            Status:{" "}
                            <strong>
                                {formatStatus(
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
                            Role:{" "}
                            <strong>
                                {post.author?.role}
                            </strong>
                        </span>
                    </div>
                </div>
            </header>

            {error && (
                <div
                    className="admin-blog-review-message admin-blog-review-error"
                    role="alert"
                >
                    {error}
                </div>
            )}

            <div className="admin-blog-review-layout">
                <article className="admin-blog-review-article">
                    {imageUrl && (
                        <img
                            src={imageUrl}
                            alt=""
                            className="admin-blog-review-image"
                        />
                    )}

                    <p className="admin-blog-review-summary">
                        {post.summary}
                    </p>

                    <div className="admin-blog-review-body">
                        {paragraphs.map(
                            (paragraph, index) => (
                                <p
                                    key={`${post.id}-${index}`}
                                >
                                    {paragraph}
                                </p>
                            )
                        )}
                    </div>
                </article>

                <aside className="admin-blog-review-sidebar">
                    <section className="admin-blog-review-panel">
                        <h2>Post information</h2>

                        <dl>
                            <div>
                                <dt>Slug</dt>
                                <dd>{post.slug}</dd>
                            </div>

                            <div>
                                <dt>SEO title</dt>
                                <dd>
                                    {post.seoTitle ||
                                        "Not provided"}
                                </dd>
                            </div>

                            <div>
                                <dt>
                                    SEO description
                                </dt>
                                <dd>
                                    {post.seoDescription ||
                                        "Not provided"}
                                </dd>
                            </div>

                            <div>
                                <dt>Keywords</dt>
                                <dd>
                                    {post.keywords
                                        ?.length
                                        ? post.keywords.join(
                                            ", "
                                        )
                                        : "None"}
                                </dd>
                            </div>
                        </dl>
                    </section>

                    {canReview && (
                        <section className="admin-blog-review-panel">
                            <h2>Review feedback</h2>

                            <label htmlFor="review-note">
                                Feedback for therapist
                            </label>

                            <textarea
                                id="review-note"
                                value={reviewNote}
                                onChange={(event) =>
                                    setReviewNote(
                                        event.target.value
                                    )
                                }
                                rows={8}
                                maxLength={2000}
                                placeholder="Explain what should be changed, or why the post is being rejected."
                            />

                            <span className="admin-blog-review-character-count">
                                {reviewNote.length}/2000
                            </span>
                        </section>
                    )}

                    <section className="admin-blog-review-actions">
                        {canPublish && (
                            <button
                                type="button"
                                className="admin-blog-review-primary-button"
                                disabled={isWorking}
                                onClick={handlePublish}
                            >
                                {isWorking
                                    ? "Working..."
                                    : "Publish"}
                            </button>
                        )}

                        {canReview && (
                            <>
                                <button
                                    type="button"
                                    className="admin-blog-review-warning-button"
                                    disabled={isWorking}
                                    onClick={
                                        handleRequestChanges
                                    }
                                >
                                    Request Changes
                                </button>

                                <button
                                    type="button"
                                    className="admin-blog-review-danger-button"
                                    disabled={isWorking}
                                    onClick={handleReject}
                                >
                                    Reject
                                </button>
                            </>
                        )}

                        {post.status ===
                            "PUBLISHED" && (
                                <Link
                                    to={`/blog/${post.slug}`}
                                    className="admin-blog-review-secondary-button"
                                >
                                    View Published Post
                                </Link>
                            )}
                    </section>
                </aside>
            </div>
        </main>
    );
}

export default AdminBlogReview;