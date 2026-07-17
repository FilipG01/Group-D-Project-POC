import { Link } from "react-router-dom";

import { getImageUrl } from "../../../utils/imageUrl";
import { formatBlogDate } from "../../../utils/blog/blogFormatters";

/*
 * Displays one preview card for a published blog post.
 * The Blog page supplies the post data.
 */
function BlogPostCard({ post }) {
    const imageUrl = getImageUrl(
        post.featuredImageUrl
    );

    return (
        <article
            className={
                post.featured
                    ? "blog-card blog-card-featured"
                    : "blog-card"
            }
        >
            <Link
                to={`/blog/${post.slug}`}
                className="blog-card-image-link"
                aria-label={`Read ${post.title}`}
            >
                {/* Show the uploaded image if there is one. */}
                {imageUrl ? (
                    <img
                        src={imageUrl}
                        alt=""
                        className="blog-card-image"
                    />
                ) : (
                    <div
                        className="
                            blog-card-image
                            blog-card-image-placeholder
                        "
                        aria-hidden="true"
                    >
                        <span>Root Therapy</span>
                    </div>
                )}
            </Link>

            <div className="blog-card-content">

                <div className="blog-card-meta">

                    {post.featured && (
                        <span className="blog-featured-label">
                            Featured
                        </span>
                    )}

                    {post.publishedAt && (
                        <time
                            dateTime={post.publishedAt}
                        >
                            {formatBlogDate(
                                post.publishedAt
                            )}
                        </time>
                    )}

                </div>

                <h2>
                    <Link
                        to={`/blog/${post.slug}`}
                    >
                        {post.title}
                    </Link>
                </h2>

                <p className="blog-card-summary">
                    {post.summary}
                </p>

                <div className="blog-card-footer">

                    <span>
                        By {post.authorName}
                    </span>

                    <Link
                        to={`/blog/${post.slug}`}
                        className="blog-read-link"
                    >
                        Read article →
                    </Link>

                </div>

            </div>
        </article>
    );
}

export default BlogPostCard;