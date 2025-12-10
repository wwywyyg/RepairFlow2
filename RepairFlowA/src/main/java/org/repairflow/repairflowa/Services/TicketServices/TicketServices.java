package org.repairflow.repairflowa.Services.TicketServices;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Exception.AppExceptions;
import org.repairflow.repairflowa.Exception.BusinessException;
import org.repairflow.repairflowa.Exception.ErrorCode;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketQuoteReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketStatusUpdateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketCreateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMapper;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author guangyang
 * @date 11/25/25 AM6:52
 * @description TODO: Description
 */
@Service
@RequiredArgsConstructor
public class TicketServices  implements ITicketServices {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;


    // CUSTOMER
    @Transactional
    @Override
    public TicketResponse createTicket(TicketCreateReq createReq,Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Ticket ticket = Ticket.builder()
                .title(createReq.title())
                .description(createReq.description())
                .deviceCategoryId(createReq.deviceCategoryId())
                .issueTypeId(createReq.issueTypeId())
                .customer(customer)
                .status(TicketStatus.PENDING)
                .quoteAmount(BigDecimal.ZERO)
                .paid(false)
                .build();
        return TicketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

//    List all customer tickets
    @Override
    public Page<TicketResponse> listCustomerTickets(Long customerId, TicketStatus status, Pageable pageable) {
        Page<Ticket> pages = (status == null) ?
                ticketRepository.findByCustomer_Id(customerId,pageable) : ticketRepository.findByCustomer_IdAndStatus(customerId,status,pageable);
        return pages.map(TicketMapper::toTicketResponse);
    }

//    read one ticket
    @Override
    public TicketResponse getCustomerTicket(Long ticketId, Long customerId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        if(!ticket.getCustomer().getId().equals(customerId)) {
            throw new BusinessException(ErrorCode.TICKET_DATA_CONFLICT);
        }
        return TicketMapper.toTicketResponse(ticket);
    }

// approve quote

    @Transactional
    @Override
    public TicketResponse approveQuote(Long ticketId, Long customerId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        ensureCustomerOwner(ticket, customerId);
        ensureStatus(ticket,TicketStatus.QUOTED);

        ticket.setStatus(TicketStatus.AWAITING_DEVICE);
        return TicketMapper.toTicketResponse(ticket);
    }
//
//    @Transactional
//    public TicketResponse confirmAndMarkPaid(Long ticketId, Long customerId){
//        Ticket ticket = getTicketOrThrow(ticketId);
//        ensureCustomerOwner(ticket, customerId);
//        ensureStatus(ticket, TicketStatus.READY_FOR_CONFIRMATION);
//
//        ticket.setPaid(true);
//        ticket.setStatus(TicketStatus.PAID);
//        return TicketMapper.toTicketResponse(ticket);
//    }

//     confirm and mark pay
    @Transactional
    @Override
    public TicketResponse confirmAndMarkPay(Long ticketId, Long customerId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        ensureCustomerOwner(ticket, customerId);
        ensureStatus(ticket,TicketStatus.READY_FOR_CONFIRMATION);
        ticket.setPaid(true);
        ticket.setStatus(TicketStatus.PAID);
        return TicketMapper.toTicketResponse(ticket);
    }

//    mark delivered
    @Transactional
    @Override
    public TicketResponse markDelivered(Long ticketId, Long customerId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        ensureCustomerOwner(ticket, customerId);
        ensureStatus(ticket,TicketStatus.SHIPPED);

        ticket.setStatus(TicketStatus.DELIVERED);
        return TicketMapper.toTicketResponse(ticket);
    }




    // EMPLOYEE

    // list available tickets
    @Override
    public Page<TicketResponse> listAvailableTickets(Pageable pageable) {
        return ticketRepository.findByEmployeeIsNull(pageable).map(TicketMapper::toTicketResponse);
    }

    //read one ticket
    @Override
    public TicketResponse getEmployeeTicket(Long ticketId, Long employeeId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        if(!ticket.getEmployee().getId().equals(employeeId)) {
            throw new BusinessException(ErrorCode.TICKET_DATA_CONFLICT);
        }
        return TicketMapper.toTicketResponse(ticket);
    }

    // list employee own ticket
    @Override
    public Page<TicketResponse> listEmployeeOwnTickets(Long employeeId, Pageable pageable) {
        return ticketRepository.findByEmployee_Id(employeeId, pageable).map(TicketMapper::toTicketResponse);
    }

//    claim ticket
    @Transactional
    @Override
    public TicketResponse claimTicket(Long ticketId, Long employeeId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        if(ticket.getEmployee() != null) throw new BusinessException(ErrorCode.TICKET_DATA_CONFLICT);
        ensureStatus(ticket, TicketStatus.PENDING);

        User employee = userRepository.findById(employeeId).orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));
        if(employee.getRole() != Role.EMPLOYEE && employee.getRole() != Role.ADMIN) {
            throw new AuthorizationDeniedException("You are not allowed to claim a ticket.");
        }
        ticket.setEmployee(employee);
        ticket.setStatus(TicketStatus.ASSIGNED);
        return TicketMapper.toTicketResponse(ticket);
    }

    //set quote
    @Transactional
    @Override
    public TicketResponse setQuote(Long ticketId, Long employeeId, TicketQuoteReq req) {
        Ticket ticket = getTicketOrThrow(ticketId);
        ensureStatus(ticket, TicketStatus.ASSIGNED);

        ticket.setQuoteAmount(req.quoteAmount());
        ticket.setStatus(TicketStatus.QUOTED);
        return TicketMapper.toTicketResponse(ticket);
    }
