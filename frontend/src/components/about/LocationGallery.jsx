import {
    useEffect,
    useState,
} from "react";

import {
    FaChevronLeft,
    FaChevronRight,
} from "react-icons/fa";

import usePublicGallery from "../../hooks/gallery/usePublicGallery.js";
import { getImageUrl } from "../../utils/imageUrl.js";

import "../../styles/about/locationGallery.css";

function LocationGallery() {
    const {
        images,
        isLoading,
        error,
        loadImages,
    } = usePublicGallery();

    const [currentImage, setCurrentImage] =
        useState(0);

    /*
     * Return to the first image whenever the backend list changes.
     * This prevents an invalid array index after images are hidden.
     */
    useEffect(() => {
        setCurrentImage(0);
    }, [images]);

    function previousImage() {
        setCurrentImage((current) =>
            current === 0
                ? images.length - 1
                : current - 1
        );
    }

    function nextImage() {
        setCurrentImage((current) =>
            current === images.length - 1
                ? 0
                : current + 1
        );
    }

    if (isLoading) {
        return (
            <section className="location-gallery">
                <p className="gallery-state-message">
                    Loading gallery...
                </p>
            </section>
        );
    }

    if (error) {
        return (
            <section className="location-gallery">
                <div className="gallery-state-message">
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={loadImages}
                    >
                        Try again
                    </button>
                </div>
            </section>
        );
    }

    /*
     * Do not render a broken carousel when no visible images exist.
     */
    if (images.length === 0) {
        return null;
    }

    const currentGalleryImage =
        images[currentImage];

    const currentImageUrl = getImageUrl(
        currentGalleryImage.imageUrl
    );

    return (
        <section className="location-gallery">
            <div className="gallery-heading">
                <p>Our Practice</p>

                <h2>
                    A calm and welcoming environment
                    where you can feel comfortable,
                    safe and supported.
                </h2>
            </div>

            <div className="gallery-main">
                <button
                    type="button"
                    className="gallery-arrow"
                    onClick={previousImage}
                    aria-label="Show previous image"
                    disabled={images.length === 1}
                >
                    <FaChevronLeft aria-hidden="true" />
                </button>

                <div className="gallery-image-frame">
                    <img
                        src={currentImageUrl}
                        alt={currentGalleryImage.altText}
                        className="gallery-feature-image"
                    />
                </div>

                <button
                    type="button"
                    className="gallery-arrow"
                    onClick={nextImage}
                    aria-label="Show next image"
                    disabled={images.length === 1}
                >
                    <FaChevronRight aria-hidden="true" />
                </button>
            </div>

            {currentGalleryImage.caption && (
                <p className="gallery-caption">
                    {currentGalleryImage.caption}
                </p>
            )}

            {images.length > 1 && (
                <>
                    <div
                        className="gallery-dots"
                        aria-label="Choose gallery image"
                    >
                        {images.map((image, index) => (
                            <button
                                key={image.id}
                                type="button"
                                className={
                                    index === currentImage
                                        ? "gallery-dot active"
                                        : "gallery-dot"
                                }
                                onClick={() =>
                                    setCurrentImage(index)
                                }
                                aria-label={`Show image ${
                                    index + 1
                                }`}
                                aria-current={
                                    index === currentImage
                                        ? "true"
                                        : undefined
                                }
                            />
                        ))}
                    </div>

                    <div className="gallery-thumbnails">
                        {images.map((image, index) => (
                            <button
                                key={image.id}
                                type="button"
                                className="gallery-thumbnail-button"
                                onClick={() =>
                                    setCurrentImage(index)
                                }
                                aria-label={`Show ${image.altText}`}
                                aria-current={
                                    index === currentImage
                                        ? "true"
                                        : undefined
                                }
                            >
                                <img
                                    src={getImageUrl(
                                        image.imageUrl
                                    )}
                                    alt=""
                                    className={
                                        index === currentImage
                                            ? "gallery-thumbnail active"
                                            : "gallery-thumbnail"
                                    }
                                />
                            </button>
                        ))}
                    </div>
                </>
            )}
        </section>
    );
}

export default LocationGallery;