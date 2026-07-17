import { useState } from "react";
import {
    FaChevronLeft,
    FaChevronRight,
} from "react-icons/fa";

import {
    locationGallery,
} from "../../data/locationGalleryData.js";

import "../../styles/about/locationGallery.css";

function LocationGallery() {
    const [currentImage, setCurrentImage] =
        useState(0);

    const currentGalleryImage =
        locationGallery[currentImage];

    function previousImage() {
        setCurrentImage((current) =>
            current === 0
                ? locationGallery.length - 1
                : current - 1
        );
    }

    function nextImage() {
        setCurrentImage((current) =>
            current ===
            locationGallery.length - 1
                ? 0
                : current + 1
        );
    }

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
                >
                    <FaChevronLeft
                        aria-hidden="true"
                    />
                </button>

                <div className="gallery-image-frame">
                    <img
                        src={currentGalleryImage.image}
                        alt={currentGalleryImage.alt}
                        className="gallery-feature-image"
                    />
                </div>

                <button
                    type="button"
                    className="gallery-arrow"
                    onClick={nextImage}
                    aria-label="Show next image"
                >
                    <FaChevronRight
                        aria-hidden="true"
                    />
                </button>
            </div>

            <div
                className="gallery-dots"
                aria-label="Choose gallery image"
            >
                {locationGallery.map(
                    (image, index) => (
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
                    )
                )}
            </div>

            <div className="gallery-thumbnails">
                {locationGallery.map(
                    (image, index) => (
                        <button
                            key={image.id}
                            type="button"
                            className="gallery-thumbnail-button"
                            onClick={() =>
                                setCurrentImage(index)
                            }
                            aria-label={`Show ${image.alt}`}
                            aria-current={
                                index === currentImage
                                    ? "true"
                                    : undefined
                            }
                        >
                            <img
                                src={image.image}
                                alt=""
                                className={
                                    index === currentImage
                                        ? "gallery-thumbnail active"
                                        : "gallery-thumbnail"
                                }
                            />
                        </button>
                    )
                )}
            </div>
        </section>
    );
}

export default LocationGallery;