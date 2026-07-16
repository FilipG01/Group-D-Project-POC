import {
    useCallback,
    useEffect,
    useState,
} from "react";

import { getPublicTherapists } from "../../api/therapistsApi.js";

/*
 * Loads the therapist profiles shown
 * on the public About page.
 */
export default function usePublicTherapists() {
    const [therapists, setTherapists] =
        useState([]);

    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [reloadKey, setReloadKey] = useState(0);

    const loadTherapists = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data =
                await getPublicTherapists();

            setTherapists(data || []);
        } catch (requestError) {
            console.error(requestError);

            setError(
                "Therapist profiles could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, []);

    // Load profiles when the section first appears
    // and again when the user retries.
    useEffect(() => {
        loadTherapists();
    }, [loadTherapists, reloadKey]);

    function reload() {
        setReloadKey((current) => current + 1);
    }

    return {
        therapists,
        isLoading,
        error,
        reload,
    };
}