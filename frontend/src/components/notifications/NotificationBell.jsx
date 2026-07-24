import { useEffect, useState } from "react";
import { Menu } from "@mantine/core";
import { useNavigate } from "react-router-dom";
import {
    getNotifications,
    getUnreadNotificationCount,
    markNotificationRead,
} from "../../api/notificationsApi.js";
import "../../styles/notifications/notificationBell.css";

function NotificationBell() {
    const navigate = useNavigate();
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(false);

    async function refreshNotifications() {
        setLoading(true);

        try {
            const [items, countResponse] = await Promise.all([
                getNotifications(),
                getUnreadNotificationCount(),
            ]);

            setNotifications(items);
            setUnreadCount(countResponse.unreadCount);
        } catch (error) {
            console.error("Could not load notifications", error);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        refreshNotifications();

        // Polling keeps the POC simple and can later be replaced by WebSockets.
        const intervalId = window.setInterval(refreshNotifications, 30000);

        return () => window.clearInterval(intervalId);
    }, []);

    async function openNotification(notification) {
        if (!notification.read) {
            await markNotificationRead(notification.id);
        }

        setUnreadCount((current) => Math.max(0, current - (notification.read ? 0 : 1)));
        setNotifications((current) =>
            current.map((item) =>
                item.id === notification.id
                    ? { ...item, read: true }
                    : item
            )
        );

        if (notification.linkUrl) {
            navigate(notification.linkUrl);
        }
    }

    return (
        <Menu
            position="bottom-end"
            offset={10}
            shadow="md"
            width={360}
            onOpen={refreshNotifications}
        >
            <Menu.Target>
                <button
                    type="button"
                    className="notification-bell-button"
                    aria-label={`Open notifications. ${unreadCount} unread.`}
                >
                    <span className="notification-bell-icon" aria-hidden="true">
                        🔔
                    </span>

                    {unreadCount > 0 && (
                        <span className="notification-bell-count">
                            {unreadCount > 99 ? "99+" : unreadCount}
                        </span>
                    )}
                </button>
            </Menu.Target>

            <Menu.Dropdown className="notification-menu">
                <div className="notification-menu-header">
                    <strong>Notifications</strong>
                    <span>{unreadCount} unread</span>
                </div>

                {loading && notifications.length === 0 && (
                    <p className="notification-menu-empty">Loading notifications...</p>
                )}

                {!loading && notifications.length === 0 && (
                    <p className="notification-menu-empty">No notifications yet.</p>
                )}

                <div className="notification-menu-list">
                    {notifications.map((notification) => (
                        <button
                            key={notification.id}
                            type="button"
                            className={`notification-menu-item ${notification.read ? "is-read" : "is-unread"}`}
                            onClick={() => openNotification(notification)}
                        >
                            <span className="notification-menu-item-title">
                                {notification.title}
                            </span>

                            <span className="notification-menu-item-message">
                                {notification.message}
                            </span>

                            <span className="notification-menu-item-date">
                                {new Date(notification.createdAt).toLocaleString()}
                            </span>
                        </button>
                    ))}
                </div>
            </Menu.Dropdown>
        </Menu>
    );
}

export default NotificationBell;
