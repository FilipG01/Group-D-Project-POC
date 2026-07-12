import { Helmet } from "react-helmet-async";

function SEO({
                 title,
                 description,
                 keywords = [],
                 canonical,
                 image,
                 type = "website",
                 children,
                 schema,
             }) {
    const keywordsContent = Array.isArray(keywords)
        ? keywords.join(", ")
        : keywords;

    return (
        <Helmet>
            <title>{title}</title>

            <meta
                name="description"
                content={description}
            />

            {keywordsContent && (
                <meta
                    name="keywords"
                    content={keywordsContent}
                />
            )}

            {canonical && (
                <link
                    rel="canonical"
                    href={canonical}
                />
            )}

            <meta property="og:type" content={type} />
            <meta property="og:title" content={title} />
            <meta property="og:description" content={description} />

            {canonical && (
                <meta property="og:url" content={canonical} />
            )}

            {image && (
                <meta property="og:image" content={image} />
            )}

            <meta
                name="twitter:card"
                content={image ? "summary_large_image" : "summary"}
            />

            <meta name="twitter:title" content={title} />
            <meta
                name="twitter:description"
                content={description}
            />

            {image && (
                <meta
                    name="twitter:image"
                    content={image}
                />
            )}

            {schema && (
                <script type="application/ld+json">
                    {JSON.stringify(schema)}
                </script>
            )}

            {children}

        </Helmet>
    );
}

export default SEO;