import blogHeroImage from "../../../assets/images/blogheaderImg.jpg";

/*
 * Hero banner shown at the top of the public blog.
 */
function BlogHero() {
    return (
        <section
            className="blog-hero"
            style={{
                "--blog-hero-image": `url(${blogHeroImage})`,
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
                    mental health, relationships,
                    personal growth and the therapeutic process.
                </p>

            </div>
        </section>
    );
}

export default BlogHero;