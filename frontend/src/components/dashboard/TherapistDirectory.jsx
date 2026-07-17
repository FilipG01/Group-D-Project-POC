function TherapistDirectory({
    therapists,
    loadingTherapists,
    therapistErr,
    conversationMessage,
    conversation,
    onStartConversation,
    onOpenMessages,
}) {
    const visibleTherapists = conversation
        ? therapists.filter((therapist) => therapist.userId === conversation.therapistUserId)
        : therapists;

    return (
        <section>
            <h2>{conversation ? "Your Therapist" : "Available Therapists"}</h2>

            {loadingTherapists && <p>Loading therapists...</p>}
            {therapistErr && <p>{therapistErr}</p>}
            {conversationMessage && <p>{conversationMessage}</p>}

            {visibleTherapists.map((therapist) => (
                <article key={therapist.userId}>
                    <h3>{therapist.firstName} {therapist.lastName}</h3>
                    <p>{therapist.bio}</p>

                    {conversation ? (
                        <button type="button" onClick={onOpenMessages}>
                            Chat
                        </button>
                    ) : (
                        <button
                            type="button"
                            onClick={() => onStartConversation(therapist.userId)}
                        >
                            Start Chat
                        </button>
                    )}
                </article>
            ))}
        </section>
    );
}

export default TherapistDirectory;