package org.repairflow.repairflowa.Event;

import org.repairflow.repairflowa.Services.ChatServices.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @author guangyang
 * @date 1/3/26 09:31
 * @description TODO: Description
 */
@Component
public class TicketEventListener {
    private final ChatService chatService;

    public TicketEventListener(ChatService chatService) {
        this.chatService = chatService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketSystemMessage(TicketSystemMessageEvent event) {
        chatService.sendSystemNotification(event.ticketId(), event.content());
    }
}
