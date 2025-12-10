package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto;

import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;

/**
 * @author guangyang
 * @date 11/25/25 PM4:58
 * @description TODO: Description
 */
public class TicketMapper {
    private TicketMapper() {}
    public static TicketResponse toTicketResponse(Ticket ticket) {
        return  TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .deviceCategoryId(ticket.getDeviceCategoryId())
                .issueTypeId(ticket.getIssueTypeId())
                .status(ticket.getStatus())
                .quoteAmount(ticket.getQuoteAmount())
                .paid(ticket.isPaid())
                .customer(TicketResponse.SimpleUserResp.builder()
                        .id(ticket.getCustomer().getId())
                        .name(ticket.getCustomer().getFirstName() + ticket.getCustomer().getLastName())
                        .email(ticket.getCustomer().getEmail())
                        .build())
                .employee(ticket.getEmployee() == null? null :
                        TicketResponse.SimpleUserResp.builder()
                                .id(ticket.getEmployee().getId())
                                .name(ticket.getEmployee().getFirstName() + ticket.getEmployee().getLastName())
                                .email(ticket.getEmployee().getEmail())
                                .build())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
