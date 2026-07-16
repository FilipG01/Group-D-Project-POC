import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import '../../styles/home/servicesTree.css'

const treeServices = [
    {
        label: 'Addiction',
        x: 28,
        y: 36,
        description:
            'Support for individuals navigating addiction, recovery, relapse prevention, and the emotional patterns connected to dependency.',
    },
    {
        label: 'Relationships',
        x: 42,
        y: 28,
        description:
            'A space to explore communication, conflict, attachment, boundaries, and emotional connection in relationships.',
    },
    {
        label: 'Stress',
        x: 58,
        y: 25,
        description:
            'Support for understanding stress, reducing overwhelm, and developing healthier ways to manage pressure.',
    },
    {
        label: 'Anxiety',
        x: 72,
        y: 34,
        description:
            'Therapeutic support for anxiety, worry, intrusive thoughts, panic, and feelings of being emotionally stuck.',
    },
    {
        label: 'Bereavement',
        x: 34,
        y: 52,
        description:
            'Compassionate support for grief, loss, adjustment, and the complex emotions that can follow bereavement.',
    },
    {
        label: 'Depression',
        x: 66,
        y: 50,
        description:
            'Support for low mood, emotional heaviness, lack of motivation, and rebuilding connection with yourself.',
    },
    {
        label: 'LGBTQ+',
        x: 25,
        y: 68,
        description:
            'An affirming space to explore identity, relationships, self-acceptance, and lived experience.',
    },
    {
        label: 'Neurodiversity',
        x: 50,
        y: 66,
        description:
            'Support for ADHD, autism, sensory needs, masking, self-understanding, and neurodivergent identity.',
    },
    {
        label: 'Stress Mgmt',
        x: 75,
        y: 68,
        description:
            'Practical and reflective support for managing ongoing stress, transitions, burnout, and emotional strain.',
    },
]

function ServicesTree() {
    const sectionRef = useRef(null)
    const [hasAnimated, setHasAnimated] = useState(false)
    const [selectedService, setSelectedService] = useState(null)

    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    setHasAnimated(true)
                    observer.disconnect()
                }
            },
            { threshold: 0.35 }
        )

        if (sectionRef.current) {
            observer.observe(sectionRef.current)
        }

        return () => observer.disconnect()
    }, [])

    return (
        <section
            ref={sectionRef}
            className={
                'services-tree-section' +
                (hasAnimated ? ' is-visible' : '') +
                (selectedService ? ' has-panel' : '')
            }
        >
            <div className="services-tree-intro">
                <p>Our Services</p>
                <h2>Support rooted in care, growth, and understanding.</h2>
            </div>

            <div className="tree-and-panel">
                <div className="tree-area">
                    <div className="tree-wrapper">
                        <svg
                            className="tree-svg"
                            viewBox="0 0 100 100"
                            role="img"
                            aria-label="Tree graphic with therapy services"
                        >
                            <g className="tree-canopy-group">
                                <circle className="tree-canopy canopy-1" cx="20" cy="55" r="12" />
                                <circle className="tree-canopy canopy-2" cx="25" cy="40" r="15" />
                                <circle className="tree-canopy canopy-3" cx="35" cy="28" r="16" />
                                <circle className="tree-canopy canopy-4" cx="50" cy="24" r="18" />
                                <circle className="tree-canopy canopy-5" cx="65" cy="28" r="16" />
                                <circle className="tree-canopy canopy-6" cx="76" cy="40" r="15" />
                                <circle className="tree-canopy canopy-7" cx="82" cy="55" r="13" />
                                <circle className="tree-canopy canopy-8" cx="32" cy="54" r="16" />
                                <circle className="tree-canopy canopy-9" cx="48" cy="50" r="18" />
                                <circle className="tree-canopy canopy-10" cx="64" cy="54" r="16" />
                                <circle className="tree-canopy canopy-11" cx="28" cy="68" r="12" />
                                <circle className="tree-canopy canopy-12" cx="50" cy="68" r="15" />
                                <circle className="tree-canopy canopy-13" cx="72" cy="68" r="12" />
                            </g>

                            <path className="tree-line trunk" d="M50 95 C49 78, 51 65, 50 48" />

                            <path className="tree-line branch branch-1" d="M50 60 C38 55, 30 47, 25 34" />
                            <path className="tree-line branch branch-2" d="M50 55 C43 44, 42 35, 40 25" />
                            <path className="tree-line branch branch-3" d="M50 50 C58 39, 62 32, 60 22" />
                            <path className="tree-line branch branch-4" d="M50 60 C62 55, 72 47, 78 34" />
                            <path className="tree-line branch branch-5" d="M50 72 C38 67, 30 63, 22 58" />
                            <path className="tree-line branch branch-6" d="M50 72 C64 68, 72 63, 80 58" />

                            <path className="tree-line root" d="M50 94 C46 95, 42 97, 38 99" />
                            <path className="tree-line root" d="M50 95 C50 97, 50 98, 50 100" />
                            <path className="tree-line root" d="M50 94 C54 95, 58 97, 62 99" />
                        </svg>

                        {treeServices.map((service, index) => (
                            <button
                                key={service.label}
                                type="button"
                                className="service-leaf"
                                onClick={() => setSelectedService(service)}
                                style={{
                                    left: `${service.x}%`,
                                    top: `${service.y}%`,
                                    animationDelay: `${1 + index * 0.15}s`,
                                }}
                            >
                                {service.label}
                            </button>
                        ))}
                    </div>
                </div>

                <aside className={`service-detail-panel ${selectedService ? 'is-open' : ''}`}>
                    {selectedService && (
                        <>
                            <button
                                type="button"
                                className="panel-close-button"
                                onClick={() => setSelectedService(null)}
                                aria-label="Close service details"
                            >
                                ×
                            </button>

                            <p className="panel-label">Service</p>
                            <h3>{selectedService.label}</h3>
                            <p>{selectedService.description}</p>

                            <Link to="/services" className="panel-services-link">
                                View all services →
                            </Link>
                        </>
                    )}
                </aside>
            </div>
        </section>
    )
}

export default ServicesTree