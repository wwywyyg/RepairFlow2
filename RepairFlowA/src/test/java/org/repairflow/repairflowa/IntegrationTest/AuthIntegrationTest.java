package org.repairflow.repairflowa.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserRegisterReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @author guangyang
 * @date 3/9/26 15:24
 * @description TODO: Description
 */
public class AuthIntegrationTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    // register
    @Test
    @DisplayName("Should register user successfully ")
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User register Successfully"))
                .andExpect(jsonPath("$.data.email").value("customer@test.com"))
                .andExpect(jsonPath("$.data.firstName").value("testF"))
                .andExpect(jsonPath("$.data.lastName").value("testL"))
                .andExpect(jsonPath("$.data.active").value(true));

        //Assert DB
        assertEquals(1, userRepository.count());
        User savedUser  = userRepository.findAll().get(0);
        assertEquals("customer@test.com", savedUser.getEmail());
        assertEquals("testF", savedUser.getFirstName());
        assertEquals("testL", savedUser.getLastName());
    }


    @Test
    @DisplayName("Should return conflict when user already Exist")
    void shouldReturnConflictWhenUserAlreadyExist() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");
        UserRegisterReq req2 = buildRegisterReq("customer@test.com");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());


        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));

    }

    @Test
    @DisplayName("should return bad request when email is empty")
    void shouldReturnBadRequestWhenEmailIsEmpty() throws Exception {
        UserRegisterReq req = buildRegisterReq("");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Should return bad request when password is empty")
    void shouldReturnBadRequestWhenPasswordIsEmpty() throws Exception {
        UserRegisterReq req = new UserRegisterReq("first","last","123@test.com","",randomPhone());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "123@123",
            "plainaddress",
            "@domain.com",
            "user@",
            "user@domain",
            "user name@example.com",
    })
    @DisplayName("Should return bad request when email format is invalid")
    void shouldReturnBadRequestWhenEmailFormatIsInvalid(String invalidEmail) throws Exception {
        UserRegisterReq req = buildRegisterReq(invalidEmail);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    // login
    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        UserLoginReq loginReq = new UserLoginReq("customer@test.com", "passwordTest");

        mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login Successfully"));
    }


    @Test
    @DisplayName("Should return unauthorized when user not exist")
    void shouldReturnBadRequestWhenUserNotExist() throws Exception {
        UserLoginReq loginReq = new UserLoginReq("customer@test.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return unauthorized when password incorrect")
    void shouldReturnUnauthorizedWhenPasswordIncorrect() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        UserLoginReq loginReq = new UserLoginReq("customer@test.com", "password");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Should return unauthorized request when password is empty")
    void shouldReturnUnauthorizedWhenPasswordIsEmpty() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        UserLoginReq loginReq = new UserLoginReq("customer@test.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should bad request when email is empty")
    void shouldReturnUnauthorizedWhenEmailIsEmpty() throws Exception {
        UserRegisterReq req = buildRegisterReq("customer@test.com");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        UserLoginReq loginReq = new UserLoginReq("", "passwordTest");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPasswordHash("passwordTest");
        user.setPhone(randomPhone());
        user.setRole(role);
        user.setActive(true);
        return userRepository.saveAndFlush(user);
    }



    private String randomPhone() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
