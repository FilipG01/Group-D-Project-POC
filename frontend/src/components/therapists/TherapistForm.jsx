import { useState } from "react";
import { getImageUrl } from "../../utils/imageUrl.js";

const emptyForm = {
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    temporaryPassword: "",

    qualifications: "",
    registrationNumber: "",
    yearsExperience: 0,
    bio: "",

    profileImageUrl: "",
    publicBio: [""],
    languages: [""],
    specialisms: [""],

    acceptingClients: false,
    publiclyVisible: false,
    displayOrder: 0,
};

function createInitialFormData(initialData) {
    if (!initialData) {
        return {
            ...emptyForm,
            publicBio: [""],
            languages: [""],
            specialisms: [""],
        };
    }

    return {
        firstName: initialData.firstName || "",
        lastName: initialData.lastName || "",
        email: initialData.email || "",
        phoneNumber: initialData.phoneNumber || "",
        temporaryPassword: "",

        qualifications: initialData.qualifications || "",
        registrationNumber: initialData.registrationNumber || "",
        yearsExperience: initialData.yearsExperience ?? 0,
        bio: initialData.bio || "",

        profileImageUrl: initialData.profileImageUrl || "",

        publicBio:
            initialData.publicBio?.length > 0
                ? [...initialData.publicBio]
                : [""],

        languages:
            initialData.languages?.length > 0
                ? [...initialData.languages]
                : [""],

        specialisms:
            initialData.specialisms?.length > 0
                ? [...initialData.specialisms]
                : [""],

        acceptingClients: Boolean(initialData.acceptingClients),
        publiclyVisible: Boolean(initialData.publiclyVisible),
        displayOrder: initialData.displayOrder ?? 0,
    };
}

