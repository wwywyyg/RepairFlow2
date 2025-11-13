package org.repairflow.repairflowa.Services.AuthServices;

import org.repairflow.repairflowa.Exception.AppExceptions;
import org.repairflow.repairflowa.Exception.Response.UserLoginResponse;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.*;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.Services.JwtServices.JwtServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author guangyang
 * @date 11/11/25 AM3:29
 * @description TODO: method of auth
 */
@Service
public class AuthServices {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtServices jwtServices;
    @Autowired
    public AuthServices(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtServices jwtServices) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtServices = jwtServices;
    }


    // USER REGISTER
    @Transactional
    public UserDto userRegister(UserRegisterReq registerReq) {
        String email = registerReq.email().toLowerCase();
        if(userRepository.existsByEmail(email)) {
            throw new AppExceptions.DataConflictException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setFirstName(registerReq.firstName());
        user.setLastName(registerReq.lastName());
        user.setPhone(registerReq.phone());
        user.setPasswordHash(passwordEncoder.encode(registerReq.password()));

        if(user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }


//    USER LOGIN
    public UserLoginResponse userLogin(UserLoginReq loginReq) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.email(), loginReq.password()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        //JWT
        User user = userRepository.findByEmail(loginReq.email()).orElseThrow( () -> new AppExceptions.ResourceNotFoundException("Wrong User email"));

        String token = jwtServices.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        UserDto userDto = UserMapper.toUserDto(user);
//        User userInfo = (User) authentication.getPrincipal();
        return new  UserLoginResponse(token,userDto);
    }


//    USER UPDATE INFO
    @Transactional
    public UserDto updateUserInfo(Long id, UserUpdateReq updateReq) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id));
        if(updateReq.firstName() != null) {
            user.setFirstName(updateReq.firstName());
        }
        if(updateReq.lastName() != null) {
            user.setLastName(updateReq.lastName());
        }
        if(updateReq.phone() != null) {
            user.setPhone(updateReq.phone());
        }
        User userUpdated = userRepository.save(user);
        return UserMapper.toUserDto(userUpdated) ;

    }

    //read User Info
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id));
        return UserMapper.toUserDto(user);
    }


}
