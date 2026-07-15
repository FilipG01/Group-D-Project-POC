import { useEffect, useState } from "react";
import { listMessages, sendMessage } from "../../api/apiMessaging";

function ClientMessages({ conversation }) {
    const [messages, setMessages] = useState([]);
    const [messageText, setMessageText] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [sending, setSending] = useState(false);

    useEffect(() => {
        async function loadMessages() {
            setLoading(true);
            setError("");

            try {
                const loadedMessages = await listMessages(conversation.id);
                setMessages(loadedMessages);
            } catch (err) {
                setError(err.message || "Could not load messages");
            } finally {
                setLoading(false);
            }
        }

        loadMessages();
    }, [conversation.id]);

    async function handleSendMessage(event) {
        event.preventDefault();

        if (!messageText.trim()) {
            return;
        }

        setSending(true);
        setError("");

        try {
            const savedMessage = await sendMessage(conversation.id, messageText.trim());
            setMessages((currentMessages) => [...currentMessages, savedMessage]);
            setMessageText("");
        } catch (err) {
            setError(err.message || "Could not send message");
        } finally {
            setSending(false);
        }
    }

    return (
        <section className="client-messages">
            <header>
                <h2>Chat</h2>
                <p>
                    Conversation with {conversation.therapistFirstName} {conversation.therapistLastName}
                </p>
            </header>

            {loading && <p>Loading messages...</p>}
            {error && <p>{error}</p>}

            <div className="client-messages-thread">
                {messages.map((message) => (
                    <article key={message.id} className="client-message">
                         <small>
                            {message.senderFirstName} {message.senderLastName}
                        </small>
                        <p>{message.ciphertext}</p>
                    </article>
                ))}
            </div>

            <form className="client-message-form" onSubmit={handleSendMessage}>
                <input
                    type="text"
                    value={messageText}
                    onChange={(event) => setMessageText(event.target.value)}
                    placeholder="Write a message..."
                />

                <button type="submit" disabled={sending}>
                    {sending ? "Sending..." : "Send"}
                </button>
            </form>
        </section>
    );
}

export default ClientMessages;