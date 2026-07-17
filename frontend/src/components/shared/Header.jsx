import { useEffect, useState } from "react";
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { Avatar, Menu } from "@mantine/core";
import { useAuth } from "../../auth/useAuth.js";
import { getOwnTherapistProfile } from "../../api/therapistsApi.js";
import { getImageUrl } from "../../utils/imageUrl.js";
import '../../styles/shared/header.css'

function Header() {
    const navigate = useNavigate();
    const { user, logoutUser } = useAuth();
    const [therapistProfileImage, setTherapistProfileImage] = useState("");

    useEffect(() => {
        let isCancelled = false;

        async function loadTherapistProfileImage() {
            if (user?.role !== "THERAPIST") {
                setTherapistProfileImage("");
                return;
            }

            try {
                const profile = await getOwnTherapistProfile();

                if (!isCancelled) {
                    setTherapistProfileImage(profile.profileImageUrl || "");
                }
            } catch (error) {
                console.error(error);

                if (!isCancelled) {
                    setTherapistProfileImage("");
                }
            }
        }

        loadTherapistProfileImage();

        return () => {
            isCancelled = true;
        };
    }, [user?.role]);

    const initials = user
        ? `${user.firstName?.[0] || ""}${user.lastName?.[0] || ""}`.toUpperCase()
        : "";

    const avatarImage =
        user?.role === "THERAPIST" && therapistProfileImage
            ? getImageUrl(therapistProfileImage)
            : "";

    function getDashboardPath() {
        if (user?.role === "ADMIN") {
            return "/admin";
        }

        if (user?.role === "THERAPIST") {
            return "/therapist";
        }

        return "/dashboard";
    }

    async function handleLogout() {
        await logoutUser();
        navigate("/login");
    }
    
    return (
        <header className="header-main">
            <div className="header-inner">

                <Link to="/" className="logo-container" aria-label="Root Therapy home">
                    <span className="logo" aria-hidden="true"></span>
                    <h1 className="header-title">Root Therapy</h1>
                </Link>

                <nav className="header-nav">
                    <NavLink to="/">Home</NavLink>
                    <NavLink to="/about">About</NavLink>
                    <NavLink to="/services">Services</NavLink>
                    <NavLink to="/blog">Blog</NavLink>
                    <NavLink to="/contact">Contact</NavLink>
                </nav>

                <div className="button-container">
                    <Link to="/contact" className="booking-button">Book a Session</Link>
                    
                    <Menu position="bottom-end" offset={8} shadow="md" width={120}>
                        <Menu.Target>
                            <Avatar component="button" type="button"
                                radius="xl" color="#DDC4AD" variant="filled" 
                                src={avatarImage || undefined}
                                className="account-avatar" aria-label="Open account menu"
                            >
                                {!avatarImage && initials}
                            </Avatar>
                        </Menu.Target>

                        <Menu.Dropdown>
                            {user ? (
                                <>
                                    <Menu.Item onClick={() => navigate(getDashboardPath())}>Dashboard</Menu.Item>
                                    <Menu.Item onClick={handleLogout}>Logout</Menu.Item>
                                </>
                            ) : (
                                <>
                                    <Menu.Item onClick={() => navigate("/login")}>Login</Menu.Item>
                                    <Menu.Item onClick={() => navigate("/register")}>Register</Menu.Item>
                                </>
                            )}
                        </Menu.Dropdown>
                    </Menu>
                </div>
            </div>
        </header>
    );
}

export default Header
