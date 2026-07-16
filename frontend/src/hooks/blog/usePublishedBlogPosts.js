import {
    useCallback,
    useEffect,
    useState,
} from "react";

import { getPublishedBlogPosts } from "../../api/blogApi.js";

const POSTS_PER_PAGE = 9;

/*
 * Loads the published posts shown on the public blog page.
 * It also manages pagination and retrying failed requests.
 */
export default function usePublishedBlogPosts() {
    const [posts, setPosts] = useState([]);
    const [pageData, setPageData] = useState(null);
    const [page, setPage] = useState(0);
    const [reloadKey, setReloadKey] = useState(0);

    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");

    const loadPosts = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data =
                await getPublishedBlogPosts({
                    page,
                    size: POSTS_PER_PAGE,
                });

            setPosts(data.content || []);
            setPageData(data);
        } catch (requestError) {
            console.error(requestError);

            setError(
                "We could not load the blog posts. Please try again."
            );
        } finally {
            setIsLoading(false);
        }
    }, [page]);

    // Reload when the page changes or the user presses retry.
    useEffect(() => {
        /* eslint-disable react-hooks/set-state-in-effect */
        loadPosts();
        /* eslint-enable react-hooks/set-state-in-effect */
    }, [loadPosts, reloadKey]);

    function nextPage() {
        setPage((current) => current + 1);
    }

    function previousPage() {
        setPage((current) =>
            Math.max(0, current - 1)
        );
    }

    function reload() {
        setReloadKey((current) => current + 1);
    }

    return {
        posts,
        page,
        pageData,
        isLoading,
        error,
        nextPage,
        previousPage,
        reload,
    };
}