function TherapistForm({
                           initialData = null,
                           mode = "edit",
                           isAdmin = false,
                           onSubmit,
                           onUploadImage,
                           submitLabel = "Save Therapist",
                           isSubmitting = false,
                       }) {
    const [formData, setFormData] = useState(() =>
        createInitialFormData(initialData)
    );

    const [isUploadingImage, setIsUploadingImage] = useState(false);
    const [uploadError, setUploadError] = useState("");

    const isCreateMode = mode === "create";

    function handleFieldChange(event) {
        const { name, value, type, checked } = event.target;

        setFormData((current) => ({
            ...current,
            [name]: type === "checkbox" ? checked : value,
        }));
    }

    function updateArrayField(fieldName, index, value) {
        setFormData((current) => ({
            ...current,
            [fieldName]: current[fieldName].map((item, itemIndex) =>
                itemIndex === index ? value : item
            ),
        }));
    }

    function addArrayItem(fieldName) {
        setFormData((current) => ({
            ...current,
            [fieldName]: [...current[fieldName], ""],
        }));
    }

    function removeArrayItem(fieldName, index) {
        setFormData((current) => {
            const remainingItems = current[fieldName].filter(
                (_, itemIndex) => itemIndex !== index
            );

            return {
                ...current,
                [fieldName]:
                    remainingItems.length > 0
                        ? remainingItems
                        : [""],
            };
        });
    }

    async function handleImageUpload(event) {
        const file = event.target.files?.[0];

        if (!file || !onUploadImage) {
            return;
        }

        setIsUploadingImage(true);
        setUploadError("");

        try {
            const result = await onUploadImage(file);

            setFormData((current) => ({
                ...current,
                profileImageUrl: result.url,
            }));
        } catch (error) {
            console.error(error);

            setUploadError(
                error.message || "The profile image could not be uploaded."
            );
        } finally {
            setIsUploadingImage(false);
            event.target.value = "";
        }
    }

    function cleanArray(values) {
        return values
            .map((value) => value.trim())
            .filter((value) => value !== "");
    }

    function handleSubmit(event) {
        event.preventDefault();

        const sharedProfileData = {
            qualifications: formData.qualifications.trim(),
            registrationNumber: formData.registrationNumber.trim(),
            yearsExperience: Number(formData.yearsExperience),
            bio: formData.bio.trim(),
            acceptingClients: formData.acceptingClients,
            profileImageUrl: formData.profileImageUrl.trim(),
            publicBio: cleanArray(formData.publicBio),
            languages: cleanArray(formData.languages),
            specialisms: cleanArray(formData.specialisms),
        };

        if (isCreateMode) {
            onSubmit({
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                email: formData.email.trim(),
                phoneNumber: formData.phoneNumber.trim(),
                temporaryPassword: formData.temporaryPassword,

                qualifications: sharedProfileData.qualifications,
                registrationNumber:
                sharedProfileData.registrationNumber,
                yearsExperience:
                sharedProfileData.yearsExperience,
            });

            return;
        }

        if (isAdmin) {
            onSubmit({
                ...sharedProfileData,
                publiclyVisible: formData.publiclyVisible,
                displayOrder: Number(formData.displayOrder),
            });

            return;
        }

        onSubmit(sharedProfileData);
    }

    return (
        <form
            className="admin-therapist-form"
            onSubmit={handleSubmit}
        >
            {isCreateMode && (
                <section className="admin-form-section">
                    <div className="admin-form-section-heading">
                        <h2>Account information</h2>

                        <p>
                            These details will be used to create the
                            therapist’s login account.
                        </p>
                    </div>

                    <div className="admin-form-grid">
                        <label>
                            <span>First name</span>

                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleFieldChange}
                                maxLength="100"
                                required
                            />
                        </label>

                        <label>
                            <span>Last name</span>

                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleFieldChange}
                                maxLength="100"
                                required
                            />
                        </label>

                        <label>
                            <span>Email address</span>

                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleFieldChange}
                                maxLength="255"
                                required
                            />
                        </label>

                        <label>
                            <span>Phone number</span>

                            <input
                                type="tel"
                                name="phoneNumber"
                                value={formData.phoneNumber}
                                onChange={handleFieldChange}
                                maxLength="30"
                            />
                        </label>

                        <label className="admin-form-full-width">
                            <span>Temporary password</span>

                            <input
                                type="password"
                                name="temporaryPassword"
                                value={formData.temporaryPassword}
                                onChange={handleFieldChange}
                                minLength="8"
                                maxLength="100"
                                autoComplete="new-password"
                                required
                            />

                            <small>
                                The therapist will use this password for
                                their first login.
                            </small>
                        </label>
                    </div>
                </section>
            )}

            <section className="admin-form-section">
                <div className="admin-form-section-heading">
                    <h2>Professional information</h2>
                </div>

                <div className="admin-form-grid">
                    <label>
                        <span>Qualifications</span>

                        <input
                            type="text"
                            name="qualifications"
                            value={formData.qualifications}
                            onChange={handleFieldChange}
                            required
                        />
                    </label>

                    <label>
                        <span>Registration number</span>

                        <input
                            type="text"
                            name="registrationNumber"
                            value={formData.registrationNumber}
                            onChange={handleFieldChange}
                            maxLength="100"
                            required
                        />
                    </label>

                    <label>
                        <span>Years of experience</span>

                        <input
                            type="number"
                            name="yearsExperience"
                            value={formData.yearsExperience}
                            onChange={handleFieldChange}
                            min="0"
                            required
                        />
                    </label>
                </div>

                {!isCreateMode && (
                    <label>
                        <span>Internal/profile biography</span>

                        <textarea
                            name="bio"
                            value={formData.bio}
                            onChange={handleFieldChange}
                            rows="5"
                        />
                    </label>
                )}
            </section>

            {!isCreateMode && (
                <>
                    <section className="admin-form-section">
                        <div className="admin-form-section-heading">
                            <h2>Public profile</h2>

                            <p>
                                These details are displayed on the About
                                page when the profile is publicly visible.
                            </p>
                        </div>

                        <div className="admin-image-field">
                            {onUploadImage && (
                                <label>
                                    <span>Upload profile image</span>

                                    <input
                                        type="file"
                                        accept="image/jpeg,image/png,image/webp"
                                        onChange={handleImageUpload}
                                        disabled={isUploadingImage}
                                    />
                                </label>
                            )}

                            {isUploadingImage && (
                                <p>Uploading image...</p>
                            )}

                            {uploadError && (
                                <p className="admin-form-error">
                                    {uploadError}
                                </p>
                            )}

                            <label>
                                <span>Profile image URL</span>

                                <input
                                    type="text"
                                    name="profileImageUrl"
                                    value={formData.profileImageUrl}
                                    onChange={handleFieldChange}
                                    placeholder="/uploads/therapists/image.jpg"
                                />
                            </label>

                            {formData.profileImageUrl && (
                                <img
                                    className="admin-therapist-image-preview"
                                    src={getImageUrl(
                                        formData.profileImageUrl
                                    )}
                                    alt="Therapist profile preview"
                                />
                            )}
                        </div>

                        <ArrayInputSection
                            title="Biography paragraphs"
                            buttonLabel="Add paragraph"
                            fieldName="publicBio"
                            values={formData.publicBio}
                            multiline
                            onAdd={addArrayItem}
                            onRemove={removeArrayItem}
                            onChange={updateArrayField}
                        />

                        <ArrayInputSection
                            title="Languages"
                            buttonLabel="Add language"
                            fieldName="languages"
                            values={formData.languages}
                            onAdd={addArrayItem}
                            onRemove={removeArrayItem}
                            onChange={updateArrayField}
                        />

                        <ArrayInputSection
                            title="Specialisms"
                            buttonLabel="Add specialism"
                            fieldName="specialisms"
                            values={formData.specialisms}
                            onAdd={addArrayItem}
                            onRemove={removeArrayItem}
                            onChange={updateArrayField}
                        />
                    </section>

                    <section className="admin-form-section">
                        <div className="admin-form-section-heading">
                            <h2>Status</h2>
                        </div>

                        <label className="admin-checkbox-field">
                            <input
                                type="checkbox"
                                name="acceptingClients"
                                checked={formData.acceptingClients}
                                onChange={handleFieldChange}
                            />

                            <span>Currently accepting new clients</span>
                        </label>

                        {isAdmin && (
                            <>
                                <label className="admin-checkbox-field">
                                    <input
                                        type="checkbox"
                                        name="publiclyVisible"
                                        checked={
                                            formData.publiclyVisible
                                        }
                                        onChange={handleFieldChange}
                                    />

                                    <span>
                                        Show this therapist on the public
                                        About page
                                    </span>
                                </label>

                                <label>
                                    <span>Display order</span>

                                    <input
                                        type="number"
                                        name="displayOrder"
                                        value={formData.displayOrder}
                                        onChange={handleFieldChange}
                                        min="0"
                                        required
                                    />
                                </label>
                            </>
                        )}
                    </section>
                </>
            )}

            <button
                type="submit"
                className="admin-primary-button"
                disabled={isSubmitting || isUploadingImage}
            >
                {isSubmitting ? "Saving..." : submitLabel}
            </button>
        </form>
    );
}

function ArrayInputSection({
                               title,
                               buttonLabel,
                               fieldName,
                               values,
                               multiline = false,
                               onAdd,
                               onRemove,
                               onChange,
                           }) {
    return (
        <div className="admin-array-field">
            <div className="admin-array-heading">
                <h3>{title}</h3>

                <button
                    type="button"
                    onClick={() => onAdd(fieldName)}
                >
                    {buttonLabel}
                </button>
            </div>

            {values.map((value, index) => (
                <div
                    className="admin-array-row"
                    key={`${fieldName}-${index}`}
                >
                    {multiline ? (
                        <textarea
                            rows="4"
                            value={value}
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
                            value={value}
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

export default TherapistForm;