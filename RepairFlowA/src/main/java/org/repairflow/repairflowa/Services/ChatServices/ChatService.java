package org.repairflow.repairflowa.Services.ChatServices;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.repairflow.repairflowa.Config.Ws.WsPrincipal;
import org.repairflow.repairflowa.Exception.BusinessException;
import org.repairflow.repairflowa.Exception.ErrorCode;
import org.repairflow.repairflowa.Exception.Response.ChatMessageResponse;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessage;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageMapper;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageType;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.ChatMessageRepository;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author guangyang
 * @date 1/2/26 16:22
 * @description TODO: Description
 */
@Service
@Slf4j
public class ChatService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public ChatService(TicketRepository ticketRepository, UserRepository userRepository, ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Transactional
    public ChatMessage saveMessage(Long ticketId, WsPrincipal senderPrincipal , String content ,ChatMessageType type) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()-> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        Long senderId = senderPrincipal.getUserId();

        boolean isAdmin = "ADMIN".equalsIgnoreCase(senderPrincipal.getRole());

        Long customer_id = ticket.getCustomer()!=null?ticket.getCustomer().getId():null;
        Long employee_id = ticket.getEmployee()!=null?ticket.getEmployee().getId():null;

        boolean allowed = isAdmin || (customer_id!=null && customer_id.equals(senderId)) || (employee_id!=null && employee_id.equals(senderId));

        if(!allowed){throw new SecurityException("No permission to chat on this ticket");}

        User sender = userRepository.findById(senderId).orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .ticket(ticket)
                .sender(sender)
                .content(content)
                .type(type)
                .build();

        return chatMessageRepository.save(message);
    }

    // system message
    @Transactional
    public void sendSystemNotification (Long ticketId,String content){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(()-> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        // save message
        ChatMessage message = ChatMessage.builder()
                .ticket(ticket)
                .sender(null)
                .content(content)
                .type(ChatMessageType.SYSTEM)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);


        ChatMessageResponse payload = ChatMessageMapper.toResponse(savedMessage,"SYSTEM");
        messagingTemplate.convertAndSend("/topic/ticket/" + ticketId, payload);
    }




    public Page<ChatMessage> getChatHistory(Long ticketId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatMessageRepository.findByTicket_IdOrderByCreatedAtAsc(ticketId, pageable);
    }

}
