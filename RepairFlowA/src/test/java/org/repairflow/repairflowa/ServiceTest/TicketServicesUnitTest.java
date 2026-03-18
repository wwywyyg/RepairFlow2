package org.repairflow.repairflowa.ServiceTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.repairflow.repairflowa.Event.TicketSystemMessageEvent;
import org.repairflow.repairflowa.Exception.BusinessException;
import org.repairflow.repairflowa.Exception.ErrorCode;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketStatusUpdateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketCreateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.Services.TicketServices.TicketServices;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.math.BigDecimal;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author guangyang
 * @date 3/16/26 22:27
 * @description TODO: Description
 */
@ExtendWith(MockitoExtension.class)
public class TicketServicesUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TicketServices ticketServices;

    /***
     *  =====================================
     *  Customer
     *  =====================================
     */

    /***
     * ================
     * Create Ticket
     * ================
     */

    @Test
    @DisplayName("Ticket should create successfully")
    void ticketShouldCreateSuccess() {
        // given
        TicketCreateReq newTicket = buildTicketCreateReq("ticket1");
        User customer = buildUser("customer@test.com",Role.CUSTOMER,1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        //when
        TicketResponse result = ticketServices.createTicket(newTicket,1L);
//        ticketServices.createTicket(newTicket,1L);

        //then
        assertNotNull(result);
        assertEquals("ticket1",result.getTitle());
        assertEquals("unitTest",result.getDescription());
        assertEquals(1L,result.getDeviceCategoryId());
        assertEquals(1L,result.getIssueTypeId());

        verify(userRepository).findById(1L);
        verify(ticketRepository).save(any(Ticket.class));


        verify(ticketRepository).save(ticketCaptor.capture());
        Ticket saveTicket = ticketCaptor.getValue();
        assertEquals("ticket1",saveTicket.getTitle());
        assertEquals("unitTest",saveTicket.getDescription());
        assertEquals(1L,saveTicket.getDeviceCategoryId());
        assertEquals(1L,saveTicket.getIssueTypeId());
        assertEquals(TicketStatus.PENDING,saveTicket.getStatus());
        assertFalse(saveTicket.isPaid());
        assertEquals(customer, saveTicket.getCustomer());
        assertEquals(BigDecimal.ZERO, saveTicket.getQuoteAmount());

    }




    @Test
    @DisplayName("should throw exception when user not fount")
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        TicketCreateReq newTicket = buildTicketCreateReq("ticket1");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        BusinessException ex = assertThrows(BusinessException.class, () -> ticketServices.createTicket(newTicket,1L));

        assertEquals("User not found", ex.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    /***
     * ================
     * Claim Ticket
     * ================
     */
    @Test
    @DisplayName("claimTicket should succeed for employee")
    void claimTicketShouldSucceedForEmployee() {
        Ticket ticket = buildTicket(100L, TicketStatus.PENDING);
        User employee = buildUser("employee@test.com", Role.EMPLOYEE, 10L);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(10L)).thenReturn(Optional.of(employee));

        TicketResponse result = ticketServices.claimTicket(100L, 10L);

        assertNotNull(result);
        assertEquals(TicketStatus.ASSIGNED, ticket.getStatus());
        assertEquals(employee, ticket.getEmployee());

        verify(ticketRepository).findById(100L);
        verify(userRepository).findById(10L);

        ArgumentCaptor<TicketSystemMessageEvent> eventCaptor =
                ArgumentCaptor.forClass(TicketSystemMessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TicketSystemMessageEvent event = eventCaptor.getValue();
        assertEquals(100L, event.ticketId());
        assertTrue(event.content().contains("ASSIGNED"));
    }


    @Test
    @DisplayName("claimTicket should throw when ticket already assigned")
    void claimTicketShouldThrowWhenAlreadyAssigned() {
        Ticket ticket = buildTicket(100L, TicketStatus.PENDING);
        User existingEmployee = buildUser("existing@test.com", Role.EMPLOYEE, 20L);
        ticket.setEmployee(existingEmployee);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.claimTicket(100L, 10L));

        assertEquals(ErrorCode.TICKET_DATA_CONFLICT, ex.getErrorCode());
        verify(userRepository, never()).findById(anyLong());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }

    @Test
    @DisplayName("claimTicket should throw when status is not pending")
    void claimTicketShouldThrowWhenStatusInvalid() {
        Ticket ticket = buildTicket(100L, TicketStatus.QUOTED);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.claimTicket(100L, 10L));

        assertEquals(ErrorCode.TICKET_STATUS_INVALID, ex.getErrorCode());
        verify(userRepository, never()).findById(anyLong());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }

    @Test
    @DisplayName("claimTicket should throw when role is not employee or admin")
    void claimTicketShouldThrowWhenRoleNotAllowed() {
        Ticket ticket = buildTicket(100L, TicketStatus.PENDING);
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 10L);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(10L)).thenReturn(Optional.of(customer));

        assertThrows(AuthorizationDeniedException.class,
                () -> ticketServices.claimTicket(100L, 10L));

        assertNull(ticket.getEmployee());
        assertEquals(TicketStatus.PENDING, ticket.getStatus());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }



    /*
     * =========================
     * approveQuote (3 tests)
     * =========================
     */

    @Test
    @DisplayName("approveQuote should succeed")
    void approveQuoteShouldSucceed() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.QUOTED);
        ticket.setCustomer(customer);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        TicketResponse result = ticketServices.approveQuote(100L, 1L);

        assertNotNull(result);
        assertEquals(TicketStatus.AWAITING_DEVICE, ticket.getStatus());

        ArgumentCaptor<TicketSystemMessageEvent> eventCaptor =
                ArgumentCaptor.forClass(TicketSystemMessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TicketSystemMessageEvent event = eventCaptor.getValue();
        assertEquals(100L, event.ticketId());
        assertTrue(event.content().contains("AWAITING_DEVICE"));
    }

    @Test
    @DisplayName("approveQuote should throw when wrong customer")
    void approveQuoteShouldThrowWhenWrongOwner() {
        User realOwner = buildUser("owner@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.QUOTED);
        ticket.setCustomer(realOwner);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.approveQuote(100L, 2L));

        assertEquals(ErrorCode.TICKET_NOT_OWN, ex.getErrorCode());
        assertEquals(TicketStatus.QUOTED, ticket.getStatus());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }

    @Test
    @DisplayName("approveQuote should throw when status is invalid")
    void approveQuoteShouldThrowWhenStatusInvalid() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.PENDING);
        ticket.setCustomer(customer);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.approveQuote(100L, 1L));

        assertEquals(ErrorCode.TICKET_STATUS_INVALID, ex.getErrorCode());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }


    /*
     * =========================
     * confirmAndMarkPay (2 tests)
     * =========================
     */

    @Test
    @DisplayName("confirmAndMarkPay should succeed")
    void confirmAndMarkPayShouldSucceed() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.READY_FOR_CONFIRMATION);
        ticket.setCustomer(customer);
        ticket.setPaid(false);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        TicketResponse result = ticketServices.confirmAndMarkPay(100L, 1L);

        assertNotNull(result);
        assertTrue(ticket.isPaid());
        assertEquals(TicketStatus.PAID, ticket.getStatus());

        ArgumentCaptor<TicketSystemMessageEvent> eventCaptor =
                ArgumentCaptor.forClass(TicketSystemMessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TicketSystemMessageEvent event = eventCaptor.getValue();
        assertEquals(100L, event.ticketId());
        assertTrue(event.content().contains("READY_FOR_CONFIRMATION"));
    }

    @Test
    @DisplayName("confirmAndMarkPay should throw when status is invalid")
    void confirmAndMarkPayShouldThrowWhenStatusInvalid() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.IN_PROGRESS);
        ticket.setCustomer(customer);
        ticket.setPaid(false);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.confirmAndMarkPay(100L, 1L));

        assertEquals(ErrorCode.TICKET_STATUS_INVALID, ex.getErrorCode());
        assertFalse(ticket.isPaid());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }

    /*
     * =========================
     * markDelivered (2 tests)
     * =========================
     */

    @Test
    @DisplayName("markDelivered should succeed")
    void markDeliveredShouldSucceed() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.SHIPPED);
        ticket.setCustomer(customer);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        TicketResponse result = ticketServices.markDelivered(100L, 1L);

        assertNotNull(result);
        assertEquals(TicketStatus.DELIVERED, ticket.getStatus());

        ArgumentCaptor<TicketSystemMessageEvent> eventCaptor =
                ArgumentCaptor.forClass(TicketSystemMessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TicketSystemMessageEvent event = eventCaptor.getValue();
        assertEquals(100L, event.ticketId());
        assertTrue(event.content().contains("DELIVERED"));
    }

    @Test
    @DisplayName("markDelivered should throw when status is invalid")
    void markDeliveredShouldThrowWhenStatusInvalid() {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 1L);
        Ticket ticket = buildTicket(100L, TicketStatus.PAID);
        ticket.setCustomer(customer);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.markDelivered(100L, 1L));

        assertEquals(ErrorCode.TICKET_STATUS_INVALID, ex.getErrorCode());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }


    /*
     * =========================
     * updateEmployeeStatus (3 tests)
     * =========================
     */

    @Test
    @DisplayName("updateEmployeeStatus should succeed for valid transition")
    void updateEmployeeStatusShouldSucceedForValidTransition() {
        User employee = buildUser("employee@test.com", Role.EMPLOYEE, 10L);
        Ticket ticket = buildTicket(100L, TicketStatus.AWAITING_DEVICE);
        ticket.setEmployee(employee);

        TicketStatusUpdateReq req = new TicketStatusUpdateReq(TicketStatus.DEVICE_RECEIVED);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        TicketResponse result = ticketServices.updateEmployeeStatus(100L, 10L, req);

        assertNotNull(result);
        assertEquals(TicketStatus.DEVICE_RECEIVED, ticket.getStatus());

        ArgumentCaptor<TicketSystemMessageEvent> eventCaptor =
                ArgumentCaptor.forClass(TicketSystemMessageEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        TicketSystemMessageEvent event = eventCaptor.getValue();
        assertEquals(100L, event.ticketId());
        assertTrue(event.content().contains("DEVICE_RECEIVED"));
    }

    @Test
    @DisplayName("updateEmployeeStatus should throw when employee is not owner")
    void updateEmployeeStatusShouldThrowWhenNotOwner() {
        User owner = buildUser("owner@test.com", Role.EMPLOYEE, 10L);
        Ticket ticket = buildTicket(100L, TicketStatus.AWAITING_DEVICE);
        ticket.setEmployee(owner);

        TicketStatusUpdateReq req = new TicketStatusUpdateReq(TicketStatus.DEVICE_RECEIVED);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.updateEmployeeStatus(100L, 99L, req));

        assertEquals(ErrorCode.TICKET_NOT_OWN, ex.getErrorCode());
        assertEquals(TicketStatus.AWAITING_DEVICE, ticket.getStatus());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }

    @Test
    @DisplayName("updateEmployeeStatus should throw when transition is invalid")
    void updateEmployeeStatusShouldThrowWhenTransitionInvalid() {
        User employee = buildUser("employee@test.com", Role.EMPLOYEE, 10L);
        Ticket ticket = buildTicket(100L, TicketStatus.AWAITING_DEVICE);
        ticket.setEmployee(employee);

        TicketStatusUpdateReq req = new TicketStatusUpdateReq(TicketStatus.SHIPPED);

        when(ticketRepository.findById(100L)).thenReturn(Optional.of(ticket));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> ticketServices.updateEmployeeStatus(100L, 10L, req));

        assertEquals(ErrorCode.TICKET_STATUS_INVALID, ex.getErrorCode());
        assertEquals(TicketStatus.AWAITING_DEVICE, ticket.getStatus());
        verify(eventPublisher, never()).publishEvent(any(TicketSystemMessageEvent.class));
    }









    /***
     * ===========
     * Helper
     * ===========
     */

    private TicketCreateReq buildTicketCreateReq(String title){
        return new TicketCreateReq(
                title,"unitTest",1L,1L);
    }




    private User buildUser(String email, Role role, Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("testF");
        user.setLastName("testL");
        user.setEmail(email);
        user.setPasswordHash("passwordTest");
        user.setPhone("1234567890");
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private Ticket buildTicket(Long id, TicketStatus status) {
        User customer = buildUser("customer@test.com", Role.CUSTOMER, 999L);
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setTitle("ticket1");
        ticket.setDescription("unitTest");
        ticket.setDeviceCategoryId(1L);
        ticket.setIssueTypeId(1L);
        ticket.setCustomer(customer);
        ticket.setStatus(status);
        ticket.setQuoteAmount(BigDecimal.ZERO);
        ticket.setPaid(false);
        return ticket;
    }


}
