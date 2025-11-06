package org.repairflow.repairflowa.UserServices;

import jakarta.transaction.Transactional;
import org.repairflow.repairflowa.Exception.AppExceptions;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.*;
import org.repairflow.repairflowa.Pojo.UserPojo.User;

import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices implements IUserServices{

    private UserRepository userRepository;

    @Autowired
    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // CREATE USER
    @Transactional
    @Override
    public UserDto createUser(UserRegisterReq userRegisterReq) {
        String email = userRegisterReq.email().trim().toLowerCase();
        if(userRepository.existsByEmail(email)) {
            throw new AppExceptions.DataConflictException("Email already exists");
        }

        User user = new User();
        user.setFirstName(userRegisterReq.firstName());
        user.setLastName(userRegisterReq.lastName());
        user.setEmail(email);
        user.setPhone(userRegisterReq.phone());
        user.setPasswordHash(userRegisterReq.password());

        if(user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        user.setActive(true);
        User SavedUser = userRepository.save(user);
        return UserMapper.toUserDto(SavedUser);
    }

//  READ USER BY ID
    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id));
        return UserMapper.toUserDto(user);
    }

//  READ ALL USER
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

//  READ USER BY PAGE
    @Override
    public Page<UserDto> listUsers(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return userRepository.findAll(pageRequest).map(UserMapper::toUserDto);
    }

//  CUSTOMER/EMPLOYEE UPDATE INFO
    @Transactional
    @Override
    public UserDto updateUserSelf(Long id, UserUpdateReq userUpdateReq) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id));
        if(userUpdateReq.firstName() != null) {
            user.setFirstName(userUpdateReq.firstName());
        }
        if(userUpdateReq.lastName() != null) {
            user.setLastName(userUpdateReq.lastName());
        }
        if(userUpdateReq.phone() != null) {
            user.setPhone(userUpdateReq.phone());
        }
        User userUpdated = userRepository.save(user);
        System.out.println(userUpdated.getFirstName() + " " + userUpdated.getLastName());
        return UserMapper.toUserDto(userUpdated) ;
    }


//    ADMIN UPDATE USER INFO
    @Transactional
    @Override
    public UserDto updateUserAdmin(Long id, UserUpdateAdmin userUpdateAdmin) {
        User user = userRepository.findById(id).orElseThrow(()-> new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id));
        if(userUpdateAdmin.role() != null) {
            user.setRole(userUpdateAdmin.role());
        }
        if(userUpdateAdmin.isActive() != null) {
            user.setActive(userUpdateAdmin.isActive());
        }
        return UserMapper.toUserDto(user);
    }

//    DELETE USER
    @Transactional
    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new AppExceptions.ResourceNotFoundException("User Not Found " + "user ID : " + id);
        }
        userRepository.deleteById(id);
    }



}
