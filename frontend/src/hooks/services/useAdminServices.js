import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    getAdminServices,
    reorderServices,
    setServiceArchived,
    setServicePublished,
} from "../../api/servicesApi.js";

/*
 * Manages the service list and admin actions.
 * The page only needs to display the returned data.
 */
export default function useAdminServices() {
    const [services, setServices] = useState([]);
    const [isLoading, setIsLoading] =
        useState(true);

    const [error, setError] = useState("");
    const [busyServiceId, setBusyServiceId] =
        useState(null);

    const loadServices = useCallback(
        async ({ showLoading = true } = {}) => {
            if (showLoading) {
                setIsLoading(true);
            }

            setError("");

            try {
                const data = await getAdminServices();
                setServices(data || []);
            } catch (requestError) {
                console.error(requestError);

                setError(
                    requestError.message ||
                    "The services could not be loaded. Make sure you are logged in as an admin."
                );
            } finally {
                if (showLoading) {
                    setIsLoading(false);
                }
            }
        },
        []
    );

    // Load the service list when the page first opens.
    useEffect(() => {
        loadServices();
    }, [loadServices]);

    async function togglePublished(service) {
        setBusyServiceId(service.id);
        setError("");

        try {
            const updatedService =
                await setServicePublished(
                    service.id,
                    !service.published
                );

            setServices((currentServices) =>
                currentServices.map(
                    (currentService) =>
                        currentService.id ===
                        updatedService.id
                            ? updatedService
                            : currentService
                )
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The service publication status could not be updated."
            );
        } finally {
            setBusyServiceId(null);
        }
    }

    async function toggleArchived(service) {
        const action = service.archived
            ? "restore"
            : "archive";

        const confirmed = window.confirm(
            `Are you sure you want to ${action} "${service.title}"?`
        );

        if (!confirmed) {
            return;
        }

        setBusyServiceId(service.id);
        setError("");

        try {
            const updatedService =
                await setServiceArchived(
                    service.id,
                    !service.archived
                );

            setServices((currentServices) =>
                currentServices.map(
                    (currentService) =>
                        currentService.id ===
                        updatedService.id
                            ? updatedService
                            : currentService
                )
            );
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The service archive status could not be updated."
            );
        } finally {
            setBusyServiceId(null);
        }
    }

    async function moveService(
        serviceIndex,
        direction
    ) {
        const targetIndex =
            direction === "up"
                ? serviceIndex - 1
                : serviceIndex + 1;

        if (
            targetIndex < 0 ||
            targetIndex >= services.length
        ) {
            return;
        }

        const reordered = [...services];

        [
            reordered[serviceIndex],
            reordered[targetIndex],
        ] = [
            reordered[targetIndex],
            reordered[serviceIndex],
        ];

        setBusyServiceId(
            services[serviceIndex].id
        );

        setError("");

        try {
            const updatedServices =
                await reorderServices(reordered);

            setServices(updatedServices || []);
        } catch (requestError) {
            console.error(requestError);

            setError(
                requestError.message ||
                "The service order could not be updated."
            );

            // Reload the saved order if the update failed.
            await loadServices({
                showLoading: false,
            });
        } finally {
            setBusyServiceId(null);
        }
    }

    return {
        services,
        isLoading,
        error,
        busyServiceId,
        loadServices,
        togglePublished,
        toggleArchived,
        moveService,
    };
}