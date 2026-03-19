package org.repairflow.repairflowa.securityTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Repository.TicketRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guangyang
 * @date 3/13/26 23:36
 * @description TODO: Description
 */
public class TicketSecurityTest extends IntegrationTestBase {
    @Autowired
    private TicketRepository ticketRepository;





    @Test
    @WithMockUser(roles = {"EMPLOYEE","ADMIN"})
    @DisplayName("should retrieve all available tickets with employee and admin role")
    void shouldRetrieveAllAvailableTicketsWithEmployeeAndAdminRole()throws Exception {
        mockMvc.perform(get("/auth/employee/tickets/available"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Should return 403 with anonymous")
    void shouldReturn403WithAnonymous()throws Exception {
        mockMvc.perform(get("/auth/employee/tickets/available"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 403 with customer Role")
    void shouldReturn403WithCustomerRole()throws Exception {
        mockMvc.perform(get("/auth/employee/tickets/available"))
                .andExpect(status().isForbidden());
    }




}
