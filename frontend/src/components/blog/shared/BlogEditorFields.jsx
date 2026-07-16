/*
 * Shared main writing fields used by both
 * therapist and admin blog editors.
 */
function BlogEditorFields({
                              form,
                              isEditable,
                              onChange,
                              sectionClassName = "therapist-blog-editor-main",
                              fieldClassName = "therapist-editor-field",
                          }) {
    return (
        <section className={sectionClassName}>
            <div className={fieldClassName}>
                <label htmlFor="title">
                    Title
                </label>

                <input
                    id="title"
                    name="title"
                    type="text"
                    value={form.title}
                    onChange={onChange}
                    maxLength={220}
                    required
                    disabled={!isEditable}
                />

                <span>
                    {form.title.length}/220
                </span>
            </div>

            <div className={fieldClassName}>
                <label htmlFor="summary">
                    Summary
                </label>

                <textarea
                    id="summary"
                    name="summary"
                    value={form.summary}
                    onChange={onChange}
                    maxLength={500}
                    rows={4}
                    disabled={!isEditable}
                />

                <span>
                    {form.summary.length}/500
                </span>
            </div>

            <div className={fieldClassName}>
                <label htmlFor="body">
                    Article body
                </label>

                <textarea
                    id="body"
                    name="body"
                    value={form.body}
                    onChange={onChange}
                    rows={20}
                    disabled={!isEditable}
                    placeholder="Write the article here. Separate paragraphs with a blank line."
                />
            </div>
        </section>
    );
}

export default BlogEditorFields;