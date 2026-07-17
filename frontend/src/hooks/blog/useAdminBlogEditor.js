import {
    useEffect,
    useState,
} from "react";
import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    createAdminBlogPost,
    getAdminBlogPost,
    publishAdminBlogPost,
    updateAdminBlogPost,
    uploadAdminBlogImage,
} from "../../api/adminBlogApi.js";

const EMPTY_FORM = {
    title: "",
    slug: "",
    summary: "",
    body: "",
    featuredImageUrl: "",
    seoTitle: "",
    seoDescription: "",
    keywords: "",
};

/*
 * Converts comma-separated keywords into a clean list.
 * Duplicate keywords are removed.
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
 * Manages the data and actions used by
 * the admin blog editor page.
 */
export default function useAdminBlogEditor() {
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

    const [isPublishing, setIsPublishing] =
        useState(false);

    const [error, setError] =
        useState("");

    const [successMessage, setSuccessMessage] =
        useState("");

    /*
     * Load an existing post when the page
     * is opened in edit mode.
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
                    await getAdminBlogPost(postId);

                if (!isCurrent) {
                    return;
                }

                setPost(data);

                setForm({
                    title: data.title || "",
                    slug: data.slug || "",
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
            slug: form.slug.trim(),
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

        if (
            !isCreating &&
            !form.slug.trim()
        ) {
            setError("A slug is required.");
            return;
        }

        setIsSaving(true);

        try {
            if (isCreating) {
                const created =
                    await createAdminBlogPost(
                        buildPayload()
                    );

                navigate(
                    `/admin/blog/${created.id}/edit`,
                    {
                        replace: true,
                    }
                );

                return;
            }

            const updated =
                await updateAdminBlogPost(
                    postId,
                    {
                        ...buildPayload(),
                        version: post.version,
                    }
                );

            setPost(updated);

            setForm((current) => ({
                ...current,
                slug:
                    updated.slug ||
                    current.slug,
            }));

            setSuccessMessage(
                "Blog post saved successfully."
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
                await uploadAdminBlogImage(file);

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

    async function handlePublish() {
        if (!post) {
            return;
        }

        const confirmed = window.confirm(
            `Publish "${post.title}" now?`
        );

        if (!confirmed) {
            return;
        }

        setError("");
        setSuccessMessage("");
        setIsPublishing(true);

        try {
            const published =
                await publishAdminBlogPost(
                    post.id,
                    post.version
                );

            setPost(published);

            setSuccessMessage(
                "The blog post was published."
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The blog post could not be published."
            );
        } finally {
            setIsPublishing(false);
        }
    }

    return {
        form,
        post,

        isCreating,
        isLoading,
        isSaving,
        isUploading,
        isPublishing,

        error,
        successMessage,

        handleChange,
        handleSave,
        handleImageUpload,
        handlePublish,
        removeImage,
    };
}