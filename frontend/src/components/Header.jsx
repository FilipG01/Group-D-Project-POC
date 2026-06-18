import { NavLink, Link } from 'react-router-dom'
import './Header.css'

function Header() {
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
                    <Link to="/contact" className="booking-button">Book a Session →</Link>
                </div>
            </div>
        </header>
    )
}

export default Header
