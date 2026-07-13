import { therapists } from "../../data/therapistData.js";
import TherapistCard from "./TherapistCard.jsx";

function TherapistSection() {
    return (
        <section className="therapist-section">
            {therapists.map((therapist) => (
                <TherapistCard
                    key={therapist.id}
                    therapist={therapist}
                />
            ))}
        </section>
    );
}

export default TherapistSection;