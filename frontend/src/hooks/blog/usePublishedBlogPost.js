import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    getPublishedBlogPostBySlug,
} from "../../api/blogApi.js";

/*
 * Loads one published blog post using its URL slug.
 */
export default function usePublishedBlogPost(slug) {
    const [post, setPost] = useState(null);
    const [isLoading, setIsLoading] =
        useState(true);

    const [notFound, setNotFound] =
        useState(false);

    const [error, setError] = useState("");
    const [reloadKey, setReloadKey] = useState(0);

    const loadPost = useCallback(async () => {
        setIsLoading(true);
        setError("");
        setNotFound(false);
        setPost(null);

        try {
            const data =
                await getPublishedBlogPostBySlug(
                    slug
                );

            setPost(data);
        } catch (requestError) {
            console.error(requestError);

            if (requestError.status === 404) {
                setNotFound(true);
                return;
            }

            setError(
                requestError.message ||
                "The article could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, [slug]);

    // Reload when the slug changes or the user presses retry.
    useEffect(() => {
        /* eslint-disable react-hooks/set-state-in-effect */
        loadPosts();
        /* eslint-enable react-hooks/set-state-in-effect */
    }, [loadPost, reloadKey]);

    function reload() {
        setReloadKey((current) => current + 1);
    }

    return {
        post,
        isLoading,
        notFound,
        error,
        reload,
    };
}