import { NavLink, Link, useNavigate } from 'react-router-dom';
import { Avatar, Menu } from "@mantine/core";
import '../../styles/shared/header.css'

function Header() {
    const navigate = useNavigate();
    
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
                                className="account-avatar" aria-label="Open account menu"
                            />
                        </Menu.Target>

                        <Menu.Dropdown>
                            <Menu.Item onClick={() => navigate("/login")}>Login</Menu.Item>
                            <Menu.Item onClick={() => navigate("/register")}>Register</Menu.Item>
                            <Menu.Item onClick={() => navigate("/dashboard")}>Dashboard</Menu.Item>
                        </Menu.Dropdown>
                    </Menu>
                </div>
            </div>
        </header>
    );
}

export default Header
