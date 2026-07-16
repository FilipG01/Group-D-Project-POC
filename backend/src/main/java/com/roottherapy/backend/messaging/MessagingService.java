package com.roottherapy.backend.messaging;


import com.roottherapy.backend.messaging.dto.ConversationResponse;
import com.roottherapy.backend.messaging.dto.MessageResponse;
import com.roottherapy.backend.messaging.dto.SendMessageRequest;
import com.roottherapy.backend.messaging.dto.StartConversationRequest;
import com.roottherapy.backend.profile.therapist.TherapistProfileRepository;
import com.roottherapy.backend.users.User;
import com.roottherapy.backend.users.UserRepository;
import com.roottherapy.backend.users.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TherapistProfileRepository therapistProfileRepository;

    public MessagingService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            TherapistProfileRepository therapistProfileRepository
    ){
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.therapistProfileRepository = therapistProfileRepository;
    }

    public void ensureClient(User user) {
        if (user.getRole() != UserRole.CLIENT) {
            throw new AccessDeniedException("only clients can start a conversation");
        }
    }


    public ConversationResponse startConversation(User currentUser, StartConversationRequest req){
        ensureClient(currentUser);
        //only clients can start conversations with therapis
        //and only with therapists that are accepting clients

        User therapist = userRepository.findById(req.therapistUserId())
                .orElseThrow(() -> new IllegalArgumentException("therapist could not be found!"));

        if(therapist.getRole() != UserRole.THERAPIST){
            throw new IllegalArgumentException("user is not a therapist");
        }
        if(therapistProfileRepository.findByUserIdAndAcceptingClientsTrue(therapist.getId()).isEmpty()){
            throw new IllegalArgumentException("this therapist is not accepting clients at the moment!");
        }

        Optional<Conversation> existingActiveConversation =
                conversationRepository.findFirstByClientIdAndStatus(
                        currentUser.getId(),
                        ConversationStatus.ACTIVE
                );

        if (existingActiveConversation.isPresent()) {
            Conversation existingConversation = existingActiveConversation.get();

            if (!existingConversation.getTherapist().getId().equals(therapist.getId())) {
                throw new IllegalArgumentException("you already have an active conversation with a therapist");
            }

            return ConversationResponse.from(existingConversation);
        }

        Conversation conversation = conversationRepository.save(
                new Conversation(currentUser, therapist)
        );

        return ConversationResponse.from(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listMyConversations(User currentUser){
        return conversationRepository
        .findByClientIdOrTherapistId(currentUser.getId(), currentUser.getId())
        .stream().map(ConversationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> listMessages(User currentUser, UUID conversationId){
        Conversation conversation = getConversationForParticipant(currentUser, conversationId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse sendMessage(User currentUser, UUID conversationId, SendMessageRequest req){
        Conversation conversation = getConversationForParticipant(currentUser, conversationId);
        if(conversation.getStatus() != ConversationStatus.ACTIVE){
            throw new IllegalArgumentException("this conversation is closed");
        }
        Message message = new Message(
                conversation,
                currentUser,
                req.ciphertext(),
                req.encryptionAlgorithm(),
                req.iv(),
                req.authTag()
        );
        Message savedMessage = messageRepository.save(message);
        return MessageResponse.from(savedMessage);
    }

    //only clients and therapists can view the conversations they are assign to,
    //admin doesn't even have access to there conversations
    private Conversation getConversationForParticipant(User user, UUID conversationId){
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("conversation could not be found!"));
        boolean isClient = conversation.getClient().getId().equals(user.getId());
        boolean isTherapist = conversation.getTherapist().getId().equals(user.getId());

        if(!isClient && !isTherapist){
            throw new AccessDeniedException("You don't have access to this conversation");

        }
    return conversation;
        }
}