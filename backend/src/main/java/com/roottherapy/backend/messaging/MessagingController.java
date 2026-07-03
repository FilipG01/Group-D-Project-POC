package com.roottherapy.backend.messaging;

import com.roottherapy.backend.messaging.dto.ConversationResponse;
import com.roottherapy.backend.messaging.dto.MessageResponse;
import com.roottherapy.backend.messaging.dto.SendMessageRequest;
import com.roottherapy.backend.messaging.dto.StartConversationRequest;
import com.roottherapy.backend.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/")
public class MessagingController {
    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/conversations")
     public ConversationResponse startConversation(Authentication auth, @Valid @RequestBody StartConversationRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return messagingService.startConversation(userDetails.getUser(), req);
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> listMyConversations(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return messagingService.listMyConversations(userDetails.getUser());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageResponse> listMessage(Authentication auth, @PathVariable UUID conversationId){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return messagingService.listMessages(userDetails.getUser(), conversationId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public MessageResponse sendMessage(Authentication auth, @PathVariable UUID conversationId,
                                       @Valid @RequestBody SendMessageRequest req){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return messagingService.sendMessage(userDetails.getUser(), conversationId, req);
    }
}
