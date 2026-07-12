import { SITE_URL, DEFAULT_SHARE_IMAGE } from "./pageMetaData.js";

export const businessSchema = {
    "@context": "https://schema.org",
    "@type": "ProfessionalService",
    "@id": `${SITE_URL}/#business`,

    name: "Root Therapy",
    url: SITE_URL,
    logo: DEFAULT_SHARE_IMAGE,
    image: DEFAULT_SHARE_IMAGE,

    description:
        "Root Therapy provides compassionate and confidential psychotherapy and counselling services in Dublin.",

    telephone: "+353833036099",
    email: "marlenatherapy@gmail.com",

    address: {
        "@type": "PostalAddress",
        streetAddress:
            "307 Swords Road, Santry Psychotherapy Consultancy Centre",
        addressLocality: "Whitehall",
        addressRegion: "Dublin",
        postalCode: "D09 H9F5",
        addressCountry: "IE",
    },

    areaServed: {
        "@type": "City",
        name: "Dublin",
    },

    sameAs: [
        "https://www.facebook.com/profile.php?id=61591362725769",
        "https://www.instagram.com/marlenatherapy?igsh=MWxvdDN2aHIwaGxkaQ==",
    ],

    availableLanguage: [
        "English",
        "Polish",
    ],
};