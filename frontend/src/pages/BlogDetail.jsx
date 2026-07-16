import { useMemo } from "react";
import {
    Link,
    Navigate,
    useParams,
} from "react-router-dom";

import ContactCTA from "../components/shared/ContactCTA.jsx";
import SEO from "../components/shared/SEO.jsx";
import { SITE_URL } from "../data/pageMetaData.js";
import usePublishedBlogPost from "../hooks/blog/usePublishedBlogPost.js";
import {
    formatBlogDate,
    splitBlogBody,
} from "../utils/blog/blogFormatters.js";
import { getImageUrl } from "../utils/imageUrl.js";

import "../styles/blog/blogDetail.css";

function BlogDetail() {
    const { slug } = useParams();

    // Load the article that matches the slug in the URL.
    const {
        post,
        isLoading,
        notFound,
        error,
        reload,
    } = usePublishedBlogPost(slug);

    // Only rebuild the paragraph list when the article body changes.
    const paragraphs = useMemo(
        () => splitBlogBody(post?.body),
        [post?.body]
    );

    if (isLoading) {
        return (
            <main className="blog-detail-status">
                <p>Loading article...</p>
            </main>
        );
    }

    if (notFound) {
        return <Navigate to="/404" replace />;
    }

    if (error || !post) {
        return (
            <main className="blog-detail-status">
                <h1>Unable to load article</h1>

                <p>{error}</p>

                <button
                    type="button"
                    onClick={reload}
                >
                    Try again
                </button>

                <Link
                    to="/blog"
                    className="blog-detail-back-link"
                >
                    Return to the blog
                </Link>
            </main>
        );
    }

    const canonicalUrl =
        `${SITE_URL}/blog/${post.slug}`;

    const displayImageUrl = getImageUrl(
        post.featuredImageUrl
    );

    const shareImage = post.featuredImageUrl
        ? post.featuredImageUrl.startsWith("http")
            ? post.featuredImageUrl
            : `${SITE_URL}${post.featuredImageUrl}`
        : undefined;

    const articleSchema = {
        "@context": "https://schema.org",
        "@type": "BlogPosting",
        headline: post.title,
        description: post.summary,
        datePublished: post.publishedAt,
        dateModified: post.updatedAt,
        mainEntityOfPage: canonicalUrl,
        author: {
            "@type": "Person",
            name: post.author?.name,
        },
        publisher: {
            "@type": "Organization",
            name: "Root Therapy",
        },
        ...(shareImage
            ? {
                image: [shareImage],
            }
            : {}),
    };

    return (
        <>
            <SEO
                title={
                    post.seoTitle ||
                    `${post.title} | Root Therapy`
                }
                description={
                    post.seoDescription ||
                    post.summary
                }
                keywords={post.keywords}
                canonical={canonicalUrl}
                image={shareImage}
                type="article"
                schema={articleSchema}
            />

            <main className="blog-detail-page">
                <article>
                    <header className="blog-detail-header">
                        <div className="blog-detail-heading">
                            <Link
                                to="/blog"
                                className="blog-detail-back-link"
                            >
                                ← Back to blog
                            </Link>

                            <p className="section-label">
                                Root Therapy Blog
                            </p>

                            <h1>{post.title}</h1>

                            <p className="blog-detail-summary">
                                {post.summary}
                            </p>

                            <div className="blog-detail-meta">
                                <span>
                                    By {post.author?.name}
                                </span>

                                {post.publishedAt && (
                                    <time
                                        dateTime={
                                            post.publishedAt
                                        }
                                    >
                                        {formatBlogDate(
                                            post.publishedAt
                                        )}
                                    </time>
                                )}
                            </div>
                        </div>
                    </header>

                    {displayImageUrl && (
                        <figure className="blog-detail-image-wrapper">
                            <img
                                src={displayImageUrl}
                                alt=""
                                className="blog-detail-image"
                            />
                        </figure>
                    )}

                    <section className="blog-detail-content">
                        <div className="blog-detail-body">
                            {paragraphs.map(
                                (paragraph, index) => (
                                    <p
                                        key={`${post.id}-paragraph-${index}`}
                                    >
                                        {paragraph}
                                    </p>
                                )
                            )}

                            {paragraphs.length === 0 && (
                                <p>
                                    This article has no
                                    content yet.
                                </p>
                            )}
                        </div>

                        <aside className="blog-detail-sidebar">
                            <h2>About the author</h2>

                            <p className="blog-detail-author-name">
                                {post.author?.name}
                            </p>

                            <p>
                                A member of the Root Therapy
                                team, sharing guidance and
                                perspectives on emotional
                                wellbeing.
                            </p>

                            {post.keywords?.length > 0 && (
                                <>
                                    <h2>Topics</h2>

                                    <div className="blog-keyword-list">
                                        {post.keywords.map(
                                            (keyword) => (
                                                <span
                                                    key={
                                                        keyword
                                                    }
                                                >
                                                    {
                                                        keyword
                                                    }
                                                </span>
                                            )
                                        )}
                                    </div>
                                </>
                            )}
                        </aside>
                    </section>
                </article>

                <ContactCTA />
            </main>
        </>
    );
}

export default BlogDetail;