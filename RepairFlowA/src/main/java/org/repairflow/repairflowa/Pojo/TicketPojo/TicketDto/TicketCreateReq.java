package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;




public record TicketCreateReq(
        @NotBlank(message = "title must be not blank") @Size(max = 128)
        String title,

        @NotBlank(message = "description must be not blank")
        String description,

        @NotNull(message = "device category id must be not blank")
        Long deviceCategoryId,

        @NotNull(message = "issue type id must be not blank")
        Long issueTypeId
) {
}
