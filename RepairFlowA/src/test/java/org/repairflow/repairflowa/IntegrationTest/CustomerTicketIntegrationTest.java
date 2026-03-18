package org.repairflow.repairflowa.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketCreateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
/**
 * @author guangyang
 * @date 3/17/26 17:53
 * @description TODO: Description
 */
public class CustomerTicketIntegrationTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void cleanUp() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    /***
     * ==================
     * create ticket
     * ==================
     */

    @Test
    @DisplayName("Customer Should create ticket successfully")
    void customer_should_create_ticket() throws Exception {
        TicketCreateReq newTicket = buildTicketCreateReq("Test");
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com","passwordTest");

        mockMvc.perform(post("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isOk());


        // verify
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> tickets = ticketRepository.findByCustomer_Id(customer.getId(), pageable);

        assertThat(tickets.getContent()).hasSize(1);

        Ticket savedTicket = tickets.getContent().get(0);
        assertThat(savedTicket.getTitle()).isEqualTo("Test");
        assertThat(savedTicket.getDescription()).isEqualTo("integration test");
        assertThat(savedTicket.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(savedTicket.getIssueTypeId()).isEqualTo(1L);
        assertThat(savedTicket.getDeviceCategoryId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("Should return 403 without token")
    void should_return_403_without_token() throws Exception {
        TicketCreateReq newTicket = buildTicketCreateReq("Test");

        mockMvc.perform(post("/auth/customer/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when admin creates customer ticket")
    void should_return_403_when_admin_creates_customer_ticket() throws Exception {
        buildUser("admin@test.com", Role.ADMIN);
        TicketCreateReq newTicket = buildTicketCreateReq("Test");
        String adminToken = loginAndGetToken("admin@test.com", "passwordTest");

        mockMvc.perform(post("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when employee creates customer ticket")
    void should_return_403_when_employee_creates_customer_ticket() throws Exception {
        buildUser("employee@test.com", Role.EMPLOYEE);
        TicketCreateReq newTicket = buildTicketCreateReq("Test");
        String employeeToken = loginAndGetToken("employee@test.com", "passwordTest");

        mockMvc.perform(post("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return bad request when title is empty")
    void should_return_bad_request_when_title_empty() throws Exception {
        TicketCreateReq newTicket = buildTicketCreateReq("");
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");

        mockMvc.perform(post("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    /***
     * ==================
     * read one ticket
     * ==================
     */

    @Test
    @DisplayName("should retrieve ticket successfully")
    void should_retrieve_ticket() throws Exception {
        TicketCreateReq newTicket = buildTicketCreateReq("Test");
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");
        Ticket savedTicket = buildTicket(customer,TicketStatus.PENDING);
        Long ticketId = savedTicket.getId();


        mockMvc.perform(get("/auth/customer/tickets/" + ticketId)
        .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTicket)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Read Ticket details"))
                .andExpect(jsonPath("$.data.id").value(ticketId))
                .andExpect(jsonPath("$.data.title").value("Test"))
                .andExpect(jsonPath("$.data.description").value("integration test"))
                .andExpect(jsonPath("$.data.customer.id").value(customer.getId()));



    }

    @Test
    @DisplayName("should return with 403 with out token ")
    void should_return_with_404_without_token() throws Exception {

        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        Ticket savedTicket = buildTicket(customer,TicketStatus.PENDING);
        mockMvc.perform(get("/auth/customer/tickets/" + savedTicket.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Should return with 403 with different role")
    void should_return_with_403_with_different_role() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        User employee = buildUser("employee@test.com", Role.EMPLOYEE);
        Ticket savedTicket = buildTicket(customer,TicketStatus.PENDING);
        String employeeToken = loginAndGetToken("employee@test.com", "passwordTest");
        mockMvc.perform(get("/auth/customer/tickets/" + savedTicket.getId())
                .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return is not found while id incorrect")
    void should_return_is_not_found_when_id_incorrect() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        Ticket savedTicket = buildTicket(customer,TicketStatus.PENDING);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");
        mockMvc.perform(get("/auth/customer/tickets/" + 99)
                .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found"));
    }

    /***
     * ==================
     * read all tickets
     * ==================
     */

    @Test
    @DisplayName("Customer should list own tickets successfully")
    void customer_should_list_own_tickets_successfully() throws Exception {
        User customer = buildUser("customer_list@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer_list@test.com", "passwordTest");

        buildTicket(customer, TicketStatus.PENDING);
        buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(get("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }


    @Test
    @DisplayName("List tickets should fail when no token provided")
    void list_tickets_should_fail_when_no_token_provided() throws Exception {
        mockMvc.perform(get("/auth/customer/tickets"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Admin should not be allowed to list customer tickets")
    void admin_should_not_list_customer_tickets() throws Exception {
        User admin = buildUser("admin_list@test.com", Role.ADMIN);
        String adminToken = loginAndGetToken("admin_list@test.com", "passwordTest");

        mockMvc.perform(get("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }



    @Test
    @DisplayName("Customer should only list own tickets")
    void customer_should_only_list_own_tickets() throws Exception {
        User customer1 = buildUser("customer_own1@test.com", Role.CUSTOMER);
        User customer2 = buildUser("customer_own2@test.com", Role.CUSTOMER);
        String token = loginAndGetToken("customer_own1@test.com", "passwordTest");

        buildTicket(customer1, TicketStatus.PENDING);
        buildTicket(customer1, TicketStatus.PENDING);
        buildTicket(customer2, TicketStatus.PENDING);

        mockMvc.perform(get("/auth/customer/tickets")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }



    /***
     * ==================
     * approve quote
     * ==================
     */

    @Test
    @DisplayName("Customer should approve quote successfully")
    void customer_should_approve_quote_successfully() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.QUOTED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/approve-quote")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(updatedTicket.getId()).isEqualTo(ticket.getId());
    }

    @Test
    @DisplayName("Approve quote should fail when no token provided")
    void approve_quote_should_fail_when_no_token_provided() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.QUOTED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/approve-quote"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Admin should not be allowed to approve quote from customer endpoint")
    void admin_should_not_approve_quote_from_customer_endpoint() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        User admin = buildUser("admin@test.com", Role.ADMIN);
        String adminToken = loginAndGetToken("admin@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.QUOTED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/approve-quote")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not approve quote for others ticket")
    void customer_should_not_approve_quote_for_others_ticket() throws Exception {
        User customer1 = buildUser("customer1@test.com", Role.CUSTOMER);
        User customer2 = buildUser("customer2@test.com", Role.CUSTOMER);
        String token = loginAndGetToken("customer1@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer2, TicketStatus.QUOTED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/approve-quote")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    /***
     * ==================
     * confirm paid
     * ==================
     */

    @Test
    @DisplayName("Customer should confirm paid successfully")
    void customer_should_confirm_paid_successfully() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.READY_FOR_CONFIRMATION);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/confirm")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(updatedTicket.isPaid()).isTrue();
    }

    @Test
    @DisplayName("Confirm paid should fail when no token provided")
    void confirm_paid_should_fail_when_no_token_provided() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.READY_FOR_CONFIRMATION);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/confirm"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should not be allowed to confirm paid from customer endpoint")
    void admin_should_not_confirm_paid_from_customer_endpoint() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        User admin = buildUser("admin@test.com", Role.ADMIN);
        String adminToken = loginAndGetToken("admin@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.READY_FOR_CONFIRMATION);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/confirm")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not confirm paid for others ticket")
    void customer_should_not_confirm_paid_for_others_ticket() throws Exception {
        User customer1 = buildUser("customer1@test.com", Role.CUSTOMER);
        User customer2 = buildUser("customer2@test.com", Role.CUSTOMER);
        String token = loginAndGetToken("customer1@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer2, TicketStatus.READY_FOR_CONFIRMATION);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/confirm")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    /***
     * ==================
     * mark delivered
     * ==================
     */

    @Test
    @DisplayName("Customer should mark delivered successfully")
    void customer_should_mark_delivered_successfully() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        String customerToken = loginAndGetToken("customer@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.SHIPPED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/mark-delivered")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.DELIVERED);
    }

    @Test
    @DisplayName("Mark delivered should fail when no token provided")
    void mark_delivered_should_fail_when_no_token_provided() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.SHIPPED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/mark-delivered"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should not be allowed to mark delivered from customer endpoint")
    void admin_should_not_mark_delivered_from_customer_endpoint() throws Exception {
        User customer = buildUser("customer@test.com", Role.CUSTOMER);
        User admin = buildUser("admin@test.com", Role.ADMIN);
        String adminToken = loginAndGetToken("admin@test.com", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.SHIPPED);

        mockMvc.perform(post("/auth/customer/tickets/" + ticket.getId() + "/mark-delivered")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }







    /***
         * ==================
         * helper
         * ==================
         */
        private TicketCreateReq buildTicketCreateReq(String title) {
            return new TicketCreateReq(title,"integration test",1L,1L);
        }


        private User buildUser(String email, Role role) {
            User user = new User();
            user.setFirstName("testF");
            user.setLastName("testL");
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode("passwordTest"));
            user.setPhone("1122334455");
            user.setRole(role);
            user.setActive(true);
            return userRepository.saveAndFlush(user);
        }

        private String loginAndGetToken(String email, String password) throws Exception {
            UserLoginReq req = new UserLoginReq(email, password);

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();

            return JsonPath.read(response, "$.data.token");
        }

        private Ticket buildTicket(User customer,TicketStatus status) {

            Ticket ticket = new Ticket();
            ticket.setTitle("Test");
            ticket.setDescription("integration test");
            ticket.setCustomer(customer);
            ticket.setPaid(false);
            ticket.setIssueTypeId(1L);
            ticket.setDeviceCategoryId(1L);
            ticket.setQuoteAmount(BigDecimal.ZERO);
            ticket.setStatus(status);
            return ticketRepository.save(ticket);
        }





}
