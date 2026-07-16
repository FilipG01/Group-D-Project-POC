import {
    useCallback,
    useEffect,
    useState,
} from "react";

import { getServiceBySlug } from "../../api/servicesApi.js";

/*
 * Loads one published service using its URL slug.
 */
export default function usePublishedService(serviceSlug) {
    const [service, setService] = useState(null);
    const [isLoading, setIsLoading] =
        useState(true);

    const [notFound, setNotFound] =
        useState(false);

    const [error, setError] = useState("");
    const [reloadKey, setReloadKey] = useState(0);

    const loadService = useCallback(async () => {
        setIsLoading(true);
        setError("");
        setNotFound(false);
        setService(null);

        try {
            const data =
                await getServiceBySlug(serviceSlug);

            setService(data);
        } catch (requestError) {
            console.error(requestError);

            if (requestError.status === 404) {
                setNotFound(true);
                return;
            }

            setError(
                requestError.message ||
                "The service could not be loaded."
            );
        } finally {
            setIsLoading(false);
        }
    }, [serviceSlug]);

    // Reload when the slug changes or the user retries.
    useEffect(() => {
        loadService();
    }, [loadService, reloadKey]);

    function reload() {
        setReloadKey((current) => current + 1);
    }

    return {
        service,
        isLoading,
        notFound,
        error,
        reload,
    };
}