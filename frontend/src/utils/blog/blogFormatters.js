/*
 * Converts a stored date into a readable blog date.
 *
 * Example:
 * 2026-07-15 becomes 15 July 2026.
 */
export function formatBlogDate(value) {
    if (!value) {
        return "";
    }

    return new Intl.DateTimeFormat("en-IE", {
        day: "numeric",
        month: "long",
        year: "numeric",
    }).format(new Date(value));
}

/*
 * Converts a backend status into readable text.
 *
 * Example:
 * CHANGES_REQUESTED becomes Changes Requested.
 */
export function formatBlogStatus(status) {
    if (!status) {
        return "";
    }

    return status
        .toLowerCase()
        .split("_")
        .map(
            (word) =>
                word.charAt(0).toUpperCase() +
                word.slice(1)
        )
        .join(" ");
}

/*
 * Formats dates shown inside the therapist
 * and admin dashboards.
 */
export function formatBlogDateTime(value) {
    if (!value) {
        return "Not available";
    }

    return new Intl.DateTimeFormat("en-IE", {
        day: "numeric",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(new Date(value));
}

/*
 * Splits the article body into paragraphs.
 * Blank lines are treated as paragraph breaks.
 */
export function splitBlogBody(body) {
    if (!body) {
        return [];
    }

    return body
        .split(/\n\s*\n/)
        .map((paragraph) => paragraph.trim())
        .filter(Boolean);
}