package org.repairflow.repairflowa.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.repairflow.repairflowa.Exception.AppExceptions;
import org.repairflow.repairflowa.Exception.Response.UserLoginResponse;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserDto;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserRegisterReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.Services.AuthServices.AuthServices;
import org.repairflow.repairflowa.Services.JwtServices.JwtServices;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author guangyang
 * @date 3/10/26 19:05
 * @description TODO: Description
 */
@ExtendWith(MockitoExtension.class)
public class UserAuthServices {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServices authServices;

    @Mock
    private JwtServices jwtServices;

    @Mock
    private AuthenticationManager authenticationManager;

    // register
    @Test
    @Disabled("User should register successfully")
    void shouldRegisterSuccessfully() {
        // given
        UserRegisterReq registerReq = buildRegisterReq("Test@123.com");
        when(userRepository.existsByEmail("test@123.com")).thenReturn(false);
        when(passwordEncoder.encode("passwordTest")).thenReturn("encodedPassword");
        User savedUser = buildUser("test@123.com",Role.CUSTOMER,1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserDto result = authServices.userRegister(registerReq);

        //then
        assertNotNull(result);
        assertEquals("testF",result.firstName());
        assertEquals("testL",result.lastName());
        assertEquals("test@123.com",result.email());

        verify(passwordEncoder).encode("passwordTest");
        verify(userRepository).existsByEmail("test@123.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should saved user with excepted fields")
    void shouldSaveUserWithExceptedFields() {
        // given
        UserRegisterReq registerReq = buildRegisterReq("Test@123.com");
        when(userRepository.existsByEmail("test@123.com")).thenReturn(false);
        when(passwordEncoder.encode("passwordTest")).thenReturn("encodedPassword");
        User savedUser = buildUser("test@123.com",Role.CUSTOMER,1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        authServices.userRegister(registerReq);

        //then
        verify(userRepository).save(userCaptor.capture());
        User userToSave = userCaptor.getValue();
        assertEquals("test@123.com",userToSave.getEmail());
        assertEquals("testF",userToSave.getFirstName());
        assertEquals("testL",userToSave.getLastName());
        assertEquals("1234567890",userToSave.getPhone());
        assertEquals("encodedPassword",userToSave.getPassword());
        assertEquals(Role.CUSTOMER,userToSave.getRole());
        assertTrue(userToSave.isEnabled());


    }

    @Test
    @DisplayName("show throw exception when user already exist")
    void shouldThrowExceptionWhenUserAlreadyExist() {
        // given
        UserRegisterReq registerReq = buildRegisterReq("test@123.com");
        when(userRepository.existsByEmail("test@123.com")).thenReturn(true);

        // when
        AppExceptions.DataConflictException ex = assertThrows(AppExceptions.DataConflictException.class, () -> authServices.userRegister(registerReq));

        assertEquals("Email already exists", ex.getMessage());
        verify(userRepository).existsByEmail("test@123.com");
        verify(passwordEncoder,never()).encode(anyString());
        verify(userRepository,never()).save(any(User.class));
    }



    // login
    @Test
    @DisplayName("Should login successfully and return token")
    void shouldLoginSuccessfullyAndReturnToken() {
        UserLoginReq loginReq = buildLoginReq("test@test.com","passwordTest");
        User user = buildUser("test@test.com",Role.CUSTOMER,1L);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtServices.generateToken(user.getEmail(), user.getId(), user.getRole().name()))
                .thenReturn("mock-jwt-token");

        UserLoginResponse result = authServices.userLogin(loginReq);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.token());
        assertEquals("test@test.com", result.user().email());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail("test@test.com");
        verify(jwtServices).generateToken(user.getEmail(), user.getId(), user.getRole().name());


    }


    @Test
    @DisplayName("Should throw exception when authentication fails")
    void shouldThrowExceptionWhenAuthenticationFails() {

        UserLoginReq loginReq = new UserLoginReq("test@test.com", "wrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authServices.userLogin(loginReq));

        verify(authenticationManager).authenticate(any());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found after authentication")
    void shouldThrowExceptionWhenUserNotFound() {

        UserLoginReq loginReq = new UserLoginReq("test@test.com", "passwordTest");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(AppExceptions.ResourceNotFoundException.class,
                () -> authServices.userLogin(loginReq));

        verify(userRepository).findByEmail("test@test.com");
    }

    // ==============================
    // UPDATE USER TEST
    // ==============================

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {

        Long userId = 1L;

        User user = buildUser("test@test.com",Role.CUSTOMER,1L);

        UserUpdateReq req = new UserUpdateReq("newFirst", "newLast", "9999999999");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = authServices.updateUserInfo(userId, req);

        assertEquals("newFirst", result.firstName());
        assertEquals("newLast", result.lastName());
        assertEquals("9999999999", result.phone());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should update only non-null fields")
    void shouldUpdatePartialFields() {

        Long userId = 1L;

        User user = buildUser("test@test.com",Role.CUSTOMER ,1L);

        UserUpdateReq req = new UserUpdateReq("newFirst", null, null);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = authServices.updateUserInfo(userId, req);

        assertEquals("newFirst", result.firstName());
        assertEquals("testL", result.lastName());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing user")
    void shouldThrowExceptionWhenUserNotFoundDuringUpdate() {

        Long userId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserUpdateReq req = new UserUpdateReq("newFirst", "newLast", "999");

        assertThrows(AppExceptions.ResourceNotFoundException.class,
                () -> authServices.updateUserInfo(userId, req));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    // ==============================
    // GET USER TEST
    // ==============================

    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {

        Long userId = 1L;

        User user = buildUser("test@test.com",Role.CUSTOMER,userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDto result = authServices.getUserById(userId);

        assertEquals(userId, result.id());
        assertEquals("test@test.com", result.email());

        verify(userRepository).findById(userId);
    }









    // helper
    private UserRegisterReq buildRegisterReq(String email) {
        return new UserRegisterReq(
                "testF",
                "testL",
                email,
                "passwordTest",
                "1234567890"
        );
    }

    private User buildUser(String email, Role role,Long id) {
        User user = new User();
        user.setId(id);
        user.setId(1L);
        user.setFirstName("testF");
        user.setLastName("testL");
        user.setEmail(email);
        user.setPasswordHash("passwordTest");
        user.setPhone("1234567890");
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private UserLoginReq buildLoginReq(String email, String password) {
        return new UserLoginReq(email, password);
    }



}
