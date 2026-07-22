import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    getAdminGalleryImages,
    reorderGalleryImages,
    setGalleryImageArchived,
    setGalleryImageVisible,
} from "../../api/galleryApi.js";

/*
 * Handles gallery loading and admin list actions.
 */
export default function useAdminGallery() {
    const [images, setImages] = useState([]);
    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [busyImageId, setBusyImageId] =
        useState(null);

    const loadImages = useCallback(
        async ({ showLoading = true } = {}) => {
            if (showLoading) {
                setIsLoading(true);
            }

            setError("");

            try {
                const data =
                    await getAdminGalleryImages();

                setImages(data || []);
            } catch (requestError) {
                console.error(requestError);

                setError(
                    requestError.message ||
                    "The gallery images could not be loaded."
                );
            } finally {
                if (showLoading) {
                    setIsLoading(false);
                }
            }
        },
        []
    );

    useEffect(() => {
        loadImages();
    }, [loadImages]);

    async function toggleVisible(image) {
        setBusyImageId(image.id);
        setError("");

        try {
            const updatedImage =
                await setGalleryImageVisible(
                    image.id,
                    !image.visible
                );

            replaceImage(updatedImage);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The image visibility could not be updated."
            );
        } finally {
            setBusyImageId(null);
        }
    }

    async function toggleArchived(image) {
        const action = image.archived
            ? "restore"
            : "archive";

        const confirmed = window.confirm(
            `Are you sure you want to ${action} this gallery image?`
        );

        if (!confirmed) {
            return;
        }

        setBusyImageId(image.id);
        setError("");

        try {
            const updatedImage =
                await setGalleryImageArchived(
                    image.id,
                    !image.archived
                );

            replaceImage(updatedImage);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The image archive status could not be updated."
            );
        } finally {
            setBusyImageId(null);
        }
    }

    async function moveImage(
        imageIndex,
        direction
    ) {
        const targetIndex =
            direction === "up"
                ? imageIndex - 1
                : imageIndex + 1;

        if (
            targetIndex < 0 ||
            targetIndex >= images.length
        ) {
            return;
        }

        const reordered = [...images];

        [
            reordered[imageIndex],
            reordered[targetIndex],
        ] = [
            reordered[targetIndex],
            reordered[imageIndex],
        ];

        setBusyImageId(images[imageIndex].id);
        setError("");

        try {
            const updatedImages =
                await reorderGalleryImages(
                    reordered
                );

            setImages(updatedImages || []);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The gallery order could not be updated."
            );

            // Restore the saved backend order after a failed request.
            await loadImages({
                showLoading: false,
            });
        } finally {
            setBusyImageId(null);
        }
    }

    function replaceImage(updatedImage) {
        setImages((currentImages) =>
            currentImages.map((currentImage) =>
                currentImage.id === updatedImage.id
                    ? updatedImage
                    : currentImage
            )
        );
    }

    return {
        images,
        isLoading,
        error,
        busyImageId,
        loadImages,
        toggleVisible,
        toggleArchived,
        moveImage,
    };
}