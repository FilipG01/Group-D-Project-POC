/*
 * Reusable editor for a list of text values.
 * It is used for paragraphs, checklist points and keywords.
 */
function ArrayFieldEditor({
                              title,
                              addButtonLabel,
                              items,
                              fieldName,
                              inputType = "text",
                              rows = 4,
                              placeholder = "",
                              onChange,
                              onAdd,
                              onRemove,
                          }) {
    return (
        <div className="admin-array-field">
            <div className="admin-array-heading">
                <h2>{title}</h2>

                <button
                    type="button"
                    onClick={() => onAdd(fieldName)}
                >
                    {addButtonLabel}
                </button>
            </div>

            {items.map((item, index) => (
                <div
                    className="admin-array-row"
                    key={`${fieldName}-${index}`}
                >
                    {inputType === "textarea" ? (
                        <textarea
                            rows={rows}
                            value={item}
                            placeholder={placeholder}
                            onChange={(event) =>
                                onChange(
                                    fieldName,
                                    index,
                                    event.target.value
                                )
                            }
                        />
                    ) : (
                        <input
                            type="text"
                            value={item}
                            placeholder={placeholder}
                            onChange={(event) =>
                                onChange(
                                    fieldName,
                                    index,
                                    event.target.value
                                )
                            }
                        />
                    )}

                    <button
                        type="button"
                        onClick={() =>
                            onRemove(fieldName, index)
                        }
                    >
                        Remove
                    </button>
                </div>
            ))}
        </div>
    );
}

export default ArrayFieldEditor;