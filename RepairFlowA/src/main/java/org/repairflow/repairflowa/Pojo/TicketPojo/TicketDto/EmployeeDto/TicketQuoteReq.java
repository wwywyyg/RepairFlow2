package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * @author guangyang
 * @date 11/25/25 AM6:37
 * @description TODO: Description
 */
public record TicketQuoteReq(
        @NotNull
        BigDecimal quoteAmount
) {
}
