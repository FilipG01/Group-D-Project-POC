import {
    useCallback,
    useEffect,
    useState,
} from "react";
import { Link } from "react-router-dom";

import {
    getAdminBlogPosts,
    reorderAdminBlogPosts,
} from "../../api/adminBlogApi.js";

import { getImageUrl } from "../../utils/imageUrl.js";

import "../../styles/adminBlogReorder.css";

function AdminBlogReorder() {
    const [posts, setPosts] = useState([]);

    const [isLoading, setIsLoading] =
        useState(true);

    const [isSaving, setIsSaving] =
        useState(false);

    const [error, setError] = useState("");

    const [successMessage, setSuccessMessage] =
        useState("");

    const [hasChanges, setHasChanges] =
        useState(false);

    const loadPosts = useCallback(async () => {
        setIsLoading(true);
        setError("");
        setSuccessMessage("");

        try {
            const data = await getAdminBlogPosts({
                status: "PUBLISHED",
                page: 0,
                size: 100,
            });

            const orderedPosts = [
                ...(data.content || []),
            ].sort((first, second) => {
                const firstOrder =
                    first.displayOrder ?? 0;

                const secondOrder =
                    second.displayOrder ?? 0;

                if (firstOrder !== secondOrder) {
                    return firstOrder - secondOrder;
                }

                return new Date(
                        second.publishedAt || 0
                    ) -
                    new Date(
                        first.publishedAt || 0
                    );
            });

            setPosts(orderedPosts);
            setHasChanges(false);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "Published posts could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        /* eslint-disable react-hooks/set-state-in-effect */
        loadPosts();
        /* eslint-enable react-hooks/set-state-in-effect */
    }, [loadPosts]);

    function movePost(index, direction) {
        const nextIndex = index + direction;

        if (
            nextIndex < 0 ||
            nextIndex >= posts.length
        ) {
            return;
        }

        setPosts((currentPosts) => {
            const reordered = [...currentPosts];

            [
                reordered[index],
                reordered[nextIndex],
            ] = [
                reordered[nextIndex],
                reordered[index],
            ];

            return reordered;
        });

        setHasChanges(true);
        setSuccessMessage("");
    }

    async function handleSaveOrder() {
        if (!hasChanges) {
            return;
        }

        setIsSaving(true);
        setError("");
        setSuccessMessage("");

        try {
            const payload = posts.map(
                (post, index) => ({
                    id: post.id,
                    displayOrder: index + 1,
                    version: post.version,
                })
            );

            const response =
                await reorderAdminBlogPosts(
                    payload
                );

            const updatedPosts =
                response.posts || [];

            if (updatedPosts.length > 0) {
                const updatedById = new Map(
                    updatedPosts.map((post) => [
                        post.id,
                        post,
                    ])
                );

                setPosts((currentPosts) =>
                    currentPosts.map(
                        (currentPost) =>
                            updatedById.get(
                                currentPost.id
                            ) || currentPost
                    )
                );
            }

            setHasChanges(false);

            setSuccessMessage(
                "Published post order saved successfully."
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The post order could not be saved."
            );
        } finally {
            setIsSaving(false);
        }
    }

    return (
        <main className="admin-blog-reorder-page">
            <header className="admin-blog-reorder-header">
                <div>
                    <Link
                        to="/admin/blog"
                        className="admin-blog-reorder-back"
                    >
                        ← Blog Management
                    </Link>

                    <p className="section-label">
                        Admin dashboard
                    </p>

                    <h1>Reorder Published Posts</h1>

                    <p>
                        Move posts into the order they
                        should appear publicly. Featured
                        posts still appear before
                        non-featured posts.
                    </p>
                </div>

                <button
                    type="button"
                    className="admin-blog-reorder-save"
                    disabled={
                        !hasChanges || isSaving
                    }
                    onClick={handleSaveOrder}
                >
                    {isSaving
                        ? "Saving..."
                        : "Save Order"}
                </button>
            </header>

            {successMessage && (
                <div
                    className="admin-blog-reorder-message admin-blog-reorder-success"
                    role="status"
                >
                    {successMessage}
                </div>
            )}

            {error && (
                <div
                    className="admin-blog-reorder-message admin-blog-reorder-error"
                    role="alert"
                >
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={loadPosts}
                    >
                        Reload
                    </button>
                </div>
            )}

            {isLoading && (
                <div className="admin-blog-reorder-status">
                    Loading published posts...
                </div>
            )}

            {!isLoading &&
                !error &&
                posts.length === 0 && (
                    <div className="admin-blog-reorder-status">
                        <h2>No published posts</h2>

                        <p>
                            Publish at least one post before
                            setting the public order.
                        </p>
                    </div>
                )}

            {!isLoading && posts.length > 0 && (
                <section className="admin-blog-reorder-list">
                    {posts.map((post, index) => {
                        const imageUrl = getImageUrl(
                            post.featuredImageUrl
                        );

                        return (
                            <article
                                className="admin-blog-reorder-card"
                                key={post.id}
                            >
                                <div className="admin-blog-reorder-position">
                                    {index + 1}
                                </div>

                                {imageUrl ? (
                                    <img
                                        src={imageUrl}
                                        alt=""
                                        className="admin-blog-reorder-image"
                                    />
                                ) : (
                                    <div className="admin-blog-reorder-image admin-blog-reorder-placeholder">
                                        No image
                                    </div>
                                )}

                                <div className="admin-blog-reorder-content">
                                    <div className="admin-blog-reorder-title-row">
                                        <h2>{post.title}</h2>

                                        {post.featured && (
                                            <span className="admin-blog-reorder-featured">
                                                Featured
                                            </span>
                                        )}
                                    </div>

                                    <p>
                                        {post.summary ||
                                            "No summary provided."}
                                    </p>

                                    <span className="admin-blog-reorder-current">
                                        Stored order:{" "}
                                        {post.displayOrder ?? 0}
                                    </span>
                                </div>

                                <div className="admin-blog-reorder-actions">
                                    <button
                                        type="button"
                                        aria-label={`Move ${post.title} up`}
                                        disabled={
                                            index === 0 ||
                                            isSaving
                                        }
                                        onClick={() =>
                                            movePost(
                                                index,
                                                -1
                                            )
                                        }
                                    >
                                        ↑ Move Up
                                    </button>

                                    <button
                                        type="button"
                                        aria-label={`Move ${post.title} down`}
                                        disabled={
                                            index ===
                                            posts.length -
                                            1 ||
                                            isSaving
                                        }
                                        onClick={() =>
                                            movePost(
                                                index,
                                                1
                                            )
                                        }
                                    >
                                        ↓ Move Down
                                    </button>
                                </div>
                            </article>
                        );
                    })}
                </section>
            )}

            {hasChanges && (
                <div className="admin-blog-reorder-unsaved">
                    You have unsaved ordering changes.
                </div>
            )}
        </main>
    );
}

export default AdminBlogReorder;