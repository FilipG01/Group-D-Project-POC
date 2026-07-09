-- V2__create_conversations_and_messages.sql

-- created tables:
-- conversations
-- messages

-- created triggers
-- updated_at trigger

CREATE TABLE conversations(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_user_id UUID NOT NULL,
    therapist_user_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_conversations_client_user FOREIGN KEY (client_user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conversations_therapist_user FOREIGN KEY (therapist_user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_conversation_status CHECK(
        status IN ('ACTIVE', 'CLOSED')
        ),
    CONSTRAINT chk_conversations_different_users CHECK(
        client_user_id <> therapist_user_id
        ),
    CONSTRAINT uq_conversations_client_therapist UNIQUE(
            client_user_id,
            therapist_user_id
        )
);

CREATE INDEX idx_conversations_client_user_id ON conversations (client_user_id);
CREATE INDEX idx_conversations_therapist_user_id ON conversations (therapist_user_id);
CREATE INDEX idx_conversations_status ON conversations (status);

-- messages table
CREATE TABLE messages(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,
    sender_user_id UUID NOT NULL,
    body TEXT NOT NULL,

    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id)
                     REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender_user FOREIGN KEY (sender_user_id)
                     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_messages_body_not_blank CHECK(
        LENGTH(TRIM(body)) > 0
        )
);

CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_user_id ON messages(sender_user_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- updated_at trigger
CREATE TRIGGER trg_conversations_updated_at
    BEFORE UPDATE ON conversations FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();