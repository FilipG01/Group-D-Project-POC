import blogHeroImage from "../../../assets/images/blogheaderImg.jpg";

/*
 * Hero banner shown at the top of the public blog.
 */
function BlogHero() {
    return (
        <section
            className="blog-hero"
            style={{
                backgroundImage: `
                    linear-gradient(
                        90deg,
                        rgba(245, 240, 230, 0.98) 0%,
                        rgba(245, 240, 230, 0.88) 52%,
                        rgba(245, 240, 230, 0.55) 100%
                    ),
                    url("${blogHeroImage}")
                `,
            }}
        >
            <div className="blog-hero-content">
                <p className="section-label">
                    Root Therapy Blog
                </p>

                <h1>
                    Ideas and guidance for emotional wellbeing
                </h1>

                <p>
                    Explore articles from our therapists on
                    mental health, relationships, personal
                    growth and the therapeutic process.
                </p>
            </div>
        </section>
    );
}

export default BlogHero;