//update status
    @Transactional
    @Override
    public TicketResponse updateEmployeeStatus(Long ticketId, Long employeeId, TicketStatusUpdateReq req) {
        Ticket ticket = getTicketOrThrow(ticketId);
        ensureEmployeeOwner(ticket, employeeId);

        TicketStatus next = req.status();
        validateEmployeeTransition(ticket.getStatus(),next);
        ticket.setStatus(next);
        return TicketMapper.toTicketResponse(ticket);
    }

    // ADMIN
    // find all users
    @Override
    public Page<TicketResponse> listAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(TicketMapper::toTicketResponse);
    }

    // find ticket by employee id
    @Override
    public Page<TicketResponse> listEmployeeTickets(Long employeeId, Pageable pageable) {
        return ticketRepository.findByEmployee_Id(employeeId, pageable).map(TicketMapper::toTicketResponse);
    }

//    read one ticket
    @Override
    public TicketResponse getTicket(Long ticketId) {
        Ticket ticket = getTicketOrThrow(ticketId);
        return TicketMapper.toTicketResponse(ticket);
    }

    //side helper
    private Ticket getTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(()-> new BusinessException(ErrorCode.TICKET_NOT_FOUND));
    }

    private void ensureStatus (Ticket ticket, TicketStatus status) {
        if(ticket.getStatus() != status) {
            throw new BusinessException(ErrorCode.TICKET_STATUS_INVALID, "Ticket status should be %s , Not %s" ,status,ticket.getStatus());
        }
    }

    private void ensureCurrentIn(TicketStatus current, TicketStatus expected){
        if(current != expected) {
            throw new BusinessException(ErrorCode.TICKET_STATUS_INVALID,"Ticket status %s not match %s",current,expected);
        }
    }

    private void ensureCustomerOwner (Ticket ticket, Long customerId) {
        if(!ticket.getCustomer().getId().equals(customerId)) {
            throw new BusinessException(ErrorCode.TICKET_NOT_OWN);
        }
    }

    private void ensureEmployeeOwner (Ticket ticket, Long employeeId) {
        if(ticket.getEmployee() == null || !ticket.getEmployee().getId().equals(employeeId)) {
            throw new BusinessException(ErrorCode.TICKET_NOT_OWN);
        }
    }
    private void validateEmployeeTransition(TicketStatus current, TicketStatus next){
        switch (next){
            case DEVICE_RECEIVED -> ensureCurrentIn(current, TicketStatus.AWAITING_DEVICE);
            case IN_PROGRESS -> ensureCurrentIn(current, TicketStatus.DEVICE_RECEIVED);
            case READY_FOR_CONFIRMATION -> ensureCurrentIn(current, TicketStatus.IN_PROGRESS);
            case SHIPPED -> ensureCurrentIn(current, TicketStatus.PAID);
            default -> throw new BusinessException(ErrorCode.TICKET_SET_STATUS_CONFLICT);
        }
    }
}
