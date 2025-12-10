package org.repairflow.repairflowa.Controller;

import jakarta.validation.Valid;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Exception.Response.UserLoginResponse;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserDto;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserLoginReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserRegisterReq;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.UserUpdateReq;

import org.repairflow.repairflowa.Services.AuthServices.AuthServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author guangyang
 * @date 11/11/25 AM4:21
 * @description TODO: Description
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthServices authServices;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserRegisterReq userRegisterReq) {
        UserDto user = authServices.userRegister(userRegisterReq);
//        return new ResponseEntity<>(ApiResponse.success("User register Successfully", user), HttpStatus.OK);
        return ResponseEntity.ok(ApiResponse.success("User register Successfully",user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginReq userLoginReq) {
        UserLoginResponse userLoginResponse = authServices.userLogin(userLoginReq);
//        return new ResponseEntity<>(ApiResponse.success("Login success! Token: " + userLoginResponse.token(), userLoginResponse.user()), HttpStatus.OK);
        return ResponseEntity.ok(ApiResponse.success("Login Successfully",userLoginResponse));
    }

    // Update User
    @PreAuthorize("hasRole('ADMIN') or @userGuard.isSelf(#id)")
    @PutMapping("/user/update/{id}") //http://localhost:8080/auth/user/update/{id}
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateReq userUpdateReq) {
        UserDto userDto = authServices.updateUserInfo(id, userUpdateReq);
        return ResponseEntity.ok(ApiResponse.success("User Updated Successfully", userDto));
    }

    // Read User
    @PreAuthorize("hasRole('ADMIN') or @userGuard.isSelf(#id)")
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
        UserDto user = authServices.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("user retrieved successfully",user));
    }

}
