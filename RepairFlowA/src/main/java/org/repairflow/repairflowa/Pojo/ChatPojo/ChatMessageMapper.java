package org.repairflow.repairflowa.Pojo.ChatPojo;

import org.repairflow.repairflowa.Exception.Response.ChatMessageResponse;

/**
 * @author guangyang
 * @date 1/2/26 23:03
 * @description TODO: Description
 */
public class ChatMessageMapper {
    public static ChatMessageResponse toResponse(ChatMessage m, String senderRole) {
        String role = (m.getSender() == null) ? "SYSTEM" : m.getSender().getRole().name();
        return new ChatMessageResponse(
                m.getId(),
                m.getTicket().getId(),
                m.getSender() == null ? null : m.getSender().getId(),
                role,
                m.getContent(),
                m.getType(),
                m.getCreatedAt()
        );
    }
}
