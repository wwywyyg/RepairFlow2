package org.repairflow.repairflowa.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guangyang
 * @date 3/17/26 21:08
 * @description TODO: Description
 */
public class EmployeeTicketIntegrationTest extends IntegrationTestBase {
    private static final String EMPLOYEE_BASE_URL = "/auth/employee/tickets";

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

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void cleanUp() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }


    /***
     * ==================
     * available tickets
     * ==================
     */

    @Test
    @DisplayName("Employee should list available tickets successfully")
    void employee_should_list_available_tickets_successfully() throws Exception {
        User employee = buildUser("e1@t.co", Role.EMPLOYEE);
        User customer = buildUser("c1@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e1@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.PENDING);
        buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/available")
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("Admin should list available tickets successfully")
    void admin_should_list_available_tickets_successfully() throws Exception {
        User admin = buildUser("a1@t.co", Role.ADMIN);
        User customer = buildUser("c2@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("a1@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.PENDING);
        buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/available")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }


    @Test
    @DisplayName("Available tickets should fail when no token provided")
    void available_tickets_should_fail_when_no_token_provided() throws Exception {
        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/available"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not list available tickets")
    void customer_should_not_list_available_tickets() throws Exception {
        User customer = buildUser("c3@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c3@t.co", "passwordTest");

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/available")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Available tickets should not include claimed tickets")
    void available_tickets_should_not_include_claimed_tickets() throws Exception {
        User employee = buildUser("e2@t.co", Role.EMPLOYEE);
        User customer = buildUser("c4@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e2@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.PENDING);
        buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/available")
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }


    /***
     * ==================
     * own tickets
     * ==================
     */

    @Test
    @DisplayName("Employee should list own tickets successfully")
    void employee_should_list_own_tickets_successfully() throws Exception {
        User employee = buildUser("e3@t.co", Role.EMPLOYEE);
        User anotherEmployee = buildUser("e4@t.co", Role.EMPLOYEE);
        User customer = buildUser("c5@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e3@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);
        buildTicket(customer, TicketStatus.IN_PROGRESS, employee, BigDecimal.ZERO, false);
        buildTicket(customer, TicketStatus.ASSIGNED, anotherEmployee, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/own-tickets")
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("Own tickets should fail when no token provided")
    void own_tickets_should_fail_when_no_token_provided() throws Exception {
        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/own-tickets"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not list employee own tickets")
    void customer_should_not_list_employee_own_tickets() throws Exception {
        User customer = buildUser("c6@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c6@t.co", "passwordTest");

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/own-tickets")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should list own tickets successfully")
    void admin_should_list_own_tickets_successfully() throws Exception {
        User admin = buildUser("a2@t.co", Role.ADMIN);
        User customer = buildUser("c7@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("a2@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.ASSIGNED, admin, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/own-tickets")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    /***
     * ==================
     * get employee ticket
     * ==================
     */

    @Test
    @DisplayName("Employee should get own ticket successfully")
    void employee_should_get_own_ticket_successfully() throws Exception {
        User employee = buildUser("e5@t.co", Role.EMPLOYEE);
        User customer = buildUser("c8@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e5@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ticket.getId()));
    }

    @Test
    @DisplayName("Get employee ticket should fail when no token provided")
    void get_employee_ticket_should_fail_when_no_token_provided() throws Exception {
        User employee = buildUser("e6@t.co", Role.EMPLOYEE);
        User customer = buildUser("c9@t.co", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/" + ticket.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not get employee ticket")
    void customer_should_not_get_employee_ticket() throws Exception {
        User employee = buildUser("e7@t.co", Role.EMPLOYEE);
        User customer = buildUser("c10@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c10@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Employee should not get another employee ticket")
    void employee_should_not_get_another_employee_ticket() throws Exception {
        User employee1 = buildUser("e8@t.co", Role.EMPLOYEE);
        User employee2 = buildUser("e9@t.co", Role.EMPLOYEE);
        User customer = buildUser("c11@t.co", Role.CUSTOMER);
        String token = loginAndGetToken("e8@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee2, BigDecimal.ZERO, false);

        mockMvc.perform(get(EMPLOYEE_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(409003));
    }


    /***
     * ==================
     * claim ticket
     * ==================
     */

    @Test
    @DisplayName("Employee should claim ticket successfully")
    void employee_should_claim_ticket_successfully() throws Exception {
        User employee = buildUser("e10@t.co", Role.EMPLOYEE);
        User customer = buildUser("c12@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e10@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(post(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/claim")
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getEmployee().getId()).isEqualTo(employee.getId());
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.ASSIGNED);
    }

    @Test
    @DisplayName("Admin should claim ticket successfully")
    void admin_should_claim_ticket_successfully() throws Exception {
        User admin = buildUser("a3@t.co", Role.ADMIN);
        User customer = buildUser("c13@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("a3@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(post(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/claim")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getEmployee().getId()).isEqualTo(admin.getId());
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.ASSIGNED);
    }


    @Test
    @DisplayName("Claim ticket should fail when no token provided")
    void claim_ticket_should_fail_when_no_token_provided() throws Exception {
        User customer = buildUser("c14@t.co", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(post(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/claim"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Customer should not claim ticket")
    void customer_should_not_claim_ticket() throws Exception {
        User customer = buildUser("c15@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c15@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.PENDING);

        mockMvc.perform(post(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/claim")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Employee should not claim already assigned ticket")
    void employee_should_not_claim_already_assigned_ticket() throws Exception {
        User employee1 = buildUser("e11@t.co", Role.EMPLOYEE);
        User employee2 = buildUser("e12@t.co", Role.EMPLOYEE);
        User customer = buildUser("c16@t.co", Role.CUSTOMER);
        String token = loginAndGetToken("e11@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee2, BigDecimal.ZERO, false);

        mockMvc.perform(post(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/claim")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    /***
     * ==================
     * quote ticket
     * ==================
     */

    @Test
    @DisplayName("Employee should quote ticket successfully")
    void employee_should_quote_ticket_successfully() throws Exception {
        User employee = buildUser("e13@t.co", Role.EMPLOYEE);
        User customer = buildUser("c17@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e13@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/quote")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildQuoteReqJson("100.00")))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getQuoteAmount()).isEqualByComparingTo("100.00");
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.QUOTED);
    }

    @Test
    @DisplayName("Admin should quote ticket successfully")
    void admin_should_quote_ticket_successfully() throws Exception {
        User admin = buildUser("a4@t.co", Role.ADMIN);
        User customer = buildUser("c18@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("a4@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, admin, BigDecimal.ZERO, false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/quote")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildQuoteReqJson("200.00")))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getQuoteAmount()).isEqualByComparingTo("200.00");
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.QUOTED);
    }

    @Test
    @DisplayName("Quote ticket should fail when no token provided")
    void quote_ticket_should_fail_when_no_token_provided() throws Exception {
        User employee = buildUser("e14@t.co", Role.EMPLOYEE);
        User customer = buildUser("c19@t.co", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildQuoteReqJson("100.00")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not quote ticket")
    void customer_should_not_quote_ticket() throws Exception {
        User employee = buildUser("e15@t.co", Role.EMPLOYEE);
        User customer = buildUser("c20@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c20@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee, BigDecimal.ZERO, false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/quote")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildQuoteReqJson("100.00")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Employee should not quote another employee ticket")
    void employee_should_not_quote_another_employee_ticket() throws Exception {
        User employee1 = buildUser("e16@t.co", Role.EMPLOYEE);
        User employee2 = buildUser("e17@t.co", Role.EMPLOYEE);
        User customer = buildUser("c21@t.co", Role.CUSTOMER);
        String token = loginAndGetToken("e16@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.ASSIGNED, employee2, BigDecimal.ZERO, false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/quote")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildQuoteReqJson("100.00")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    /***
     * ==================
     * update status
     * ==================
     */

    @Test
    @DisplayName("Employee should update ticket status successfully")
    void employee_should_update_ticket_status_successfully() throws Exception {
        User employee = buildUser("e18@t.co", Role.EMPLOYEE);
        User customer = buildUser("c22@t.co", Role.CUSTOMER);
        String employeeToken = loginAndGetToken("e18@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.DEVICE_RECEIVED, employee, new BigDecimal("100.00"), false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/status")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildStatusUpdateReqJson("IN_PROGRESS")))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Admin should update ticket status successfully")
    void admin_should_update_ticket_status_successfully() throws Exception {
        User admin = buildUser("a5@t.co", Role.ADMIN);
        User customer = buildUser("c23@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("a5@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.DEVICE_RECEIVED, admin, new BigDecimal("100.00"), false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildStatusUpdateReqJson("IN_PROGRESS")))
                .andDo(print())
                .andExpect(status().isOk());

        Ticket updatedTicket = getTicketOrThrow(ticket.getId());
        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Update status should fail when no token provided")
    void update_status_should_fail_when_no_token_provided() throws Exception {
        User employee = buildUser("e19@t.co", Role.EMPLOYEE);
        User customer = buildUser("c24@t.co", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, TicketStatus.DEVICE_RECEIVED, employee, new BigDecimal("100.00"), false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildStatusUpdateReqJson("IN_PROGRESS")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should not update ticket status")
    void customer_should_not_update_ticket_status() throws Exception {
        User employee = buildUser("e20@t.co", Role.EMPLOYEE);
        User customer = buildUser("c25@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c25@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, TicketStatus.DEVICE_RECEIVED, employee, new BigDecimal("100.00"), false);

        mockMvc.perform(put(EMPLOYEE_BASE_URL + "/" + ticket.getId() + "/status")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildStatusUpdateReqJson("IN_PROGRESS")))
                .andDo(print())
                .andExpect(status().isForbidden());
    }






    /***
     * ==================
     * helper
     * ==================
     */

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

    private Ticket buildTicket(User customer, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setTitle("Test");
        ticket.setDescription("integration test");
        ticket.setCustomer(customer);
        ticket.setPaid(false);
        ticket.setIssueTypeId(1L);
        ticket.setDeviceCategoryId(1L);
        ticket.setQuoteAmount(BigDecimal.ZERO);
        ticket.setStatus(status);
        return ticketRepository.saveAndFlush(ticket);
    }

    private Ticket buildTicket(User customer, TicketStatus status, User employee,
                               BigDecimal quoteAmount, boolean paid) {
        Ticket ticket = new Ticket();
        ticket.setTitle("Test");
        ticket.setDescription("integration test");
        ticket.setCustomer(customer);
        ticket.setEmployee(employee);
        ticket.setPaid(paid);
        ticket.setIssueTypeId(1L);
        ticket.setDeviceCategoryId(1L);
        ticket.setQuoteAmount(quoteAmount);
        ticket.setStatus(status);
        return ticketRepository.saveAndFlush(ticket);
    }

    private String buildQuoteReqJson(String amount) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("quoteAmount", amount);
        return objectMapper.writeValueAsString(json);
    }

    private String buildStatusUpdateReqJson(String status) throws Exception {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", status);
        return objectMapper.writeValueAsString(json);
    }

    private Ticket getTicketOrThrow(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow();
    }
}


