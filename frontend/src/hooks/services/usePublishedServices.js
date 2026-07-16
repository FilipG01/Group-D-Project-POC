import {
    useCallback,
    useEffect,
    useState,
} from "react";

import { getPublishedServices } from "../../api/servicesApi.js";

/*
 * Loads the published services shown on the public services page.
 */
export default function usePublishedServices() {
    const [services, setServices] = useState([]);
    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [reloadKey, setReloadKey] = useState(0);

    const loadServices = useCallback(async () => {
        setIsLoading(true);
        setError("");

        try {
            const data =
                await getPublishedServices();

            setServices(data || []);
        } catch (requestError) {
            console.error(requestError);

            setError(
                "Services could not be loaded. Please try again later."
            );
        } finally {
            setIsLoading(false);
        }
    }, []);

    // Load once on mount and again when the user retries.
    useEffect(() => {
        loadServices();
    }, [loadServices, reloadKey]);

    function reload() {
        setReloadKey((current) => current + 1);
    }

    return {
        services,
        isLoading,
        error,
        reload,
    };
}