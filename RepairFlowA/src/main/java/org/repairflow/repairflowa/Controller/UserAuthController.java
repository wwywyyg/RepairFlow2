package org.repairflow.repairflowa.Controller;



import jakarta.validation.Valid;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Pojo.UserPojo.Dto.UserDto.*;
import org.repairflow.repairflowa.Services.UserServices.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserAuthController {
    @Autowired
    private UserServices userServices;


    // Create User
//    @PostMapping("/create")  // localhost:8080/auth/user/register
//    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody  UserRegisterReq userRegisterReq) {
//        UserDto createdUser = userServices.createUser(userRegisterReq);
//        return new ResponseEntity<>(ApiResponse.success("User Created Successfully", createdUser), HttpStatus.OK);
//    }

    // read user
    // read one
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto userDto = userServices.getUser(id);
        return  ResponseEntity.ok(ApiResponse.success(userDto));
    }

    // read by List
    @GetMapping("/list") //http://localhost:8080/auth/user/list?page=0&size=5
    public ResponseEntity<ApiResponse<Page<UserDto>>> listUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Page<UserDto> userDto = userServices.listUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }




    // update User Info by admin
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUserAdmin(@PathVariable Long id, @RequestBody @Valid UserUpdateAdmin userUpdateAdmin) {
        UserDto userDto = userServices.updateUserAdmin(id, userUpdateAdmin);
        return ResponseEntity.ok(ApiResponse.success("admin Updated Successfully", userDto));
    }

    // Delete User

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userServices.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User Delete Success!",null));
    }

    // login
//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody @Valid UserLoginReq userLoginReq) {
//        UserDto userDto = userServices.userLogin(userLoginReq);
//        return ResponseEntity.ok(ApiResponse.success("User Login Successfully", userDto));
//    }

}
