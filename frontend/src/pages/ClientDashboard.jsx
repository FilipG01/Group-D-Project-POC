import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { useEffect, useState } from "react";
import { listTherapists } from "../api/apiTherapist";
import { startConversation } from "../api/apiMessaging";

function ClientDashboard() {
    const navigate = useNavigate();
    const { user, logoutUser } = useAuth();

    async function handleLogout() {
        await logoutUser();
        navigate("/login");
    }

    const [therapists, setTherapists] = useState([]);
    const [loadingTherapists, setLoadingTherapists] = useState(true);
    const [therapistErr, setTherapistErr] = useState("");
    const [conversationMessage, setConversationMessage] = useState("");
    
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
            const conversation = await startConversation(therapistUserId);
            setConversationMessage(`Conversation is ready: ${conversation.id}`);
        }catch(err){
            setConversationMessage(err.message || "couldn't start conversation!");
        }
    }

    return (
        <main>
            <h2>Client Dashboard</h2>
            <ul>
                <li>Email: {user.email}</li>
                <li>Name: {user.firstName} {user.lastName}</li>
                <li>Role: {user.role}</li>
                <li>Status: {user.accountStatus}</li>
            </ul>

            <button type="button" onClick={handleLogout}>
                Logout
            </button>
        
        <section>
    <h3>Therapists</h3>

    {loadingTherapists && <p>Loading therapists...</p>}
    {therapistErr && <p>{therapistErr}</p>}
    {conversationMessage && <p>{conversationMessage}</p>}

    {therapists.map((therapist) => (
        <article key={therapist.userId}>
            <h4>{therapist.firstName} {therapist.lastName}</h4>
            <p>{therapist.specialisms}</p>
            <p>{therapist.bio}</p>

            <button
                type="button"
                onClick={() => handleStartConvo(therapist.userId)}
            >
                Start conversation
            </button>
        </article>
    ))}
</section>
        
        
        </main>
    );
}

export default ClientDashboard;