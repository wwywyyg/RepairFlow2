package org.repairflow.repairflowa.Pojo.ChatPojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author guangyang
 * @date 1/2/26 16:20
 * @description TODO: Description
 */
public record ChatMessageRequest(
         @NotBlank(message = "Message can not be empty")
        String content,

         @NotNull(message = "ChatMessageType can not be empty")
         ChatMessageType chatMessageType
) {
}
