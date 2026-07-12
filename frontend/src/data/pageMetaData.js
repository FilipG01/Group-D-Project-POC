const SITE_NAME = "Root Therapy";

const SITE_URL = "https://www.roottherapy.ie";

const DEFAULT_SHARE_IMAGE = `${SITE_URL}/images/root-therapy-share.jpg`;

export const pageMeta = {
    home: {
        title: "Root Therapy | Psychotherapy & Counselling in Dublin",
        description:
            "Compassionate psychotherapy and counselling in Dublin for anxiety, depression, relationships, neurodiversity, grief, stress and emotional wellbeing.",
        keywords: [
            "psychotherapist Dublin",
            "psychotherapy Dublin",
            "counselling Dublin",
            "therapy Dublin",
            "mental health support Dublin",
            "private therapist Dublin",
            "Root Therapy",
        ],
        canonical: `${SITE_URL}/`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

    about: {
        title: "About Root Therapy | Psychotherapist in Dublin",
        description:
            "Learn about Root Therapy, our therapeutic approach, professional qualifications and commitment to compassionate, confidential psychotherapy in Dublin.",
        keywords: [
            "about Root Therapy",
            "psychotherapist Dublin",
            "qualified psychotherapist Dublin",
            "APCP psychotherapist",
            "Polish speaking therapist Dublin",
            "LGBTQ+ affirming therapist",
            "neurodiversity affirming therapy",
        ],
        canonical: `${SITE_URL}/about`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

    services: {
        title: "Psychotherapy Services Dublin | Root Therapy",
        description:
            "Explore psychotherapy services in Dublin for LGBTQ+ support, neurodiversity, depression, anxiety, relationships, stress, anger, grief and addiction.",
        keywords: [
            "psychotherapy services Dublin",
            "counselling services Dublin",
            "anxiety therapy Dublin",
            "depression counselling Dublin",
            "LGBTQ+ therapy Dublin",
            "neurodiversity therapy Dublin",
            "relationship counselling Dublin",
        ],
        canonical: `${SITE_URL}/services`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

    contact: {
        title: "Contact Root Therapy | Book Psychotherapy in Dublin",
        description:
            "Contact Root Therapy to ask a question or arrange a confidential psychotherapy session in Dublin. Reach out by phone, email or contact form.",
        keywords: [
            "contact psychotherapist Dublin",
            "book therapy Dublin",
            "psychotherapy appointment Dublin",
            "counselling appointment Dublin",
            "private therapist Dublin",
            "Root Therapy contact",
        ],
        canonical: `${SITE_URL}/contact`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

    privacy: {
        title: "Privacy Policy | Root Therapy",
        description:
            "Read the Root Therapy Privacy Policy, including how website enquiries, analytics, advertising data and cookies are handled.",
        keywords: [
            "Root Therapy privacy policy",
            "Root Therapy privacy",
            "Root Therapy policies",
        ],
        canonical: `${SITE_URL}/privacy`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

    terms: {
        title: "Terms & Conditions | Root Therapy",
        description:
            "Read the terms governing use of the Root Therapy website, including website information, enquiries, acceptable use and third-party links.",
        keywords: [
            "Root Therapy terms and conditions",
            "Root Therapy website terms",
            "Root Therapy website terms Ireland",
        ],
        canonical: `${SITE_URL}/terms`,
        image: DEFAULT_SHARE_IMAGE,
        type: "website",
    },

};

export { SITE_NAME, SITE_URL, DEFAULT_SHARE_IMAGE };