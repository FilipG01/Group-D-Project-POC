import {
    useEffect,
    useState,
} from "react";
import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    createTherapistBlogPost,
    getTherapistBlogPost,
    submitTherapistBlogPost,
    updateTherapistBlogPost,
    uploadTherapistBlogImage,
} from "../../api/therapistBlogApi.js";

const EDITABLE_STATUSES = new Set([
    "DRAFT",
    "CHANGES_REQUESTED",
    "REJECTED",
]);

const EMPTY_FORM = {
    title: "",
    summary: "",
    body: "",
    featuredImageUrl: "",
    seoTitle: "",
    seoDescription: "",
    keywords: "",
};

/*
 * Turns a comma-separated keyword string into
 * a clean list without duplicate values.
 */
function parseKeywords(value) {
    const seen = new Set();

    return value
        .split(",")
        .map((keyword) => keyword.trim())
        .filter(Boolean)
        .filter((keyword) => {
            const normalized =
                keyword.toLowerCase();

            if (seen.has(normalized)) {
                return false;
            }

            seen.add(normalized);

            return true;
        });
}

/*
 * Manages the data and actions used by the
 * therapist blog editor page.
 */
export default function useTherapistBlogEditor() {
    const { postId } = useParams();
    const navigate = useNavigate();

    const isCreating = !postId;

    const [form, setForm] =
        useState(EMPTY_FORM);

    const [post, setPost] =
        useState(null);

    const [isLoading, setIsLoading] =
        useState(!isCreating);

    const [isSaving, setIsSaving] =
        useState(false);

    const [isUploading, setIsUploading] =
        useState(false);

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const [error, setError] =
        useState("");

    const [successMessage, setSuccessMessage] =
        useState("");

    const isEditable =
        isCreating ||
        EDITABLE_STATUSES.has(post?.status);

    /*
     * Load the existing post when editing.
     * New posts start with an empty form.
     */
    useEffect(() => {
        if (isCreating) {
            return;
        }

        let isCurrent = true;

        async function loadPost() {
            setIsLoading(true);
            setError("");

            try {
                const data =
                    await getTherapistBlogPost(postId);

                if (!isCurrent) {
                    return;
                }

                setPost(data);

                setForm({
                    title: data.title || "",
                    summary: data.summary || "",
                    body: data.body || "",
                    featuredImageUrl:
                        data.featuredImageUrl || "",
                    seoTitle: data.seoTitle || "",
                    seoDescription:
                        data.seoDescription || "",
                    keywords:
                        data.keywords?.join(", ") || "",
                });
            } catch (requestError) {
                console.error(requestError);

                if (isCurrent) {
                    setError(
                        requestError.message ||
                        "The blog post could not be loaded."
                    );
                }
            } finally {
                if (isCurrent) {
                    setIsLoading(false);
                }
            }
        }

        loadPost();

        return () => {
            isCurrent = false;
        };
    }, [isCreating, postId]);

    function handleChange(event) {
        const { name, value } = event.target;

        setForm((current) => ({
            ...current,
            [name]: value,
        }));
    }

    function buildPayload() {
        return {
            title: form.title.trim(),
            summary: form.summary.trim(),
            body: form.body.trim(),
            featuredImageUrl:
                form.featuredImageUrl.trim() || null,
            seoTitle:
                form.seoTitle.trim() || null,
            seoDescription:
                form.seoDescription.trim() || null,
            keywords: parseKeywords(
                form.keywords
            ),
        };
    }

    async function handleSave(event) {
        event.preventDefault();

        setError("");
        setSuccessMessage("");

        if (!form.title.trim()) {
            setError("A title is required.");
            return;
        }

        setIsSaving(true);

        try {
            if (isCreating) {
                const created =
                    await createTherapistBlogPost(
                        buildPayload()
                    );

                navigate(
                    `/therapist/blog/${created.id}/edit`,
                    {
                        replace: true,
                        state: {
                            message:
                                "Draft created successfully.",
                        },
                    }
                );

                return;
            }

            const updated =
                await updateTherapistBlogPost(
                    postId,
                    {
                        ...buildPayload(),
                        version: post.version,
                    }
                );

            setPost(updated);

            setSuccessMessage(
                "Draft saved successfully."
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The blog post could not be saved."
            );
        } finally {
            setIsSaving(false);
        }
    }

    async function handleImageUpload(event) {
        const file =
            event.target.files?.[0];

        event.target.value = "";

        if (!file) {
            return;
        }

        setError("");
        setSuccessMessage("");
        setIsUploading(true);

        try {
            const uploaded =
                await uploadTherapistBlogImage(file);

            const uploadedUrl =
                uploaded.url ||
                uploaded.imageUrl ||
                uploaded.fileUrl;

            if (!uploadedUrl) {
                setError(
                    "The upload completed but no image URL was returned."
                );
                return;
            }

            setForm((current) => ({
                ...current,
                featuredImageUrl: uploadedUrl,
            }));

            setSuccessMessage(
                "Image uploaded. Save the post to keep this change."
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The image could not be uploaded."
            );
        } finally {
            setIsUploading(false);
        }
    }

    function removeImage() {
        setForm((current) => ({
            ...current,
            featuredImageUrl: "",
        }));
    }

    async function handleSubmitForReview() {
        if (!post) {
            return;
        }

        const confirmed = window.confirm(
            "Submit this post for admin review? You will not be able to edit it while it is submitted."
        );

        if (!confirmed) {
            return;
        }

        setError("");
        setSuccessMessage("");
        setIsSubmitting(true);

        try {
            const submitted =
                await submitTherapistBlogPost(
                    post.id,
                    post.version
                );

            setPost(submitted);

            setSuccessMessage(
                "The post was submitted for admin review."
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The post could not be submitted."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return {
        form,
        post,

        isCreating,
        isEditable,
        isLoading,
        isSaving,
        isUploading,
        isSubmitting,

        error,
        successMessage,

        handleChange,
        handleSave,
        handleImageUpload,
        handleSubmitForReview,
        removeImage,
    };
}