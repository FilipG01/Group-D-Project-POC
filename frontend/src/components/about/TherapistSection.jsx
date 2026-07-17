import usePublicTherapists from "../../hooks/therapists/usePublicTherapists.js";
import TherapistCard from "./TherapistCard.jsx";

function TherapistSection() {
    const {
        therapists,
        isLoading,
        error,
        reload,
    } = usePublicTherapists();

    if (isLoading) {
        return (
            <section className="therapist-section">
                <p>Loading therapist profiles...</p>
            </section>
        );
    }

    if (error) {
        return (
            <section className="therapist-section">
                <div className="therapist-status-message">
                    <p>{error}</p>

                    <button
                        type="button"
                        onClick={reload}
                    >
                        Try again
                    </button>
                </div>
            </section>
        );
    }

    if (therapists.length === 0) {
        return null;
    }

    return (
        <section className="therapist-section">
            {therapists.map((therapist) => (
                <TherapistCard
                    key={therapist.userId}
                    therapist={therapist}
                />
            ))}
        </section>
    );
}

export default TherapistSection;