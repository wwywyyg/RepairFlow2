package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto;

import jakarta.validation.constraints.NotNull;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;

/**
 * @author guangyang
 * @date 11/25/25 AM6:39
 * @description TODO: Description
 */

public record TicketStatusUpdateReq(
        @NotNull
        TicketStatus status
) {
}
