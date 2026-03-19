package org.repairflow.repairflowa.securityTest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guangyang
 * @date 3/13/26 22:19
 * @description TODO: Description
 */
public class AdminSecurityTest extends IntegrationTestBase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("should retrieve user data with admin role")
    void shouldRetrieveUserDataWithAdminRole() throws Exception {
        User admin = buildUser("admin@test.com",Role.ADMIN);

        mockMvc.perform(get("/user/{id}", admin.getId()))
                .andDo(print())
                .andExpect(status().isOk());



    }

    @Test
    @DisplayName("should return 401 with anonymous")
    void shouldReturn401WithAnonymous() throws Exception {
        mockMvc.perform(get("/user/{id}",1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER","EMPLOYEE"})
    @DisplayName("should return 403 when role not admin")
    void shouldReturn401WhenRoleNotAdmin() throws Exception {
        mockMvc.perform(get("/user/{id}",1L))
                .andExpect(status().isForbidden());
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


}
