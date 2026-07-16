import BlogPostCard from "./BlogPostCard.jsx";

/*
 * Displays the public blog list.
 * Handles loading, empty and error states before
 * rendering the individual blog cards.
 */
function BlogPostGrid({
                          posts,
                          isLoading,
                          error,
                          onRetry,
                      }) {
    if (isLoading) {
        return (
            <div className="blog-status-message">
                <p>Loading articles...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div
                className="
                    blog-status-message
                    blog-error-message
                "
                role="alert"
            >
                <p>{error}</p>

                <button
                    type="button"
                    onClick={onRetry}
                >
                    Try again
                </button>
            </div>
        );
    }

    if (posts.length === 0) {
        return (
            <div className="blog-status-message">
                <h2>No articles yet</h2>

                <p>
                    Published articles will appear
                    here.
                </p>
            </div>
        );
    }

    return (
        <div className="blog-grid">
            {posts.map((post) => (
                <BlogPostCard
                    key={post.id}
                    post={post}
                />
            ))}
        </div>
    );
}

export default BlogPostGrid;