package org.repairflow.repairflowa.Exception.Response;

import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessage;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageType;

import java.time.LocalDateTime;

/**
 * @author guangyang
 * @date 1/2/26 22:50
 * @description TODO: Description
 */
public record ChatMessageResponse(
        Long id,
        Long ticketId,
        Long senderId,
        String senderRole,
        String content,
        ChatMessageType type,
        LocalDateTime createdAt
) { }


