package org.repairflow.repairflowa.RepositoryTest;

/**
 * @author guangyang
 * @date 3/8/26 19:25
 * @description TODO: Description
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class TicketRepositoryTest  extends IntegrationTestBase {
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    UserRepository userRepository;




    @Test
    @DisplayName("should find  all tickets by customer ID")
    void shouldFindAllTicketsByCustomerId() {
        User customer = createAndSaveUer("customer@user.com", Role.CUSTOMER);
        User employee = createAndSaveUer("employee@user.com", Role.EMPLOYEE);

        Ticket ticket1 = createAndSaveTicket(customer,employee,"title1","description1",TicketStatus.PENDING);
        Ticket ticket2 = createAndSaveTicket(customer,employee,"title2","description2",TicketStatus.PENDING);
        Ticket ticket3 = createAndSaveTicket(customer,employee,"title3","description3",TicketStatus.PENDING);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);
        ticketRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> response = ticketRepository.findByCustomer_Id(customer.getId(), pageable);

        assertThat(response.getContent())
                .hasSize(3);
        assertThat(response.getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("title1", "title2", "title3");
    }

    @Test
    @DisplayName("Should return empty when customer without any tickets")
    void shouldReturnEmptyWhenCustomerWithoutAnyTickets() {
        User customer = createAndSaveUer("customer@user.com", Role.CUSTOMER);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> response = ticketRepository.findByCustomer_Id(customer.getId(), pageable);

        assertThat(response).isEmpty();

    }

    @Test
    @DisplayName("Should find all tickets by employee ID")
    void shouldFindAllTicketsByEmployeeId() {
        User employee = createAndSaveUer("employee@user.com", Role.EMPLOYEE);
        User customer = createAndSaveUer("customer@user.com", Role.CUSTOMER);

        Ticket ticket1 = createAndSaveTicket(customer,employee,"title1","description1",TicketStatus.PENDING);
        Ticket ticket2 = createAndSaveTicket(customer,employee,"title2","description2",TicketStatus.PENDING);
        Ticket ticket3 = createAndSaveTicket(customer,employee,"title3","description3",TicketStatus.PENDING);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);
        ticketRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> response = ticketRepository.findByEmployee_Id(employee.getId(), pageable);

        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getContent())
                .extracting("title")
                .containsExactlyInAnyOrder("title1", "title2", "title3");
    }


    @Test
    @DisplayName("should find all tickets by customer ID and ticket Status")
    void shouldFindAllTicketsByCustomerIdAndTicketStatus() {
        User customer = createAndSaveUer("customer@user.com", Role.CUSTOMER);
        User employee = createAndSaveUer("employee@user.com", Role.EMPLOYEE);

        Ticket ticket1 = createAndSaveTicket(customer,employee,"title1","description1",TicketStatus.PENDING);
        Ticket ticket2 = createAndSaveTicket(customer,employee,"title2","description2",TicketStatus.ASSIGNED);
        Ticket ticket3 = createAndSaveTicket(customer,employee,"title2","description2",TicketStatus.ASSIGNED);
        Ticket ticket4 = createAndSaveTicket(customer,employee,"title3","description3",TicketStatus.PENDING);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);
        ticketRepository.save(ticket4);
        ticketRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> response = ticketRepository.findByCustomer_IdAndStatus(customer.getId(), TicketStatus.PENDING, pageable);
        Page<Ticket> response2 = ticketRepository.findByCustomer_IdAndStatus(customer.getId(), TicketStatus.ASSIGNED,pageable);
//         status is pending
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent())
                .extracting(Ticket::getStatus)
                .containsOnly(TicketStatus.PENDING);

        // status is assigned
        assertThat(response2.getContent()).hasSize(2);
        assertThat(response2.getContent())
                .extracting(Ticket::getStatus)
                .containsOnly(TicketStatus.ASSIGNED);
    }


    @Test
    @DisplayName("Should find tickets by employee id and status")
    void shouldFindTicketsByEmployeeIdAndTicketStatus() {
        User employee = createAndSaveUer("employee@user.com", Role.EMPLOYEE);
        User customer = createAndSaveUer("customer@user.com", Role.CUSTOMER);

        Ticket ticket1 = createAndSaveTicket(customer,employee,"title1","description1",TicketStatus.PENDING);
        Ticket ticket2 = createAndSaveTicket(customer,employee,"title2","description2",TicketStatus.ASSIGNED);
        Ticket ticket3 = createAndSaveTicket(customer,employee,"title3","description3",TicketStatus.PENDING);

        ticketRepository.save(ticket1);
        ticketRepository.save(ticket2);
        ticketRepository.save(ticket3);
        ticketRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> response = ticketRepository.findByEmployee_IdAndStatus(employee.getId(), TicketStatus.PENDING, pageable);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent())
                .extracting(Ticket::getStatus)
                .containsOnly(TicketStatus.PENDING);



    }





    private User createAndSaveUer(String email,Role role){
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPasswordHash("password123");
        user.setPhone(randomPhone());
        user.setRole(role);
        user.setActive(true);
        return userRepository.saveAndFlush(user);
    }

    private Ticket createAndSaveTicket(User customer,User employee,String title, String description,TicketStatus status){
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCustomer(customer);
        ticket.setEmployee(employee);
        ticket.setStatus(status);
        ticket.setQuoteAmount(BigDecimal.ZERO);
        ticket.setDeviceCategoryId(1L);
        ticket.setIssueTypeId(2L);
        ticket.setPaid(false);
        return ticketRepository.saveAndFlush(ticket);

    }


    private String randomPhone() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
