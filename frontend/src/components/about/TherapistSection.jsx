import therapistImg from "../../assets/images/therapistImg.jpg";

function TherapistSection() {

    return (

        <section className="therapist-section">

            <div className="therapist-container">

                <div className="therapist-image">
                    <img
                        src={therapistImg}
                        alt="Marlena Gnitecki"
                    />
                </div>

                <div className="therapist-text-card">

                    <h2>Marlena Gnitecka</h2>

                    <p>
                        Marlena has navigated through her own therapeutic journey,
                        experiencing confusion, vulnerability, and a lack of solutions.

                        These encounters have been invaluable,
                        ensuring she never forgot the vulnerability and confusion clients face when
                        seeking help.
                    </p>

                    <p>
                        Marlena's services are backed by a combination of educational
                        qualifications—Master's in Science, BA (Hons) in Counselling
                        and Psychotherapy, practical experience since 2021,
                        and APCP accreditation.
                    </p>

                    <p>
                        With a background of 16 years in special education,
                        Marlena comprehends the diverse challenges individuals face.
                        Culturally sensitive and bilingual in Polish and English,
                        Marlena embraces cross-cultural backgrounds and specialises in working
                        with LGBTQ+ individuals.
                    </p>

                </div>

            </div>

        </section>

    );

}

export default TherapistSection;