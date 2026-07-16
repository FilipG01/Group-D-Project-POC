import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth.js";
import { useEffect, useState } from "react";
import { listTherapists } from "../api/apiTherapist";
import { startConversation, listConversations } from "../api/apiMessaging";

import ClientSidebar from "../components/dashboard/ClientSidebar"
import ClientMessages from "../components/dashboard/ClientMessages";
import TherapistDirectory from "../components/dashboard/TherapistDirectory";

import "../styles/ClientSidebar.css"
import "../styles/ClientDashboard.css"

function ClientDashboard() {
    const navigate = useNavigate();
    const { user, logoutUser } = useAuth();
    const [activeSection, setActiveSection] = useState("Dashboard");

    async function handleLogout() {
        await logoutUser();
        navigate("/login");
    }

    const [therapists, setTherapists] = useState([]);
    const [loadingTherapists, setLoadingTherapists] = useState(true);
    const [therapistErr, setTherapistErr] = useState("");
    const [conversationMessage, setConversationMessage] = useState("");
    const [conversation, setConversation] = useState(null);
    
    useEffect(() => {
        async function loadTherapists(){
            try{
                const loadedTherapists = await listTherapists();
                setTherapists(loadedTherapists);
            }catch(err){
                setTherapistErr(err.message || "couldn't load therapists!");
            }finally{
                setLoadingTherapists(false);
            }
        }
        loadTherapists();
    }, []);

    
    async function handleStartConvo(therapistUserId){
        setConversationMessage("");

        try{
            const newConversation = await startConversation(therapistUserId);
            setConversation(newConversation);
            setActiveSection("Chat");
        }catch(err){
            setConversationMessage(err.message || "couldn't start conversation!");
        }
    }

    useEffect(() => {
    async function loadConversations(){
        try{
            const conversations = await listConversations();
            
            if(conversations.length > 0){
                setConversation(conversations[0]);
            }
        }catch(err){
            console.error(err)
        }
    }
        loadConversations();
    }, []);

    function renderDashboardSection(){
        return(
            <>
            <h2>main dashboard</h2>
            <TherapistDirectory
            therapists={therapists}
            loadingTherapists={loadingTherapists}
            therapistErr={therapistErr}
            conversationMessage={conversationMessage}
            conversation={conversation}
            onStartConversation={handleStartConvo}
            onOpenMessages={() => setActiveSection("Chat")}
            />
            </>
        );
    }

    function renderMessageSection(){
        if(!conversation){
            return(
                <>
                    <h2>Chat</h2>
                    <p>Select Therapist from the Dashboard</p>
                </>
            );
        }
        return <ClientMessages conversation={conversation} />
    }

    function renderProfileSection(){
        return(
            <>
            <h2>Profile</h2>
            <ul>
                <li>Email: {user.email}</li>
                <li>Name: {user.firstName} {user.lastName}</li>
                <li>Role: {user.role}</li>
                <li>Status: {user.accountStatus}</li>
            </ul>
        </>
        );
    }

    function renderActiveSection(){
        if(activeSection === "Chat"){
            return renderMessageSection();
        }
        if(activeSection === "Profile"){
            return renderProfileSection();
        }

        return renderDashboardSection();
    }

    return (
        
        <div className="client-dashboard-layout">
        <ClientSidebar
            activeSection={activeSection}
            onSectionChange={setActiveSection}
            onLogout={handleLogout}
            />

        <main className="client-dashboard-main">
            {renderActiveSection()}
        </main>
        </div>
    );
}

export default ClientDashboard;