package org.repairflow.repairflowa.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
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


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author guangyang
 * @date 3/17/26 23:03
 * @description TODO: Description
 */
public class AdminIntegrationTest extends IntegrationTestBase {

    private static final String ADMIN_BASE_URL = "/auth/admin/ticket";

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



    /***
     * ==================
     * list all tickets
     * ==================
     */

    @Test
    @DisplayName("Admin should list all tickets successfully")
    void admin_should_list_all_tickets_successfully() throws Exception {
        User admin = buildUser("admin1@t.co", Role.ADMIN);
        User customer = buildUser("c1@t.co", Role.CUSTOMER);
        String adminToken = loginAndGetToken("admin1@t.co", "passwordTest");

        buildTicket(customer, TicketStatus.PENDING);
        buildTicket(customer, TicketStatus.ASSIGNED);

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-all")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("all tickets retrieved successfully"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("List all tickets should return 403 when token is missing")
    void list_all_tickets_should_return_403_when_token_is_missing() throws Exception {
        mockMvc.perform(get(ADMIN_BASE_URL + "/list-all"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Employee should get 403 when listing all tickets")
    void employee_should_get_403_when_listing_all_tickets() throws Exception {
        User employee = buildUser("e1@t.co", Role.EMPLOYEE);
        String employeeToken = loginAndGetToken("e1@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-all")
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should get 403 when listing all tickets")
    void customer_should_get_403_when_listing_all_tickets() throws Exception {
        User customer = buildUser("c2@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c2@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-all")
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    /***
     * ==================
     * get employee tickets
     * ==================
     */

    @Test
    @DisplayName("admin should list employee own tickets successfully")
    void admin_should_list_employee_own_tickets_successfully() throws Exception {
        User employee = buildUser("e2@t.co", Role.EMPLOYEE);

        User customer = buildUser("c3@t.co", Role.CUSTOMER);
        User admin = buildUser("admin1@t.co", Role.ADMIN);
        String adminToken = loginAndGetToken("admin1@t.co", "passwordTest");

        buildTicket(customer, employee, TicketStatus.ASSIGNED);
        buildTicket(customer, employee, TicketStatus.IN_PROGRESS);


        mockMvc.perform(get(ADMIN_BASE_URL + "/list-employee-tickets/" + employee.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("employee-tickets retrieved successfully"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("admin should get empty page when employee no own tickets exist")
    void admin_should_get_empty_page_when_employee_no_own_tickets_exist() throws Exception {
        User admin = buildUser("admin1@t.co", Role.ADMIN);
        User employee = buildUser("e1@t.co", Role.EMPLOYEE);
        String employeeToken = loginAndGetToken("admin1@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-employee-tickets/" + employee.getId())
                        .header("Authorization", "Bearer " + employeeToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }

    @Test
    @DisplayName("Get employee tickets should return 403 when token is missing")
    void get_employee_tickets_should_return_401_when_token_is_missing() throws Exception {
        User admin = buildUser("admin1@t.co", Role.ADMIN);
        User employee = buildUser("e1@t.co", Role.EMPLOYEE);

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-employee-tickets/" + employee.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should get 403 when listing employee tickets")
    void customer_should_get_403_when_listing_employee_tickets() throws Exception {
        User employee = buildUser("e6@t.co", Role.EMPLOYEE);
        User customer = buildUser("c4@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c4@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-employee-tickets/" + employee.getId())
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Another employee should get 403 when listing other employee tickets")
    void another_employee_should_get_403_when_listing_other_employee_tickets() throws Exception {
        User employee1 = buildUser("e7@t.co", Role.EMPLOYEE);
        User employee2 = buildUser("e8@t.co", Role.EMPLOYEE);
        String employee1Token = loginAndGetToken("e7@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/list-employee-tickets/" + employee2.getId())
                        .header("Authorization", "Bearer " + employee1Token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    /***
     * ==================
     * get one ticket
     * ==================
     */

    @Test
    @DisplayName(" Admin should get employee's all  ticket successfully")
    void Admin_should_get_employee_all_tickets() throws Exception {
        User employee = buildUser("e9@t.co", Role.EMPLOYEE);
        User customer = buildUser("c5@t.co", Role.CUSTOMER);
        User admin = buildUser("ad@t.co", Role.ADMIN);
        String adminToken = loginAndGetToken("ad@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, employee, TicketStatus.ASSIGNED);

        mockMvc.perform(get(ADMIN_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ticket retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(ticket.getId()));
    }

    @Test
    @DisplayName("Get one ticket should return 403 when token is missing")
    void get_one_ticket_should_return_401_when_token_is_missing() throws Exception {
        User employee = buildUser("e10@t.co", Role.EMPLOYEE);
        User customer = buildUser("c6@t.co", Role.CUSTOMER);
        Ticket ticket = buildTicket(customer, employee, TicketStatus.ASSIGNED);

        mockMvc.perform(get(ADMIN_BASE_URL + "/" + ticket.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer should get 403 when getting employee ticket")
    void customer_should_get_403_when_getting_employee_ticket() throws Exception {
        User employee = buildUser("e11@t.co", Role.EMPLOYEE);
        User customer = buildUser("c7@t.co", Role.CUSTOMER);
        String customerToken = loginAndGetToken("c7@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, employee, TicketStatus.ASSIGNED);

        mockMvc.perform(get(ADMIN_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + customerToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Another employee should get 403 when getting other employee ticket")
    void another_employee_should_get_403_when_getting_other_employee_ticket() throws Exception {
        User employee1 = buildUser("e12@t.co", Role.EMPLOYEE);
        User employee2 = buildUser("e13@t.co", Role.EMPLOYEE);
        User customer = buildUser("c8@t.co", Role.CUSTOMER);
        String employee1Token = loginAndGetToken("e12@t.co", "passwordTest");

        Ticket ticket = buildTicket(customer, employee2, TicketStatus.ASSIGNED);

        mockMvc.perform(get(ADMIN_BASE_URL + "/" + ticket.getId())
                        .header("Authorization", "Bearer " + employee1Token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get one ticket should return 404 when ticket does not exist")
    void get_one_ticket_should_return_404_when_ticket_does_not_exist() throws Exception {
        User admin = buildUser("admin1@t.co", Role.ADMIN);
        String adminToken = loginAndGetToken("admin1@t.co", "passwordTest");

        mockMvc.perform(get(ADMIN_BASE_URL + "/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isNotFound());
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

    private Ticket buildTicket(User customer, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setTitle("Test ticket - " + System.nanoTime());
        ticket.setDescription("Test description");
        ticket.setStatus(status);
        ticket.setCustomer(customer);
        return ticketRepository.saveAndFlush(ticket);
    }

    private Ticket buildTicket(User customer, User employee, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setTitle("Assigned ticket - " + System.nanoTime());
        ticket.setDescription("Assigned description");
        ticket.setStatus(status);
        ticket.setCustomer(customer);
        ticket.setEmployee(employee);
        return ticketRepository.saveAndFlush(ticket);
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


}
