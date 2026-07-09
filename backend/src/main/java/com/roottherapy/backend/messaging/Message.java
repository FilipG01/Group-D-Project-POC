package com.roottherapy.backend.messaging;


import com.roottherapy.backend.users.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ciphertext;

    @Column(name = "encryption_algorithm", nullable = false, length = 100)
    private String encryptionAlgorithm;

    @Column(name = "iv", nullable = false, columnDefinition = "TEXT")
    private String iv;

    @Column(name = "auth_tag", columnDefinition = "TEXT")
    private String authTag;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public Message(Conversation conversation, User sender, String ciphertext, String encryptionAlgorithm, String iv, String authTag) {
        this.conversation = conversation;
        this.sender = sender;
        this.ciphertext =  ciphertext;
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.iv = iv;
        this.authTag = authTag;
    }
}
