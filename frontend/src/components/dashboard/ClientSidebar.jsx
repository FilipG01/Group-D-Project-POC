function ClientSidebar({ activeSection, onSectionChange, onLogout }){
    const navItems = [
        "Dashboard",
        "Chat",
        "Profile"
    ];


    return(
        <aside className="client-sidebar">
            <div className="client-sidebar-title">
                <h2>Root Therapy</h2>
                <p>Your Journey Matters</p>
            </div>

            <nav className="client-sidebar-nav">
                {navItems.map((item) => (
                    <button key={item} type="button"
                        className={item === activeSection
                            ? "client-sidebar-link client-sidebar-link--active"
                            : "client-sidebar-link" }
                            onClick={() => onSectionChange(item)} 
                    >{item}
                    </button>
                )
                )}
            </nav>

            <button type="button" className="client-sidebar-logout" onClick={onLogout}>
                Logout
            </button>
        </aside>
    );
}

export default ClientSidebar;