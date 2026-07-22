import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    getPublicGalleryImages,
} from "../../api/galleryApi.js";

/*
 * Loads the visible gallery images for public pages.
 */
export default function usePublicGallery() {
    const [images, setImages] = useState([]);

    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");

    const loadImages = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data =
                await getPublicGalleryImages();

            setImages(data || []);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The gallery could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        loadImages();
    }, [loadImages]);

    return {
        images,
        isLoading,
        error,
        loadImages,
    };
}