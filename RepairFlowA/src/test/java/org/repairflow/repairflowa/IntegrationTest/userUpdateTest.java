package org.repairflow.repairflowa.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserRegisterReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateAdmin;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author guangyang
 * @date 3/9/26 22:17
 * @description TODO: Description
 */
public class userUpdateTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    // update      @PutMapping("/user/update/{id}")
    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        User user  = buildUser("customer@123.com",Role.CUSTOMER);

        // login and get jwt token
        UserLoginReq loginReq = new UserLoginReq("customer@123.com", "passwordTest");
        MvcResult loginRes = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String body = loginRes.getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.data.token");

        // update user info
        Long userId = user.getId();
        UserUpdateReq updateReq = new UserUpdateReq("newFirst","newLast","1122334455");
        mockMvc.perform(put("/auth/user/update/"+userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isOk());

        // verify database
        Optional<User> findUser = userRepository.findById(userId);
        assertEquals("newFirst", findUser.get().getFirstName());
        assertEquals("newLast", findUser.get().getLastName());
    }



    @Test
    @DisplayName("Should return 403 when updating user without token")
    void shouldReturn401WhenUpdatingWithoutToken() throws Exception {
        User user = buildUser("customer@123.com", Role.CUSTOMER);
        Long userId = user.getId();

        UserUpdateReq updateReq = new UserUpdateReq("newFirst", "newLast", "1122334455");

        mockMvc.perform(put("/auth/user/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update user Info successfully with admin role")
    void shouldUpdateUserInfoSuccessfullyWithAdminRole() throws Exception {
        User admin  = buildUser("admin@123.com",Role.ADMIN);
        User customer  = buildUser("customer@123.com",Role.CUSTOMER);
        Long adminId = admin.getId();
        Long customerId = customer.getId();

        String token = loginAndGetToken("admin@123.com","passwordTest");
        UserUpdateReq updateReq = new UserUpdateReq("newFirst","newLast","1122334455");



        mockMvc.perform(put("/auth/user/update/"+customerId)
                        .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isOk());

        // verify database
        Optional<User> findUser = userRepository.findById(customerId);
        assertEquals("newFirst", findUser.get().getFirstName());
        assertEquals("newLast", findUser.get().getLastName());

    }

    @Test
    @DisplayName("Should return 403 when user got update by other user")
    void shouldReturn403WhenUserGotUpdateByOtherUser() throws Exception {
        User user1 = buildUser("customer@123.com",Role.CUSTOMER);
        User user2 = buildUser("customer@456.com",Role.CUSTOMER);

        String user2Token = loginAndGetToken("customer@456.com","passwordTest");
        UserUpdateReq updateReq = new UserUpdateReq("newFirst","newLast","1122334455");

        mockMvc.perform(put("/auth/user/update/"+user1.getId())
                .header("Authorization", "Bearer " + user2Token)
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // @GetMapping("/user/{id}")
    @Test
    @DisplayName("Should get user successfully when user reads own profile")
    void shouldGetUserSuccessfullyWhenSelf() throws Exception {
        User user = buildUser("customer1@test.com", Role.CUSTOMER);
        Long userId = user.getId();

        String token = loginAndGetToken("customer1@test.com", "passwordTest");

        mockMvc.perform(get("/auth/user/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value("customer1@test.com"))
                .andExpect(jsonPath("$.data.firstName").value("testF"))
                .andExpect(jsonPath("$.data.lastName").value("testL"));
    }


    @Test
    @DisplayName("Should get any user successfully when admin requests")
    void shouldGetUserSuccessfullyWhenAdmin() throws Exception {
        User admin = buildUser("admin@test.com", Role.ADMIN);
        User customer = buildUser("customer2@test.com", Role.CUSTOMER);

        String adminToken = loginAndGetToken("admin@test.com", "passwordTest");

        mockMvc.perform(get("/auth/user/" + customer.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(customer.getId()))
                .andExpect(jsonPath("$.data.email").value("customer2@test.com"));
    }

    @Test
    @DisplayName("Should return 403 when user tries to read another user's profile")
    void shouldReturn403WhenUserReadsAnotherUsersProfile() throws Exception {
        User user1 = buildUser("customer3@test.com", Role.CUSTOMER);
        User user2 = buildUser("customer4@test.com", Role.CUSTOMER);

        String token = loginAndGetToken("customer3@test.com", "passwordTest");

        mockMvc.perform(get("/auth/user/" + user2.getId())
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Should return 404 when requested user does not exist")
    void shouldReturn404WhenRequestedUserDoesNotExist() throws Exception {
        User admin = buildUser("admin2@test.com", Role.ADMIN);
        String adminToken = loginAndGetToken("admin2@test.com", "passwordTest");

        mockMvc.perform(get("/auth/user/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when getting user without token")
    void shouldReturn401WhenGetUserWithoutToken() throws Exception {
        User user = buildUser("customer5@test.com", Role.CUSTOMER);

        mockMvc.perform(get("/auth/user/" + user.getId()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    //  @GetMapping("/user/me")
    @Test
    @DisplayName("Should get current user successfully")
    void shouldGetCurrentUserSuccessfully() throws Exception {
        User user = buildUser("me@test.com", Role.CUSTOMER);

        String token = loginAndGetToken("me@test.com", "passwordTest");

        mockMvc.perform(get("/auth/user/me")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getId()))
                .andExpect(jsonPath("$.data.email").value("me@test.com"))
                .andExpect(jsonPath("$.data.firstName").value("testF"))
                .andExpect(jsonPath("$.data.lastName").value("testL"));
    }

    @Test
    @DisplayName("Should return 401 when getting current user without token")
    void shouldReturn401WhenGetCurrentUserWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/user/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Should return 404 when current user from token does not exist")
    void shouldReturn404WhenCurrentUserDoesNotExist() throws Exception {
        User user = buildUser("ghost@test.com", Role.CUSTOMER);
        String token = loginAndGetToken("ghost@test.com", "passwordTest");

        userRepository.deleteById(user.getId());
        userRepository.flush();

        mockMvc.perform(get("/auth/user/me")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNotFound());
    }




    private UserRegisterReq buildRegisterReq(String email) {
        return new UserRegisterReq(
                "testF",
                "testL",
                email,
                "passwordTest",
                randomPhone()
        );
    }

    private User buildUser(String email, Role role) {
        User user = new User();
        user.setFirstName("testF");
        user.setLastName("testL");
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("passwordTest"));
        user.setPhone(randomPhone());
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




    private String randomPhone() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
