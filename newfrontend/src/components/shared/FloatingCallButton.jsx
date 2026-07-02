import { FaPhoneAlt } from "react-icons/fa";
import "../../styles/floatingCallButton.css";

function FloatingCallButton() {
    return (
        <a
            href="tel:+353833036099"
            className="floating-call-button"
            aria-label="Call Root Therapy"
        >
            <FaPhoneAlt />
        </a>
    );
}

export default FloatingCallButton;