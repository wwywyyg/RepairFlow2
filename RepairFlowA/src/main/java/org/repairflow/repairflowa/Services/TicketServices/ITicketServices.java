package org.repairflow.repairflowa.Services.TicketServices;

import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketQuoteReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketStatusUpdateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketCreateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author guangyang
 * @date 11/25/25 AM6:54
 * @description TODO: Description
 */
public interface ITicketServices {

    // CUSTOMER
    TicketResponse createTicket(TicketCreateReq createReq, Long customerId); // create
    Page<TicketResponse> listCustomerTickets(Long customerId, TicketStatus status, Pageable pageable); // list customer tickets
    TicketResponse getCustomerTicket(Long ticketId, Long customerId); // get  Customer ticket
    TicketResponse approveQuote(Long ticketId, Long customerId);
    TicketResponse confirmAndMarkPay(Long ticketId, Long customerId);
    TicketResponse markDelivered(Long ticketId, Long customerId);


    // EMPLOYEE
    Page<TicketResponse> listAvailableTickets(Pageable pageable);
    TicketResponse claimTicket(Long ticketId, Long employeeId);
    TicketResponse setQuote(Long ticketId, Long employeeId, TicketQuoteReq req);
    TicketResponse updateEmployeeStatus(Long ticketId, Long employeeId, TicketStatusUpdateReq req);
    Page<TicketResponse> listEmployeeOwnTickets(Long employeeId, Pageable pageable);
    TicketResponse getEmployeeTicket(Long ticketId, Long employeeId);

    // ADMIN
    Page<TicketResponse> listEmployeeTickets(Long employeeId, Pageable pageable);
    Page<TicketResponse> listAllTickets(Pageable pageable);
    TicketResponse getTicket(Long ticketId);
}
