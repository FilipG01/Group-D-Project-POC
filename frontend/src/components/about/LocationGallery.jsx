import { useState } from "react";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import { locationGallery } from "../../data/locationGalleryData";
import "../../styles/locationGallery.css";

function LocationGallery() {

    const [currentImage, setCurrentImage] = useState(0);

    function previousImage() {
        setCurrentImage((prev) =>
            prev === 0 ? locationGallery.length - 1 : prev - 1
        );
    }

    function nextImage() {
        setCurrentImage((prev) =>
            prev === locationGallery.length - 1 ? 0 : prev + 1
        );
    }

    return (

        <section className="location-gallery">

            <div className="gallery-heading">

                <p>Our Practice</p>

                <h2>
                    A calm and welcoming environment where you can feel comfortable,
                    safe and supported.
                </h2>

            </div>

            <div className="gallery-main">

                <button
                    className="gallery-arrow"
                    onClick={previousImage}
                >
                    <FaChevronLeft />
                </button>

                <div className="gallery-image-frame">
                    <img
                        src={locationGallery[currentImage].image}
                        alt={locationGallery[currentImage].alt}
                        className="gallery-feature-image"
                    />
                </div>

                <button
                    className="gallery-arrow"
                    onClick={nextImage}
                >
                    <FaChevronRight />
                </button>

            </div>

            <div className="gallery-dots">

                {locationGallery.map((_, index) => (

                    <span
                        key={index}
                        className={
                            index === currentImage
                                ? "gallery-dot active"
                                : "gallery-dot"
                        }
                    />

                ))}

            </div>

            <div className="gallery-thumbnails">

                {locationGallery.map((image, index) => (

                    <img
                        key={image.id}
                        src={image.image}
                        alt={image.alt}
                        className={
                            index === currentImage
                                ? "gallery-thumbnail active"
                                : "gallery-thumbnail"
                        }
                        onClick={() => setCurrentImage(index)}
                    />

                ))}

            </div>

        </section>

    );

}

export default LocationGallery;