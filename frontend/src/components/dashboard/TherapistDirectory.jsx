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
        <section className="therapist-directory">
            <h2 className="therapist-directory-heading">{conversation ? "Your Therapist" : "Available Therapists"}</h2>

            {loadingTherapists && <p>Loading therapists...</p>}
            {therapistErr && <p>{therapistErr}</p>}
            {conversationMessage && <p>{conversationMessage}</p>}

        <div className="therapist-card-container">
            {visibleTherapists.map((therapist) => (
                <article className="therapist-card" key={therapist.userId}>

                    <div className="therapist-card-header">
                        <div className="therapist-avatar">
                            {therapist.firstName.charAt(0).toUpperCase()}
                        </div>
                      

                     <div>
                    <h3>{therapist.firstName} {therapist.lastName}</h3>
                    
                </div>
             </div>

             <div className="therapist-card-details">

                <div className="therapist-info-section">
                    <h4>Experience:</h4>
                    <p>
                        {therapist.yearsExperience} years of experience
                    </p>
                </div>

            <div className="therapist-info-section">
                    <h4>About:</h4>

                <p>
                    {therapist.bio}
                </p>
            </div>

            <div className="therapist-info-section">
                <h4>Qualifications:</h4>
                <p>       
                    {therapist.qualifications}
                </p>
             </div>
        </div>
                    {conversation ? (

                        <button 
                        className="therapist-button"
                        type="button"
                        onClick={onOpenMessages}
                        >                      
                            Chat
                        </button>
                    ) : (
                        <button
                            className="therapist-button"
                            type="button"
                            onClick={() => onStartConversation(therapist.userId)}
                        >
                            Start Chat
                        </button>
                    )}
                </article>
            ))}
            </div>

        </section>
    );
}

export default TherapistDirectory;