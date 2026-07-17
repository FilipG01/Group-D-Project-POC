/*
 * Shared SEO fields used by both
 * therapist and admin blog editors.
 */
function BlogSeoFields({
                           form,
                           isEditable,
                           onChange,
                           panelClassName = "therapist-blog-editor-panel",
                           fieldClassName = "therapist-editor-field",
                       }) {
    return (
        <section className={panelClassName}>
            <h2>SEO</h2>

            <div className={fieldClassName}>
                <label htmlFor="seoTitle">
                    SEO title
                </label>

                <input
                    id="seoTitle"
                    name="seoTitle"
                    type="text"
                    value={form.seoTitle}
                    onChange={onChange}
                    maxLength={255}
                    disabled={!isEditable}
                />
            </div>

            <div className={fieldClassName}>
                <label htmlFor="seoDescription">
                    SEO description
                </label>

                <textarea
                    id="seoDescription"
                    name="seoDescription"
                    value={form.seoDescription}
                    onChange={onChange}
                    maxLength={320}
                    rows={5}
                    disabled={!isEditable}
                />

                <span>
                    {form.seoDescription.length}/320
                </span>
            </div>

            <div className={fieldClassName}>
                <label htmlFor="keywords">
                    Keywords
                </label>

                <input
                    id="keywords"
                    name="keywords"
                    type="text"
                    value={form.keywords}
                    onChange={onChange}
                    disabled={!isEditable}
                    placeholder="anxiety, therapy, wellbeing"
                />

                <span>
                    Separate keywords with commas.
                </span>
            </div>
        </section>
    );
}

export default BlogSeoFields;