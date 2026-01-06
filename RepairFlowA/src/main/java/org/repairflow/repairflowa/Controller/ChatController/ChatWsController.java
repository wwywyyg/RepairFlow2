package org.repairflow.repairflowa.Controller.ChatController;

import jakarta.validation.Valid;
import org.repairflow.repairflowa.Config.Ws.WsPrincipal;
import org.repairflow.repairflowa.Exception.Response.ChatMessageResponse;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessage;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageMapper;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageRequest;
import org.repairflow.repairflowa.Repository.ChatMessageRepository;
import org.repairflow.repairflowa.Services.ChatServices.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

/**
 * @author guangyang
 * @date 1/2/26 22:59
 * @description TODO: Description
 */

@Controller
public class ChatWsController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWsController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // 客户端发送到：/app/ticket/{ticketId}/message
    @MessageMapping("/ticket/{ticketId}/message")
    public void sendMessage(@DestinationVariable Long ticketId,
                            @Valid @Payload ChatMessageRequest req,
                            Authentication authentication) {

        WsPrincipal principal = (WsPrincipal) authentication.getPrincipal();

        ChatMessage saved = chatService.saveMessage(
                ticketId,
                principal,
                req.content(),
                req.chatMessageType()
        );


        ChatMessageResponse payload = ChatMessageMapper.toResponse(saved, principal.getRole());


        messagingTemplate.convertAndSend("/topic/ticket/" + ticketId, payload);
    }
}
