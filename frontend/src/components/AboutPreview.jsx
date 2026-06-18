import { Link } from 'react-router-dom'
import '../../styles/AboutPreview.css'

function AboutPreview() {
    return (
        <section className="about-preview-section">
            <div className="about-preview-image">

            </div>

            <div className="about-preview-content">
                <p className="about-preview-label">
                    About Root Therapy
                </p>

                <h2>
                    A place to feel heard,
                    understood and supported.
                </h2>

                <p className="about-preview-text">
                    Root Therapy provides a warm, confidential and
                    non-judgemental space where individuals can
                    explore life's challenges, strengthen resilience
                    and move towards meaningful change.

                    Through compassionate therapeutic support,
                    we aim to help clients better understand
                    themselves, develop healthier coping strategies
                    and build lasting emotional wellbeing.
                </p>

                <Link
                    to="/about"
                    className="about-preview-button"
                >
                    Learn More →
                </Link>
            </div>
        </section>
    )
}

export default AboutPreview