import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { listConversations } from "../../api/apiMessaging";
import ClientMessages from "../../components/dashboard/ClientMessages";
import "../../styles/ClientDashboard.css";
import "../../styles/therapistChat.css";

function TherapistChat() {
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadConversations() {
            setLoading(true);
            setError("");

            try {
                const loadedConversations = await listConversations();
                setConversations(loadedConversations);

                if (loadedConversations.length > 0) {
                    setSelectedConversation(loadedConversations[0]);
                }
            } catch (err) {
                setError(err.message || "Could not load conversations");
            } finally {
                setLoading(false);
            }
        }

        loadConversations();
    }, []);

    return (
        <main className="therapist-chat-page">
            <section className="therapist-chat-shell">
                <aside className="therapist-chat-sidebar">
                    <div className="therapist-chat-sidebar-header">
                        <Link to="/therapist" className="therapist-chat-back">
                            Back to dashboard
                        </Link>
                        <h1>Clients</h1>
                    </div>

                    {loading && <p className="therapist-chat-status">Loading conversations...</p>}
                    {error && <p className="therapist-chat-status therapist-chat-error">{error}</p>}

                    <div className="therapist-chat-client-list">
                        {conversations.map((conversation) => {
                            const isSelected = selectedConversation?.id === conversation.id;

                            return (
                                <button
                                    key={conversation.id}
                                    type="button"
                                    className={
                                        isSelected
                                            ? "therapist-chat-client therapist-chat-client--active"
                                            : "therapist-chat-client"
                                    }
                                    onClick={() => setSelectedConversation(conversation)}
                                >
                                    <span>
                                        {conversation.clientFirstName} {conversation.clientLastName}
                                    </span>
                                </button>
                            );
                        })}
                    </div>
                </aside>

                <div className="therapist-chat-main">
                    {selectedConversation ? (
                        <ClientMessages conversation={selectedConversation} />
                    ) : (
                        <section className="therapist-chat-empty">
                            <h2>No conversations yet</h2>
                            <p>Client conversations will appear here once clients start chatting.</p>
                        </section>
                    )}
                </div>
            </section>
        </main>
    );
}

export default TherapistChat;
