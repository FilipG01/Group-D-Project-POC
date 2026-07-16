/*
 * Displays pagination controls for the public blog.
 * The parent page controls which page is currently selected.
 */
function BlogPagination({
                            page,
                            pageData,
                            onPrevious,
                            onNext,
                        }) {
    if (
        !pageData ||
        pageData.totalPages <= 1
    ) {
        return null;
    }

    return (
        <nav
            className="blog-pagination"
            aria-label="Blog pagination"
        >
            <button
                type="button"
                disabled={pageData.first}
                onClick={onPrevious}
            >
                ← Previous
            </button>

            <span>
                Page {page + 1} of{" "}
                {pageData.totalPages}
            </span>

            <button
                type="button"
                disabled={pageData.last}
                onClick={onNext}
            >
                Next →
            </button>
        </nav>
    );
}

export default BlogPagination;