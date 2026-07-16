import BlogHero from "../components/blog/public/BlogHero.jsx";
import BlogPagination from "../components/blog/public/BlogPagination.jsx";
import BlogPostGrid from "../components/blog/public/BlogPostGrid.jsx";
import ContactCTA from "../components/shared/ContactCTA.jsx";
import SEO from "../components/shared/SEO.jsx";
import { SITE_URL } from "../data/pageMetaData.js";
import usePublishedBlogPosts from "../hooks/blog/usePublishedBlogPosts.js";

import "../styles/blog/blog.css";

function Blog() {
    const {
        posts,
        page,
        pageData,
        isLoading,
        error,
        nextPage,
        previousPage,
        reload,
    } = usePublishedBlogPosts();

    return (
        <>
            <SEO
                title="Therapy Blog | Root Therapy"
                description="Articles, guidance and insights from the Root Therapy team."
                keywords={[
                    "therapy blog",
                    "mental health",
                    "emotional wellbeing",
                    "Root Therapy",
                ]}
                canonical={`${SITE_URL}/blog`}
            />

            <main className="blog-page">
                <BlogHero />

                <section className="blog-list-section">
                    <BlogPostGrid
                        posts={posts}
                        isLoading={isLoading}
                        error={error}
                        onRetry={reload}
                    />

                    <BlogPagination
                        page={page}
                        pageData={pageData}
                        onPrevious={previousPage}
                        onNext={nextPage}
                    />
                </section>

                <ContactCTA />
            </main>
        </>
    );
}

export default Blog;